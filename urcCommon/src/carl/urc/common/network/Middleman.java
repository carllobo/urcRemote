/*
 *    This file is part of urcRemote. urcRemote turns your mobile phone into
 *    a touch-pad and keyboard for your computer.
 *
 *    Copyright  2010 Carl Lobo <carllobo@gmail.com>
 *
 *    urcRemote is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    urcRemote is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with urcRemote.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package carl.urc.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import carl.urc.common.CommonPreferences;
import carl.urc.common.host.ApplicationHost;
import carl.urc.common.network.client.ConnectionInitiator;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.common.network.method.AuthenticationMethod;
import carl.urc.common.network.method.AuthenticationResponseMethod;
import carl.urc.common.network.method.ConnectionHandshakeMethod;

public class Middleman {

	private NetworkEndPoint endPoint;

	private ApplicationHost host;

	private SendQueue sendQueue;

	private ReceiveQueue receiveQueue;

	private ProtocolResolver resolver;

	private boolean interrupted = false;

	private boolean authenticated;

	private boolean fromServerAccept;

	private ConnectionInitiator connectionCallback;

	public Middleman(NetworkEndPoint endPoint, ApplicationHost host,
			ProtocolResolver resolver) throws IOException {
		this.endPoint = endPoint;
		this.host = host;
		this.resolver = resolver;
		this.fromServerAccept = true;

		init();
	}

	public Middleman(NetworkEndPoint endPoint, ApplicationHost host,
			ProtocolResolver resolver, ConnectionInitiator clientCallback)
			throws IOException {
		
		this.endPoint = endPoint;
		this.host = host;
		this.resolver = resolver;
		this.fromServerAccept = false;
		this.connectionCallback = clientCallback;

		init();
	}

	private void init() throws IOException {
		if (resolver.isFullDuplex())
			receiveQueue = new ReceiveQueue(this, createDataInputStream(endPoint
					.getDataInput()));
		if (!fromServerAccept)
			sendQueue = new SendQueue(this, createDataOutputStream(endPoint
					.getDataOuput()));
	}

	protected DataOutputStream createDataOutputStream(DataOutputStream dataOuput) {
		return dataOuput;
	}

	protected DataInputStream createDataInputStream(DataInputStream dataInput) {
		return dataInput;
	}

	void dispatchMethod(UrcNetworkMethod method) {
		if (fromServerAccept) {
			if (method instanceof ConnectionHandshakeMethod) {
				boolean fullDuplex = ((ConnectionHandshakeMethod) method)
						.isFullDuplex();
				if (fullDuplex) {
					try {
						sendQueue = new SendQueue(this,
								createDataOutputStream(endPoint.getDataOuput()));
					} catch (IOException e) {
						showError("Error opening send stream", e);
					}
				}
			} else if (!authenticated
					&& CommonPreferences.preferenceAuthenticate) {
				if (method instanceof AuthenticationResponseMethod)
					onAuthenticateReceived((AuthenticationResponseMethod) method);
				if (!(authenticated || method instanceof AuthenticationMethod))
					throw new ProtocolException("Not authenticated");
			}
		}
		host.dispatch(method, this);
	}

	public void clearOutputQueue() {
		sendQueue.clear();
	}

	public void sendMethod(UrcNetworkMethod method) {
		if (method instanceof AuthenticationResponseMethod)
			authenticated = ((AuthenticationResponseMethod) method).authenticated;
		sendQueue.push(method);
	}

	public void close() {
		if (!interrupted) {
			interrupted = true;
			if (sendQueue != null)
				sendQueue.interrupt();

			if (receiveQueue != null)
				receiveQueue.interrupt();
			host.onConnectionClose(this);
		}
	}

	void showError(String message, Exception e) {
		if (!interrupted) {
			host.showError(message, e);
		}
	}

	UrcNetworkMethod resolve(int header) {
		return resolver.resolve(header);
	}

	public NetworkEndPoint getEndPoint() {
		return endPoint;
	}

	public void startHandShake() {
		sendMethod(new ConnectionHandshakeMethod(resolver.isFullDuplex()));
		if (CommonPreferences.preferenceAuthenticate) {
			sendMethod(new AuthenticationMethod(
					CommonPreferences.preferencePassword));
		} else {
			connectionCallback.onConnect();
		}
	}

	private void onAuthenticateReceived(AuthenticationResponseMethod method) {
		authenticated = method.authenticated;
		if (!authenticated)
			connectionCallback.onAuthenticationFailed();
		else
			connectionCallback.onConnect();
	}
}
