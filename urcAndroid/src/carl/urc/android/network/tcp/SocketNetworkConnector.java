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
 
package carl.urc.android.network.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import carl.urc.android.UrcAndroidApp;
import carl.urc.android.network.AndroidNetworkConnector;
import carl.urc.common.j2se.network.tcp.TcpSocketNetworkEndPoint;
import carl.urc.common.network.client.NetworkEndPoint;

public class SocketNetworkConnector extends AndroidNetworkConnector {
	
	private Socket socket;

	public SocketNetworkConnector(UrcAndroidApp application) {
		super(application);
	}

	@Override
	protected NetworkEndPoint createNetworkEndPoint() {
		return new TcpSocketNetworkEndPoint(socket);
	}

	@Override
	protected void platformCloseConnection() throws IOException {
		if(socket != null) socket.close();
	}

	@Override
	protected void platformOpen(String addr, String channel)
			throws IOException {
		SocketAddress remoteAddr = new InetSocketAddress(addr, Integer.parseInt(channel));
		socket = new Socket();
		socket.connect(remoteAddr);
	}

}
