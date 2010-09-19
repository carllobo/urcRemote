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

public class AuthenticationResponseMethod extends UrcNetworkMethod {
	
	public static final UrcNetworkMethod DEFAULT = new AuthenticationResponseMethod(false);
	
	private static final byte MY_HEADER = 2;

	public boolean authenticated;

	public AuthenticationResponseMethod(boolean authenticated) {
		super(MY_HEADER);
		this.authenticated = authenticated;
	}

	protected UrcNetworkMethod readPayload(DataInputStream input)
			throws IOException {
		boolean authenticated = input.readBoolean();
		return new AuthenticationResponseMethod(authenticated);
	}

	protected void writePayload(DataOutputStream output) throws IOException {
		output.writeBoolean(authenticated);
	}
}
