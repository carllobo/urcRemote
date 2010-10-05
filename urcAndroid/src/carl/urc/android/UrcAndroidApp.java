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
 
package carl.urc.android;

import java.io.IOException;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import carl.urc.android.network.AndroidNetworkConnector;
import carl.urc.android.network.btspp.BluetoothNetworkConnector;
import carl.urc.android.network.tcp.SocketNetworkConnector;
import carl.urc.common.CommonPreferences;
import carl.urc.common.client.ClientPreferences;
import carl.urc.common.host.ApplicationHost;
import carl.urc.common.network.Middleman;
import carl.urc.common.network.ProtocolResolver;
import carl.urc.common.network.UrcNetworkMethod;
import carl.urc.common.network.client.ConnectionInitiator;
import carl.urc.common.network.client.NetworkEndPoint;
import carl.urc.common.network.method.AuthenticationResponseMethod;

public class UrcAndroidApp extends Application implements ApplicationHost, ConnectionInitiator {

	private int lastSelectedProtocol;
	private String lastAddr;
	private String lastChannel;
	private AndroidNetworkConnector networkConnector;
	private Handler handler;
	private Toast currentToast;
	private boolean clientClosed;

	private static final ProtocolResolver resolver = getResolver();

	private static ProtocolResolver getResolver() {
		ProtocolResolver resolver;
		if (CommonPreferences.preferenceAuthenticate) {
			resolver = new ProtocolResolver(
					new UrcNetworkMethod[] { AuthenticationResponseMethod.DEFAULT });
		} else {
			resolver = new ProtocolResolver(null);
		}
		return resolver;
	}
	

	@Override
	public void onTerminate() {
		if(networkConnector != null) networkConnector.close();
		super.onTerminate();
	}

	@Override
	public void dispatch(UrcNetworkMethod method, Middleman middleman) {
		
	}

	@Override
	public void onConnectionClose(Middleman mm) {
		if(! clientClosed) {
			clientClosed = false;
			showMessage("Disconnected from "
					+ mm.getEndPoint().getRemoteConnectionInfo()
					+ ", reconnecting...");
			doConnection(lastAddr, lastChannel, lastSelectedProtocol);
		}
	}

	@Override
	public void showError(String message, Throwable e) {
		Log.e("urcRemote", message, e);
		showMessage(message + ": " + e.toString());
	}

	@Override
	public void showMessage(final String message) {
		Log.i("urcRemote", message);
		Runnable r = new Runnable() {

			@Override
			public void run() {
				if(currentToast != null) currentToast.cancel();
				currentToast = Toast.makeText(UrcAndroidApp.this, message, ClientPreferences.preferenceAlertTimeout);
				currentToast.show();
			}
		};
		if(getMainLooper().getThread() == Thread.currentThread()) {
			r.run();
		} else {
			handler.post(r);
		}
	}
	
	public void closeConnection() {
		if(networkConnector != null) {
			clientClosed = true;
			networkConnector.close();
			networkConnector = null;
		}
	}

	public void doConnection(String addr, String channel, int selectedProtocol) {
		showMessage("Connecting...");
		this.lastChannel = channel;
		this.lastAddr = addr;
		this.lastSelectedProtocol = selectedProtocol;

		if (networkConnector != null) {
			networkConnector.close();
			networkConnector = null;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		networkConnector = getNetworkEndPoint(selectedProtocol);
		networkConnector.connectAsync(addr, channel);
	}

	public void onConnect() {
		switchToMouse();
	}
	
	private AndroidNetworkConnector getNetworkEndPoint(int protocol) {
		switch(protocol) {
		case R.id.btspp:
			return new BluetoothNetworkConnector(this);
		case R.id.socket:
			return new SocketNetworkConnector(this);
		}
		return null;
	}
	
	private void switchToMouse() {
		Intent intent = new Intent("carl.urc.android.MOUSE");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public Middleman getMiddleman() {
		return (networkConnector == null) ? null : networkConnector.getMiddleman();
	}

	@Override
	public void connectStep(String string) {

	}

	@Override
	public Middleman createMiddleman(NetworkEndPoint ep) throws IOException {
		return new Middleman(ep, this, resolver, this);
	}

	@Override
	public void onAuthenticationFailed() {
		showError("Authentication Failed", new SecurityException("Authentication Failed"));
	}

	@Override
	public void onConnectionRefused(Exception e) {
		showError("Connection Refused", e);
	}


	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler(getMainLooper());
	}
}
