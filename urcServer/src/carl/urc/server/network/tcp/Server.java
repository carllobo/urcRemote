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
 
package carl.urc.server.network.tcp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

import carl.urc.common.j2se.network.tcp.TcpSocketNetworkEndPoint;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.server.UrcServerPreferences;
import carl.urc.server.network.GenericServer;
import carl.urc.server.network.ServerNetworkEndPoint;

public class Server extends GenericServer<ServerSocket> implements ServerNetworkEndPoint, Runnable {

	public Server() {
		super("TCP");
	}

	@Override
	public void listen() throws Exception {
		ServerSocket serverSocket = new ServerSocket(UrcServerPreferences.preferencePort, 3);
		startServer(serverSocket);
	}

	@Override
	public void writeConnectionInfo(PrintWriter w) {
		w.println("Listening on:");
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface currentInterface = interfaces.nextElement();

				Enumeration<InetAddress> addresses = currentInterface
						.getInetAddresses();

				while (addresses.hasMoreElements()) {
					InetAddress currentAddress = addresses.nextElement();
					String url = "socket://" + currentAddress.getHostAddress()
							+ ":" + UrcServerPreferences.preferencePort;
					w.println(url);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected NetworkEndPoint acceptEndPoint() throws Exception {
		Socket s = connectionNotifier.accept();
		return new TcpSocketNetworkEndPoint(s);
	}

	@Override
	protected void closeConnectionNotifier() {
		try {
			connectionNotifier.close();
		} catch (Exception e) {
			callback.onError(this, e);
		}
	}
}
