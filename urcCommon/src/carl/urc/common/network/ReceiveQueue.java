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
 
package carl.urc.common.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

class ReceiveQueue extends Thread {

	private Middleman middleman;
	private DataInputStream input;
	private volatile boolean run = true;

	ReceiveQueue(Middleman middleman, DataInputStream input) {
		this.middleman = middleman;
		this.input = input;

		start();
	}

	public void run() {
		try {
			while (run) {
				byte header = input.readByte();
				if(header == -1) break;
				UrcNetworkMethod method = middleman.resolve(header);
				method = method.readPayload(input);
				middleman.dispatchMethod(method);
			}
		} catch (Exception e) {
			middleman.showError("Read Error", e);
		}
		middleman.close();
	}

	public void interrupt() {
		run = false;
		try {
			((InputStream) input).close();
		} catch (IOException e) {
			middleman.showError("Input Close Error", e);
		}
		super.interrupt();
	}
}
