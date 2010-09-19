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
 
package carl.urc.server.network.bluetooth.l2cap.test;

import java.util.Enumeration;

import javax.bluetooth.DataElement;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import carl.urc.common.network.bluetooth.BluetoothCommon;
import carl.urc.common.network.bluetooth.BluetoothCommonPreferences;

public class Server extends carl.urc.server.network.bluetooth.l2cap.Server {
	
	public Server() {
		super("BT-L2CAP-TEST");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void listen() throws Exception {
		String aci = BluetoothCommon.getAdditionalConnectionInfo(true);
		aci = aci.replaceAll("encrypt=true", "encrypt=false");
		String connectionString = "btl2cap://localhost:"
				+ Long.parseLong(BluetoothCommonPreferences.preferenceDefaultPSM, 16)
				+ aci;
		System.err.println(connectionString);
		Connection con = Connector.open(connectionString);
		ServiceRecord record = LocalDevice.getLocalDevice().getRecord(con);

		DataElement protocolDescriptorList = record.getAttributeValue(0x0004);
		Enumeration protocolDescriptorListElems = (Enumeration) protocolDescriptorList
				.getValue();

		// Should be L2Cap stuff
		DataElement l2cap = (DataElement) protocolDescriptorListElems
				.nextElement();
		Enumeration l2capElems = (Enumeration) l2cap.getValue();
		l2capElems.nextElement(); // UUID
		DataElement l2capPsmElement = (DataElement) l2capElems.nextElement();
		DataElement newPsmElement = new DataElement(l2capPsmElement
				.getDataType(), Long
				.parseLong(BluetoothCommonPreferences.preferenceDefaultPSM, 16));
		l2cap.removeElement(l2capPsmElement);
		l2cap.insertElementAt(newPsmElement, 1);

		startServer((L2CAPConnectionNotifier) con);
	}
}
