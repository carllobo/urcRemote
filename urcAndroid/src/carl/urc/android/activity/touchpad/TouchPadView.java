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
 
package carl.urc.android.activity.touchpad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import carl.urc.android.UrcAndroidApp;
import carl.urc.android.activity.TouchPadActivity;
import carl.urc.common.host.touchpad.TouchPadEvent;
import carl.urc.common.host.touchpad.TouchPadPreferences;
import carl.urc.common.host.touchpad.TouchPadSimulator;
import carl.urc.common.host.touchpad.TouchPadSimulatorHost;
import carl.urc.common.network.Middleman;
import carl.urc.common.network.method.KeyboardMethod;

public class TouchPadView extends TextView implements TouchPadSimulatorHost {

	private static final TouchPadPreferences prefs = new AndroidTouchPadPreferences();

	private Middleman middleman;
	private TouchPadSimulator client;
	private UrcAndroidApp application;
	private TouchPadActivity touchPadActivity;

	public TouchPadView(Context context, AttributeSet attrs) {
		super(context, attrs);

		touchPadActivity = (TouchPadActivity) context;
		application = (UrcAndroidApp) touchPadActivity.getApplication();
		middleman = application.getMiddleman();

		setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				middleman.sendMethod(new KeyboardMethod((int) arg2.getKeyCode()));
				return false;
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		client = new TouchPadSimulator(prefs, middleman, this, w);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int unicode = event.getUnicodeChar();

		if (unicode == 0) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_MENU:
				touchPadActivity.openOptionsMenu();
				return true;
			case KeyEvent.KEYCODE_DEL:
				unicode = KeyboardMethod.KEY_BACKSPACE;
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				unicode = KeyboardMethod.KEY_CONTROL;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				unicode = KeyboardMethod.KEY_DPAD_RIGHT;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				unicode = KeyboardMethod.KEY_DPAD_DOWN;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				unicode = KeyboardMethod.KEY_DPAD_UP;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				unicode = KeyboardMethod.KEY_DPAD_LEFT;
				break;
			}
		}

		if (unicode != 0) {
			middleman.sendMethod(new KeyboardMethod(unicode));
			return true;
		}

		return false;
	}

	@Override
	public boolean performLongClick() {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			client.onTouchMove(wrapEvent(event));
			break;
		case MotionEvent.ACTION_DOWN:
			client.onTouchDown(wrapEvent(event));
			break;
		case MotionEvent.ACTION_UP:
			client.onTouchUp(wrapEvent(event));
			break;
		}
		return true;
	}

	private TouchPadEvent wrapEvent(MotionEvent event) {
		return new AndroidTouchPadEvent(event);
	}

	@Override
	public float pow(double base, double exp) {
		return (float) Math.pow(base, exp);
	}

	@Override
	public int round(float number) {
		return Math.round(number);
	}

	@Override
	public int signum(double amount) {
		return (int) Math.signum(amount);
	}

	@Override
	public void setLeftPressed(boolean b) {
		touchPadActivity.setLeftPressed(b);
	}

	@Override
	public void vibrate(int duration) {
		touchPadActivity.vibrate(duration);
	}

	@Override
	public void runDelayed(Runnable runnable, int delay) {
		super.postDelayed(runnable, (long) delay);
	}

	public TouchPadSimulator getSimulator() {
		return client;
	}
}
