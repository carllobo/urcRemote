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
 
package carl.urc.j2me.network.bluetooth.l2cap;

import javax.bluetooth.L2CAPConnection;

import carl.urc.common.micro.network.GenericMicroNetworkConnector;
import carl.urc.common.micro.network.bluetooth.l2cap.BtL2CapEndPoint;
import carl.urc.common.network.bluetooth.BluetoothCommon;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.j2me.UrcMidlet;

public class BluetoothL2CapNetworkConnector extends GenericMicroNetworkConnector {

	public BluetoothL2CapNetworkConnector(UrcMidlet application) {
		super(application);
	}

	protected String getConnectionUrl(String addr, String channel) {
		return "btl2cap://" + addr + ":" + channel + BluetoothCommon.getAdditionalConnectionInfo(false);
	}

	protected NetworkEndPoint createNetworkEndPoint() {
		return new BtL2CapEndPoint((L2CAPConnection) connection);
	}

}
