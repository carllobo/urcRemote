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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TeeingOutputStream extends DataOutputStream {

	private static final long LATENCY = 50;
	
	private boolean slowSends;

	private boolean console;

	public void write(byte[] b, int off, int len) throws IOException {
		if(slowSends) {
			try {
				Thread.sleep(LATENCY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.err.println(new String(b, off, len));
		super.write(b, off, len);
	}

	public TeeingOutputStream(OutputStream out, boolean slowSends) {
		super(out);
		this.slowSends = slowSends;
	}

	public TeeingOutputStream() {
		super(System.err);
		console = true;
	}

	public void write(int b) throws IOException {
		if(slowSends) {
			try {
				Thread.sleep(LATENCY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.err.println(b);
		if(! console) super.write(b);
	}

}
