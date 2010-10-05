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
 
package carl.urc.server.network.bluetooth.spp;

import java.util.Enumeration;

import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import carl.urc.common.micro.network.GenericMicroNetworkEndPoint;
import carl.urc.common.micro.network.bluetooth.BluetoothMicroCommon;
import carl.urc.common.network.bluetooth.BluetoothCommonPreferences;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.server.network.ServerNetworkEndPoint;
import carl.urc.server.network.bluetooth.BluetoothBaseServer;

public class Server extends BluetoothBaseServer<StreamConnectionNotifier> implements
		ServerNetworkEndPoint, Runnable {

	private boolean setupChannel;

	public Server() {
		this("BT-SPP");
	}
	
	protected Server(String name) {
		super(name, "btspp");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected NetworkEndPoint acceptEndPoint() throws Exception {
		if(! setupChannel) {
			ServiceRecord record = LocalDevice.getLocalDevice().getRecord(connectionNotifier);
	
			DataElement protocolDescriptorList = record.getAttributeValue(0x0004);
			Enumeration protocolDescriptorListElems = (Enumeration) protocolDescriptorList
					.getValue();
	
			// Should be L2Cap stuff
			protocolDescriptorListElems.nextElement();
			
			DataElement rfcomm = (DataElement) protocolDescriptorListElems
					.nextElement();
			Enumeration rfcommElems = (Enumeration) rfcomm.getValue();
			rfcommElems.nextElement(); // UUID
			DataElement rfcommChannelElement = (DataElement) rfcommElems.nextElement();
			DataElement newChannelElement = new DataElement(rfcommChannelElement
					.getDataType(), Long
					.parseLong(BluetoothCommonPreferences.preferenceDefaultChannel));
			rfcomm.removeElement(rfcommChannelElement);
			rfcomm.insertElementAt(newChannelElement, 1);
			setupChannel = true;
		}

		StreamConnection c = connectionNotifier.acceptAndOpen();
		return new GenericMicroNetworkEndPoint(c, BluetoothMicroCommon.getRemoteDevice(c));
	}
}
