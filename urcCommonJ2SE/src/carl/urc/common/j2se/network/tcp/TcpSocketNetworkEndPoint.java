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
 
package carl.urc.common.j2se.network.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import carl.urc.common.network.client.AbstractNetworkEndPoint;

public class TcpSocketNetworkEndPoint extends AbstractNetworkEndPoint {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;

	public TcpSocketNetworkEndPoint(Socket s) {
		super(s.getRemoteSocketAddress().toString());
		socket = s;
	}

	@Override
	public DataInputStream getDataInput() throws IOException {
		if (input == null) {
			InputStream in = socket.getInputStream();
			input = new DataInputStream(in);
		}
		return input;
	}

	@Override
	public DataOutputStream getDataOuput() throws IOException {
		if (output == null) {
			OutputStream out = socket.getOutputStream();
			output = new DataOutputStream(out);
		}
		return output;
	}
}
