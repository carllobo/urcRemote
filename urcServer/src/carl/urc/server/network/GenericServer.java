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

import carl.urc.common.network.client.NetworkEndPoint;

public abstract class GenericServer<T> implements ServerNetworkEndPoint, Runnable {

	protected ServerConnectionCallback callback;
	private Thread acceptThread;
	private boolean functional = false;
	private volatile boolean run = true;
	private String name;
	
	protected T connectionNotifier;
	
	protected GenericServer(String name) {
		this.name = name;
	}

	protected final void startServer(T connectionNotifier) throws Exception {
		this.connectionNotifier = connectionNotifier;
		acceptThread = new Thread(this);
		acceptThread.setName(getName() + " Accept");
		acceptThread.start();
		functional = true;
	}


	@Override
	public final void setConnectionCallback(ServerConnectionCallback connectionManager) {
		this.callback = connectionManager;
	}

	@Override
	public final void shutdown() {
		run = false;
		if (acceptThread != null)
			acceptThread.interrupt();
		closeConnectionNotifier();
	}

	@Override
	public final void run() {
		while (run) {
			try {
				NetworkEndPoint ep = acceptEndPoint();
				callback.onAccept(ep);
			} catch (Exception e) {
				callback.onError(this, e);
			}
		}
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final boolean isFunctional() {
		return functional;
	}

	protected abstract void closeConnectionNotifier();
	
	protected abstract NetworkEndPoint acceptEndPoint() throws Exception;

}
