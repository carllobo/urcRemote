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
 
package carl.urc.server;

import java.awt.AWTException;
import java.io.IOException;

import carl.urc.common.CommonPreferences;
import carl.urc.common.host.ApplicationHost;
import carl.urc.common.network.Middleman;
import carl.urc.common.network.ProtocolException;
import carl.urc.common.network.ProtocolResolver;
import carl.urc.common.network.UrcNetworkMethod;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.common.network.method.AuthenticationMethod;
import carl.urc.common.network.method.AuthenticationResponseMethod;
import carl.urc.common.network.method.ConnectionHandshakeMethod;
import carl.urc.common.network.method.KeyboardMethod;
import carl.urc.common.network.method.MessageMethod;
import carl.urc.common.network.method.MouseClickMethod;
import carl.urc.common.network.method.MouseMoveMethod;
import carl.urc.common.network.method.MouseWheelMethod;
import carl.urc.server.network.ConnectionManager;
import carl.urc.server.ui.RobotManager;
import carl.urc.server.ui.UIManager;

public class UrcServer implements ApplicationHost {

	private ConnectionManager connectionManager;
	protected UIManager uiManager;
	private RobotManager robot;

	public static final UrcNetworkMethod[] responseMethods = new UrcNetworkMethod[] {
		AuthenticationMethod.DEFAULT,
		KeyboardMethod.DEFAULT,
		MessageMethod.DEFAULT,
		MouseClickMethod.DEFAULT,
		MouseMoveMethod.DEFAULT,
		MouseWheelMethod.DEFAULT,
		ConnectionHandshakeMethod.DEFAULT };

	@Override
	public void dispatch(UrcNetworkMethod method, Middleman middleman) {
		if (method instanceof MouseMoveMethod)
			robot.moveMouse((MouseMoveMethod) method);
		else if (method instanceof MouseClickMethod)
			robot.mouseClick((MouseClickMethod) method);
		else if (method instanceof MouseWheelMethod)
			robot.mouseWheel((MouseWheelMethod) method, robot);
		else if (method instanceof KeyboardMethod)
			keyboard((KeyboardMethod) method);
		else if (method instanceof MessageMethod)
			this.message((MessageMethod) method);
		else if (method instanceof AuthenticationMethod)
			authenticateRequest((AuthenticationMethod) method, middleman);
		else if (method instanceof ConnectionHandshakeMethod)
			onHandshake((ConnectionHandshakeMethod) method, middleman);
		else
			throw new ProtocolException(method);
	}
	
	protected void run() throws AWTException {
		uiManager = new UIManager(this);
		connectionManager = new ConnectionManager(this);
		robot = new RobotManager(this);
	}

	public String getServerInformation() {
		return connectionManager.getServerInformation();
	}

	private void onHandshake(ConnectionHandshakeMethod method,
			Middleman middleman) {
		// NOOP		
	}

	private void keyboard(KeyboardMethod method) {
		robot.keyboard(method, UrcServerPreferences.preferenceAltKeyboard);
	}

	private void authenticateRequest(AuthenticationMethod method,
			Middleman middleman) {
		AuthenticationResponseMethod response;
		if (CommonPreferences.preferenceAuthenticate) {
			response = new AuthenticationResponseMethod(CommonPreferences.preferencePassword
					.equals(method.getPassword()));
		} else {
			response = new AuthenticationResponseMethod(true);
		}
		middleman.sendMethod(response);
	}

	private void message(MessageMethod method) {
		uiManager.showMessage(method.getMessage());
	}

	@Override
	public void showError(String message, Throwable e) {
		showMessage(message + ": " + e.toString());
	}

	@Override
	public void showMessage(String message) {
		uiManager.showMessage(message);
	}

	public void exit() {
		if(robot != null) {
			robot.shutdown();
		}
		if (connectionManager != null)
			try {
				connectionManager.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (uiManager != null)
			try {
				uiManager.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		System.exit(0);
	}

	@Override
	public void onConnectionClose(Middleman mm) {
		connectionManager.onConnectionClose(mm);
		showMessage("Peer connection terminated");
	}

	public Middleman createMiddleman(NetworkEndPoint ep,
			ProtocolResolver responseprotocols) throws IOException {
		return new Middleman(ep, this, responseprotocols);
	}

	public String[] getServers() {
		return UrcServerPreferences.preferenceConnectionServers;
	}

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		UrcServer server = new UrcServer();
		server.run();
	}
}
