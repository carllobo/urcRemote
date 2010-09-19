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
 
package carl.urc.common.micro.network;

import java.io.IOException;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import carl.urc.common.network.client.ConnectionInitiator;
import carl.urc.common.network.client.NetworkConnector;

public abstract class GenericMicroNetworkConnector extends NetworkConnector {
	
	protected Connection connection;
	
	protected GenericMicroNetworkConnector(ConnectionInitiator connectionClient) {
		super(connectionClient);
	}

	protected final void platformCloseConnection() throws IOException {
		if(connection != null) connection.close();
	}
	
	protected final void platformOpen(String addr, String channel) throws IOException {
		String connectionUrl = getConnectionUrl(addr, channel);
		connectionInitiator.connectStep("Connecting to: " + connectionUrl);
		connection = Connector.open(connectionUrl);
	}

	protected abstract String getConnectionUrl(String addr, String channel);
}
