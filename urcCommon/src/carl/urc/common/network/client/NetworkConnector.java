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
 
package carl.urc.common.network.client;

import java.io.IOException;

import carl.urc.common.network.Middleman;

public abstract class NetworkConnector implements Runnable {

	protected Thread worker;
	protected Middleman middleman;
	protected ConnectionInitiator connectionInitiator;
	private String addr;
	private String channel;

	protected NetworkConnector(ConnectionInitiator connectionClient) {
		this.connectionInitiator = connectionClient;
	}

	public void connectAsync(String addr, String channel) {
		this.addr = addr;
		this.channel = channel;
		worker = new Thread(this);
		worker.start();
	}

	public void run() {
		try {
			platformOpen(addr, channel);
			connectionInitiator.connectStep("Connected");
			NetworkEndPoint ep = createNetworkEndPoint();
			connectionInitiator.connectStep("End point created");
			middleman = connectionInitiator.createMiddleman(ep);
			connectionInitiator.connectStep("Middleman created");
			middleman.startHandShake();
			connectionInitiator.connectStep("Handshake sent");
		} catch (Exception e) {
			connectionInitiator.onConnectionRefused(e);
		}
	}

	public Middleman getMiddleman() {
		return middleman;
	}

	public void close() {
		try {
			platformCloseConnection();
		} catch (IOException e) {
			connectionInitiator.connectStep("Error closing connection" + e);
		}
		if (worker != null)
			worker.interrupt();
		if (middleman != null)
			middleman.close();
	}
	
	protected abstract void platformCloseConnection() throws IOException;

	protected abstract NetworkEndPoint createNetworkEndPoint();

	protected abstract void platformOpen(String addr, String channel) throws IOException;
}
