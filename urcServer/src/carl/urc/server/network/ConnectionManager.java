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
 
package carl.urc.server.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Vector;

import carl.urc.common.network.Middleman;
import carl.urc.common.network.ProtocolResolver;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.server.UrcServer;

public class ConnectionManager implements ServerConnectionCallback {

	private static final ProtocolResolver responseProtocols = createProtocolResolver();

	private UrcServer host;

	private Vector<ServerNetworkEndPoint> endpoints;

	private Vector<Middleman> middleMen;

	private HashMap<ServerNetworkEndPoint, Exception> errors;

	private boolean isShutdown;

	public ConnectionManager(UrcServer urcServer) {
		this.host = urcServer;
		this.endpoints = new Vector<ServerNetworkEndPoint>();
		this.middleMen = new Vector<Middleman>();
		this.errors = new HashMap<ServerNetworkEndPoint, Exception>();
		initConnections();
	}

	private static ProtocolResolver createProtocolResolver() {
		return new ProtocolResolver(UrcServer.responseMethods);
	}

	@SuppressWarnings("unchecked")
	private void initConnections() {
		for (String connectionServerClass1 : host.getServers()) {
			String connectionServerClass = "carl.urc.server.network."
					+ connectionServerClass1 + ".Server";
			Class<ServerNetworkEndPoint> c;
			try {
				c = (Class<ServerNetworkEndPoint>) Class
						.forName(connectionServerClass);
				ServerNetworkEndPoint ep = c.newInstance();
				ep.setConnectionCallback(this);
				try {
					ep.listen();
				} catch (Exception e) {
					errors.put(ep, e);
					host.showError("Can't start " + ep.getName(), e);
				}
				endpoints.add(ep);
			} catch (Throwable e1) {
				host.showError("Server not found " + connectionServerClass, e1);
			}
		}
	}
	
	public String getServerInformation() {
		StringWriter s = new StringWriter();
		PrintWriter w = new PrintWriter(s);
		for (ServerNetworkEndPoint ep : endpoints) {
			w.print(ep.getName());
			if (ep.isFunctional()) {
				w.println(" working");
				ep.writeConnectionInfo(w);
			} else {
				w.println(" not working");
				Exception e = errors.get(ep);
				w.println(e);
			}
			w.println("-------------------------");
		}
		w.println();
		w.println("Active Connections:");
		for (Middleman man : middleMen) {
			w.println(man.getEndPoint().getRemoteConnectionInfo());
		}
		w.close();
		return s.toString();
	}

	@Override
	public void onAccept(NetworkEndPoint ep) {
		Middleman mm;
		try {
			mm = host.createMiddleman(ep, responseProtocols);
			host.showMessage("Received connection from "
					+ ep.getRemoteConnectionInfo());
			middleMen.add(mm);
		} catch (IOException e) {
			host.showError("Error initializing client connection", e);
		}
	}

	public void onConnectionClose(Middleman mm) {
		middleMen.remove(mm);
	}

	@Override
	public void onError(ServerNetworkEndPoint server, Exception e) {
		if (isShutdown)
			return;
		host
				.showError("Error accepting " + server.getName()
						+ " connection", e);
	}

	public void shutdown() {
		isShutdown = true;
		for (Middleman mm : middleMen) {
			mm.close();
		}
		for (ServerNetworkEndPoint ep : endpoints) {
			ep.shutdown();
		}
	}
}
