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

import java.util.Hashtable;

public class ProtocolResolver {

	private Hashtable methodMap;

	public ProtocolResolver(UrcNetworkMethod[] responseMethods) {
		if(responseMethods != null && responseMethods.length > 0) {
		methodMap = new Hashtable();
		for (int i = 0; i < responseMethods.length; ++i) {
			methodMap.put(Integer.toString(responseMethods[i].getHeader()),
					responseMethods[i]);
		}
		}
	}

	public UrcNetworkMethod resolve(int header) {
		UrcNetworkMethod m = (UrcNetworkMethod) methodMap.get(Integer.toString(header));
		if (m == null)
			throw new ProtocolException((byte) header);
		return m;
	}
	
	public boolean isFullDuplex() {
		return (methodMap != null);
	}

}
