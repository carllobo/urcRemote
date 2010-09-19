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
 
package carl.urc.common.micro.network.bluetooth.l2cap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.L2CAPConnection;

import carl.urc.common.micro.network.bluetooth.BluetoothMicroCommon;
import carl.urc.common.network.client.AbstractNetworkEndPoint;

public class BtL2CapEndPoint extends AbstractNetworkEndPoint {

	private L2CAPConnection connection;
	private DataInputStream input;
	private DataOutputStream output;

	public BtL2CapEndPoint(L2CAPConnection connection) {
		super(BluetoothMicroCommon.getRemoteDevice(connection));
		this.connection = connection;
	}

	public DataInputStream getDataInput() throws IOException {
		if (input == null) {
			InputStream in = new L2CapDepacketizer(connection);
			input = new DataInputStream(in);
		}
		return input;
	}

	public DataOutputStream getDataOuput() throws IOException {
		if (output == null) {
			OutputStream out = new L2CapPacketizer(connection);
			output = new DataOutputStream(out);
		}
		return output;
	}
}
