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
 
package carl.urc.common.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import carl.urc.common.host.ApplicationHost;
import carl.urc.common.network.Middleman;
import carl.urc.common.network.ProtocolResolver;
import carl.urc.common.network.client.ConnectionInitiator;
import carl.urc.common.network.client.NetworkEndPoint;

public class TeeingMiddleman extends Middleman {

	private boolean slowSends;

	public TeeingMiddleman(NetworkEndPoint client, ApplicationHost host,
			ProtocolResolver resolver, ConnectionInitiator clientCallback, boolean slowSends)
			throws IOException {
		super(client, host, resolver, clientCallback);
		this.slowSends = slowSends;
	}

	public TeeingMiddleman(NetworkEndPoint client, ApplicationHost host,
			ProtocolResolver resolver, boolean slowSends) throws IOException {
		super(client, host, resolver);
		this.slowSends = slowSends;
	}

	protected DataInputStream createDataInputStream(DataInputStream dataInput) {
		return new DataInputStream(new TeeingInputStream(dataInput));
	}

	protected DataOutputStream createDataOutputStream(DataOutputStream dataOuput) {
		return new TeeingOutputStream(dataOuput, slowSends);
	}
}
