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
 
package carl.urc.common.network.method;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import carl.urc.common.network.UrcNetworkMethod;

public class KeyboardMethod extends UrcNetworkMethod {
	
	public static final int KEY_BACKSPACE = -1;

	public static final int KEY_ENTER = 10;

	public static final int KEY_CONTROL = -2;

	public static final int KEY_DPAD_DOWN = -3;

	public static final int KEY_DPAD_LEFT = -4;

	public static final int KEY_DPAD_RIGHT = -5;

	public static final int KEY_DPAD_UP = -6;

	public static final int KEY_INVALID = Integer.MIN_VALUE;

	public static final UrcNetworkMethod DEFAULT = new KeyboardMethod(KEY_INVALID);

	private static final byte MY_HEADER = 3;

	private int unicode;

	public KeyboardMethod(int unicode) {
		super(MY_HEADER);
		this.unicode = unicode;
	}

	protected UrcNetworkMethod readPayload(DataInputStream input)
			throws IOException {
		int unicode = input.readInt();
		return new KeyboardMethod(unicode);
	}

	protected void writePayload(DataOutputStream output) throws IOException {
		output.writeInt(unicode);
	}

	public int getUnicode() {
		return unicode;
	}
}
