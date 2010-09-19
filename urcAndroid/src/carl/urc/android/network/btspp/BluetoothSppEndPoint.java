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
 
package carl.urc.android.network.btspp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import carl.urc.common.network.client.NetworkEndPoint;

public class BluetoothSppEndPoint implements NetworkEndPoint {
	
	private BluetoothSocket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private String remoteConnectionInfo;

	public BluetoothSppEndPoint(BluetoothSocket socket) {
		this.socket = socket;
		this.remoteConnectionInfo = socket.getRemoteDevice().getAddress();
	}

	@Override
	public DataInputStream getDataInput() throws IOException {
		if(input == null) {
			input = new DataInputStream(socket.getInputStream());
		}
		return input;
	}

	@Override
	public DataOutputStream getDataOuput() throws IOException {
		if(output == null) {
			output = new DataOutputStream(socket.getOutputStream());
		}
		return output;
	}

	@Override
	public String getRemoteConnectionInfo() {
		return remoteConnectionInfo;
	}

}
