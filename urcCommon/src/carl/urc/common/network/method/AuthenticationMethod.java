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

public class AuthenticationMethod extends UrcNetworkMethod {
	
	public static final UrcNetworkMethod DEFAULT = new AuthenticationMethod(null);
	
	private static final byte METHOD_HEADER = 1;

	private String password;

	public AuthenticationMethod(String password) {
		super(METHOD_HEADER);
		this.password = password;
	}

	protected UrcNetworkMethod readPayload(DataInputStream input)
			throws IOException {
		String password = input.readUTF();
		return new AuthenticationMethod(password);
	}

	protected void writePayload(DataOutputStream output) throws IOException {
		output.writeUTF(password);
	}

	public String getPassword() {
		return password;
	}
}
