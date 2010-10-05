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
 
package carl.urc.server.test;

import java.awt.AWTException;
import java.io.IOException;

import carl.urc.common.network.Middleman;
import carl.urc.common.network.ProtocolResolver;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.common.test.TeeingMiddleman;
import carl.urc.server.UrcServer;

public class UrcServerTest extends UrcServer {

	@Override
	public String getServerInformation() {
		String s = super.getServerInformation();
		s = s + "\n  * Test Servers listen with encrypt=false";
		System.err.println(s);
		return s;
	}

	@Override
	protected void run() throws AWTException {
		super.run();
		uiManager.showServerInformation();
	}

	@Override
	public Middleman createMiddleman(NetworkEndPoint ep,
			ProtocolResolver responseprotocols) throws IOException {
		return new TeeingMiddleman(ep, this, responseprotocols, true);
	}
		
	@Override
	public void showMessage(String message) {
		System.err.println(message);
		super.showMessage(message);
	}
	
	@Override
	public void showError(String message, Throwable e) {
		System.err.print(message + ": ");
		e.printStackTrace();
		super.showError(message, e);
	}

	public static void main(String[] args) throws Throwable {
		UrcServerTest u = new UrcServerTest();
		u.run();
	}

}
