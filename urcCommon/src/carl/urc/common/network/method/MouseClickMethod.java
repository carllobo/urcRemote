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

public class MouseClickMethod extends UrcNetworkMethod {

	public static final byte BUTTON_NONE = 0;
	public static final byte BUTTON_LEFT = 1;
	public static final byte BUTTON_RIGHT = 2;
	public static final byte BUTTON_MIDDLE = 3;

	public static final boolean STATE_UP = false;
	public static final boolean STATE_DOWN = true;

	public static final UrcNetworkMethod DEFAULT = new MouseClickMethod(
			BUTTON_NONE, false);
	
	private static final byte MY_HEADER = 4;

	private byte button;
	private boolean state;

	public MouseClickMethod(byte button, boolean state) {
		super(MY_HEADER);
		this.button = button;
		this.state = state;
	}

	protected UrcNetworkMethod readPayload(DataInputStream input)
			throws IOException {
		byte button = input.readByte();
		boolean state = input.readBoolean();
		return new MouseClickMethod(button, state);
	}

	protected void writePayload(DataOutputStream output) throws IOException {
		output.writeByte(button);
		output.writeBoolean(state);
	}

	public boolean getState() {
		return state;
	}

	public byte getButton() {
		return button;
	}
}
