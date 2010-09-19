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
 
package carl.urc.j2me.test;

import java.io.IOException;

import javax.microedition.lcdui.Displayable;

import carl.urc.common.CommonPreferences;
import carl.urc.common.client.ClientPreferences;
import carl.urc.common.network.Middleman;
import carl.urc.common.network.client.NetworkConnector;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.common.test.TeeingMiddleman;
import carl.urc.j2me.UrcMidlet;
import carl.urc.j2me.network.tcp.SocketNetworkConnector;

public class UrcTestMidlet extends UrcMidlet {

	private final String[] PROTOCOLS;
	private boolean tee;
	private boolean slow;

	public UrcTestMidlet() {
		String[] p = super.getProtocols();
		PROTOCOLS = new String[p.length + 2];
		System.arraycopy(p, 0, PROTOCOLS, 0, p.length);
		PROTOCOLS[p.length] = "slowsocket";
		PROTOCOLS[p.length + 1] = "teesocket";
	}

	protected NetworkConnector getNetworkEndPoint(String protocolType) {
		NetworkConnector client = super.getNetworkEndPoint(protocolType);
		if (client == null) {
			if (protocolType.equals("slowsocket")) {
				slow = true;
			} else if (protocolType.equals("teesocket")) {
				tee = true;
			}
			client = new SocketNetworkConnector(this);
		}
		return client;
	}

	public String getDefaultDevice(String protocolType) {
		String client = null;
		if (protocolType.equals("slowsocket"))
			client = ClientPreferences.preferenceDefaultIp;
		else if (protocolType.equals("teesocket"))
			client = ClientPreferences.preferenceDefaultIp;
		else
			client = super.getDefaultDevice(protocolType);
		return client;
	}

	public String getDefaultChannel(String protocolType) {
		String client = null;
		if (protocolType.equals("slowsocket"))
			client = CommonPreferences.preferenceDefaultPort;
		else if (protocolType.equals("teesocket"))
			client = CommonPreferences.preferenceDefaultPort;
		else
			client = super.getDefaultChannel(protocolType);
		return client;
	}

	public String[] getProtocols() {
		return PROTOCOLS;
	}

	public Middleman createMiddleman(NetworkEndPoint ep) throws IOException {
		if (slow | tee)
			return new TeeingMiddleman(ep, this, resolver, this, slow);
		else
			return super.createMiddleman(ep);
	}

	public void connectStep(String string) {
		showMessage(string);
	}

	protected void showMessageInternal(String message, Displayable next) {
		System.err.println(message);
		super.showMessageInternal(message, next);
		try {
			Thread.sleep(ClientPreferences.preferenceAlertTimeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void showError(String message, Throwable e) {
		System.err.print(message + ": ");
		e.printStackTrace();
		super.showError(message, e);
	}
}
