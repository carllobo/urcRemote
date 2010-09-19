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

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import carl.urc.android.UrcAndroidApp;
import carl.urc.android.network.AndroidNetworkConnector;
import carl.urc.common.network.bluetooth.BluetoothCommon;
import carl.urc.common.network.client.NetworkEndPoint;

public class BluetoothNetworkConnector extends AndroidNetworkConnector {
	
	private BluetoothSocket socket;

	public BluetoothNetworkConnector(UrcAndroidApp application) {
		super(application);
	}

	@Override
	protected NetworkEndPoint createNetworkEndPoint() {
		return new BluetoothSppEndPoint(socket);
	}

	@Override
	protected void platformCloseConnection() throws IOException {
		if(socket != null) socket.close();
	}

	@Override
	protected void platformOpen(String addr, String channel)
			throws IOException {
		BluetoothDevice remote = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addr);
		application.connectStep("Got Device");
		this.socket = remote.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothCommon.uuid));
	}

}
