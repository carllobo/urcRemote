package carl.urc.server.network.bluetooth.spp.androidhack;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

import carl.urc.common.network.bluetooth.BluetoothCommon;
import carl.urc.common.network.bluetooth.BluetoothCommonPreferences;

public class Server extends carl.urc.server.network.bluetooth.spp.Server {

	@Override
	public void listen() throws Exception {
		String connectionString = "btspp://localhost:" + BluetoothCommonPreferences.preferenceDefaultChannel
			+ BluetoothCommon.getAdditionalConnectionInfo(true);
		startServer((StreamConnectionNotifier) Connector.open(connectionString));
	}
}
