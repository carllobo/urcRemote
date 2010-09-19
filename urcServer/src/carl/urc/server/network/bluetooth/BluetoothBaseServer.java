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
 
package carl.urc.server.network.bluetooth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import carl.urc.common.network.bluetooth.BluetoothCommon;
import carl.urc.common.network.bluetooth.BluetoothCommonPreferences;
import carl.urc.server.network.GenericServer;
import carl.urc.server.network.ServerNetworkEndPoint;

public abstract class BluetoothBaseServer<T extends Connection> extends GenericServer<T> implements ServerNetworkEndPoint, Runnable  {

	private String protocol;
	
	protected BluetoothBaseServer(String name, String protocol) {
		super(name);
		this.protocol = protocol;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void listen() throws Exception {
		String connectionString = protocol + "://localhost:" 
				+ BluetoothCommon.uuid + BluetoothCommon.getAdditionalConnectionInfo(true);
		startServer((T) Connector.open(connectionString));
	}

	@Override
	protected void closeConnectionNotifier() {
		try {
			connectionNotifier.close();
		} catch (Exception e) {
			callback.onError(this, e);
		}
	}

	@Override
	public void writeConnectionInfo(PrintWriter w) {
		w.println("Listening on:");
		try {
			w.println(getConnectionString(connectionNotifier));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getConnectionString(Connection connection)
			throws BluetoothStateException {
		LocalDevice ld = LocalDevice.getLocalDevice();
		ServiceRecord record = ld.getRecord(connection);
		int security;
		if (BluetoothCommonPreferences.preferenceAuthenticate) {
			security = (BluetoothCommonPreferences.preferenceEncrypt) ? ServiceRecord.AUTHENTICATE_ENCRYPT
					: ServiceRecord.AUTHENTICATE_NOENCRYPT;
		} else {
			security = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;
		}
		return record.getConnectionURL(security, false);
	}
}
