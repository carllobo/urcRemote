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
 
package carl.urc.j2me;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import carl.urc.common.CommonPreferences;
import carl.urc.common.client.ClientPreferences;
import carl.urc.common.host.ApplicationHost;
import carl.urc.common.network.Middleman;
import carl.urc.common.network.ProtocolResolver;
import carl.urc.common.network.UrcNetworkMethod;
import carl.urc.common.network.bluetooth.BluetoothCommonPreferences;
import carl.urc.common.network.client.ConnectionInitiator;
import carl.urc.common.network.client.NetworkConnector;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.common.network.method.AuthenticationResponseMethod;
import carl.urc.j2me.network.bluetooth.l2cap.BluetoothL2CapNetworkConnector;
import carl.urc.j2me.network.bluetooth.spp.BluetoothSppNetworkConnector;
import carl.urc.j2me.network.tcp.SocketNetworkConnector;
import carl.urc.j2me.ui.KeyboardForm;
import carl.urc.j2me.ui.WelcomeForm;
import carl.urc.j2me.ui.touchpad.MidletTouchPadPreferences;
import carl.urc.j2me.ui.touchpad.TouchPadCanvas;

public class UrcMidlet extends MIDlet implements ApplicationHost, ConnectionInitiator {

	private static final String[] PROTOCOLS = new String[] { "btl2cap", "socket", "btspp" };

	protected ProtocolResolver resolver = getResolver();

	private NetworkConnector connector;

	private String lastChannel;

	private String lastSelectedProtocol;

	private String lastAddr;
	
	private WelcomeForm welcomeForm;

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		if(connector != null) connector.close();
	}

	public ProtocolResolver getResolver() {
		if (resolver == null) {
			if (CommonPreferences.preferenceAuthenticate) {
				resolver = new ProtocolResolver(
						new UrcNetworkMethod[] { AuthenticationResponseMethod.DEFAULT });
			} else {
				resolver = new ProtocolResolver(null);
			}
		}
		return resolver;
	}

	protected void pauseApp() {
		if(connector != null) connector.close();
		switchToWelcome();
	}

	protected void startApp() throws MIDletStateChangeException {
		switchToWelcome();
	}

	protected NetworkConnector getNetworkEndPoint(String protocolType) {
		NetworkConnector client = null;
		if (protocolType.equals("btl2cap"))
			client = new BluetoothL2CapNetworkConnector(this);
		else if (protocolType.equals("socket"))
			client = new SocketNetworkConnector(this);
		else if (protocolType.equals("btspp"))
			client = new BluetoothSppNetworkConnector(this);
		return client;
	}
	
	public String getDefaultDevice(String protocolType) {
		String client = null;
		if (protocolType.equals("btl2cap"))
			client = ClientPreferences.preferenceDefaultDevice;
		else if (protocolType.equals("socket"))
			client = ClientPreferences.preferenceDefaultIp;
		else if (protocolType.equals("btspp"))
			client = ClientPreferences.preferenceDefaultDevice;
		return client;
	}
	
	public String getDefaultChannel(String protocolType) {
		String client = null;
		if (protocolType.equals("btl2cap"))
			client = BluetoothCommonPreferences.preferenceDefaultPSM;
		else if (protocolType.equals("socket"))
			client = CommonPreferences.preferenceDefaultPort;
		else if (protocolType.equals("btspp"))
			client = BluetoothCommonPreferences.preferenceDefaultChannel;
		return client;
	}
	
	public String[] getProtocols() {
		return PROTOCOLS;
	}

	public void doConnection(String addr, String channel,
			String selectedProtocol) throws IOException {
		showMessage("Connecting...");
		this.lastChannel = channel;
		this.lastAddr = addr;
		this.lastSelectedProtocol = selectedProtocol;

		if (connector != null) {
			connector.close();
			connector = null;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		connector = getNetworkEndPoint(selectedProtocol);
		connector.connectAsync(addr, channel);
	}

	public void onConnect() {
		switchToMouse();
	}
	
	public void onConnectionRefused(Exception e) {
		showErrorInternal("Connection refused", e, welcomeForm);
	}

	public void onAuthenticationFailed() {
		showErrorInternal("Authentication Failed", new SecurityException("Authentication Failed"), welcomeForm);
	}

	public void onConnectionClose(Middleman mm) {
		showMessage("Disconnected from "
				+ mm.getEndPoint().getRemoteConnectionInfo()
				+ ", reconnecting...");
		try {
			doConnection(lastAddr, lastChannel, lastSelectedProtocol);
		} catch (IOException e) {
			showError("Can't reconnect", e);
		}
	}

	public void exit() {
		notifyDestroyed();
	}

	public void pause() {
		notifyPaused();
	}

	public void switchToKeyboard() {
		Display d = Display.getDisplay(this);
		KeyboardForm kf = new KeyboardForm(this, connector.getMiddleman());
		d.setCurrent(kf);
	}

	public void switchToMouse() {
		Display d = Display.getDisplay(this);
		TouchPadCanvas mc = new TouchPadCanvas(this, connector.getMiddleman());
		d.setCurrent(mc);
	}

	public void switchToWelcome() {
		Display d = Display.getDisplay(this);
		if(welcomeForm == null)
			welcomeForm = new WelcomeForm(this);
		d.setCurrent(welcomeForm);
	}
	
	public void vibrate() {
		Display.getDisplay(this).vibrate(MidletTouchPadPreferences.preferenceVibrationTime);
	}

	public void dispatch(UrcNetworkMethod method, Middleman middleman) {
		
	}

	public void showError(String message, Throwable e) {
		showErrorInternal(message, e, null);
	}
	
	private void showErrorInternal(String message, Throwable e, Displayable next) {
		showMessageInternal(message + ": " + e.toString(), next);
	}

	public void showMessage(String message) {
		showMessageInternal(message, null);
	}

	protected void showMessageInternal(String message, Displayable next) {
		Alert a = new Alert(message);
		a.setTimeout(ClientPreferences.preferenceAlertTimeout);
		a.setString(message);
		if (next == null)
			Display.getDisplay(this).setCurrent(a);
		else
			Display.getDisplay(this).setCurrent(a, next);
	}

	public Middleman createMiddleman(NetworkEndPoint ep)
			throws IOException {
		return new Middleman(ep, this, resolver, this);
	}
	
	public void connectStep(String string) {
		
	}
}
