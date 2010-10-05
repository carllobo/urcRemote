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
 
package carl.urc.android.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import carl.urc.android.R;
import carl.urc.android.UrcAndroidApp;
import carl.urc.android.activity.touchpad.TouchPadView;
import carl.urc.common.network.method.KeyboardMethod;
import carl.urc.common.network.method.MouseClickMethod;

public class TouchPadActivity extends Activity {

	private UrcAndroidApp application;
	private Vibrator vibrator;
	private TouchPadView touchPadView;
	private MenuItem keyboardMenuItem;
	private MenuItem tab;
	private MenuItem up;
	private MenuItem control;
	private MenuItem down;
	private MenuItem left;
	private MenuItem right;
	private MenuItem closeMenuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.touchpad);

		application = (UrcAndroidApp) getApplication();
		application.setTouchpadActivity(this);
		
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		touchPadView = (TouchPadView) findViewById(R.id.touchPadView);

		addEventListeners();
	}

	@Override
	protected void onDestroy() {
		close();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		close();
		super.onPause();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		closeMenuItem = menu.add("Close");
		keyboardMenuItem = menu.add("Keyboard");
		SubMenu specialKeyItems = menu.addSubMenu("Special Keys");
		tab = specialKeyItems.add("Tab");
		control = specialKeyItems.add("Control");
		up = specialKeyItems.add("Up Arrow");
		down = specialKeyItems.add("Down Arrow");
		left = specialKeyItems.add("Left Arrow");
		right = specialKeyItems.add("Right Arrow");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item == closeMenuItem) {
			close();
			finish();
		} else if (item == keyboardMenuItem) {
			toggleKeyboard();
		} else if(item == tab) {
			doSpecialKeys('\t');
		} else if(item == control) {
			doSpecialKeys(KeyboardMethod.KEY_CONTROL);
		} else if(item == up) {
			doSpecialKeys(KeyboardMethod.KEY_DPAD_UP);
		} else if(item == down) {
			doSpecialKeys(KeyboardMethod.KEY_DPAD_DOWN);
		} else if(item == left) {
			doSpecialKeys(KeyboardMethod.KEY_DPAD_LEFT);
		} else if(item == right) {
			doSpecialKeys(KeyboardMethod.KEY_DPAD_RIGHT);
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
		return true;
	}

	private void addEventListeners() {
		Button b = (Button) findViewById(R.id.leftButton);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				touchPadView.getSimulator().doClick(
						MouseClickMethod.BUTTON_LEFT);
			}
		});

		b = (Button) findViewById(R.id.middleButton);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				touchPadView.getSimulator().doClick(
						MouseClickMethod.BUTTON_MIDDLE);
			}
		});

		b = (Button) findViewById(R.id.rightButton);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				touchPadView.getSimulator().doClick(
						MouseClickMethod.BUTTON_RIGHT);
			}
		});

	}
	
	private void close() {
		try {
			touchPadView.getSimulator().close();
			application.closeConnection();
			application.setTouchpadActivity(null);
		} catch(RuntimeException e) {
			application.showError("Error in close", e);
		}
	}

	public void vibrate(int duration) {
		vibrator.vibrate(duration);
	}

	public void setLeftPressed(boolean b) {
		((Button) findViewById(R.id.leftButton)).setPressed(b);
	}

	private void toggleKeyboard() {
		((InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.toggleSoftInput(0, 0);
	}
	
	private void doSpecialKeys(int unicode) {
		application.getMiddleman().sendMethod(new KeyboardMethod(unicode));
	}

}
