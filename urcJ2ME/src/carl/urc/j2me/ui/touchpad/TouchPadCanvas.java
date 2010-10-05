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
 
package carl.urc.j2me.ui.touchpad;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import carl.urc.common.host.touchpad.TouchPadEvent;
import carl.urc.common.host.touchpad.TouchPadPreferences;
import carl.urc.common.host.touchpad.TouchPadSimulator;
import carl.urc.common.host.touchpad.TouchPadSimulatorHost;
import carl.urc.common.network.Middleman;
import carl.urc.common.network.method.KeyboardMethod;
import carl.urc.common.network.method.MouseClickMethod;
import carl.urc.j2me.UrcMidlet;

public class TouchPadCanvas extends Canvas implements CommandListener,
		TouchPadSimulatorHost {

	private static final TouchPadPreferences prefs = new MidletTouchPadPreferences();

	private UrcMidlet application;
	private Timer runDelayTimer;
	private Command keyboard;
	private Command rtclick;
	private Command click;
	private Command cclick;
	private Command dblclick;
	private int width;
	private int height;
	private TouchPadSimulator client;
	public Middleman middleman;
	private long downtime;
	private int grayscale = 255;

	private Command close;

	public TouchPadCanvas(UrcMidlet j2meRemoteControl,
			Middleman middleman) {
		this.application = j2meRemoteControl;
		this.middleman = middleman;

		client = new TouchPadSimulator(prefs, middleman, this);
		runDelayTimer = new Timer();

		close = new Command("Close", Command.ITEM, 7);
		keyboard = new Command("Keyboard", Command.OK, 6);
		cclick = new Command("Mid-Click", Command.ITEM, 5);
		rtclick = new Command("Rt-Click", Command.ITEM, 4);
		dblclick = new Command("Dbl-Click", Command.ITEM, 3);
		click = new Command("Click", Command.ITEM, 2);
		addCommand(click);
		addCommand(rtclick);
		addCommand(cclick);
		addCommand(keyboard);
		addCommand(close);
		setCommandListener(this);
		setFullScreenMode(true);
	}

	protected void paint(Graphics g) {
		width = getWidth();
		client.setWidth(width);
		height = getHeight();
		g.setGrayScale(grayscale);
		g.fillRect(0, 0, width - prefs.wheelBarWidth, height);
		g.setColor(0);
		g.fillRect(width - prefs.wheelBarWidth, 0, width, height);
	}

	protected void keyReleased(int keyCode) {
		switch (keyCode) {
		case UP:
			middleman
					.sendMethod(new KeyboardMethod(KeyboardMethod.KEY_DPAD_UP));
			break;
		case DOWN:
			middleman.sendMethod(new KeyboardMethod(
					KeyboardMethod.KEY_DPAD_DOWN));
			break;
		case RIGHT:
			middleman.sendMethod(new KeyboardMethod(
					KeyboardMethod.KEY_DPAD_RIGHT));
			break;
		case LEFT:
			middleman.sendMethod(new KeyboardMethod(
					KeyboardMethod.KEY_DPAD_LEFT));
			break;
		case FIRE:
			click();
			break;
		default:
			middleman.sendMethod(new KeyboardMethod(keyCode));
		}
		super.keyReleased(keyCode);
	}

	protected void pointerDragged(int x, int y) {
		TouchPadEvent event = new TouchPadEvent(x, y, downtime);
		client.onTouchMove(event);
		super.pointerDragged(x, y);
	}

	protected void pointerPressed(int x, int y) {
		downtime = System.currentTimeMillis();
		TouchPadEvent event = new TouchPadEvent(x, y, downtime);
		client.onTouchDown(event);
		super.pointerPressed(x, y);
	}

	protected void pointerReleased(int x, int y) {
		middleman.clearOutputQueue();
		TouchPadEvent event = new TouchPadEvent(x, y, downtime);
		client.onTouchUp(event);
		super.pointerReleased(x, y);
	}

	private void click() {
		client.doClick(MouseClickMethod.BUTTON_LEFT);
	}

	private void rtclick() {
		client.doClick(MouseClickMethod.BUTTON_RIGHT);
	}

	private void cclick() {
		client.doClick(MouseClickMethod.BUTTON_MIDDLE);
	}

	private void dblclick() {
		click();
		try {
			Thread.sleep(60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		click();
	}
	
	private void close() {
		application.switchToWelcome();
	}

	public void commandAction(Command arg0, Displayable arg1) {
		if (arg0 == click)
			click();
		else if (arg0 == close)
			close();
		else if (arg0 == dblclick)
			dblclick();
		else if (arg0 == rtclick)
			rtclick();
		else if (arg0 == cclick)
			cclick();
		else if (arg0 == keyboard)
			application.switchToKeyboard();
	}

	public void runDelayed(final Runnable runnable, final int delay) {
		runDelayTimer.schedule(new TimerTask() {
			
			public void run() {
				runnable.run();
			}
		}, delay);
	}

	public void setLeftPressed(boolean b) {
		grayscale = (b) ? 120 : 255;
		repaint();
	}

	public void vibrate(int duration) {
		Display.getDisplay(application).vibrate(duration);
	}

	public float pow(double base, double exp) {
		if (base == 0)
			return 0;
		final int x = (int) (Double.doubleToLongBits(base) >> 32);
		final int y = (int) (exp * (x - 1072632447) + 1072632447);
		float result = (float) Double.longBitsToDouble(((long) y) << 32);
		return result;
	}

	public int round(float number) {
		int r = (int) (number + (signum(number) * 0.5));
		return r;
	}

	public int signum(double amount) {
		return (amount < 0) ? -1 : 1;
	}

	protected void hideNotify() {
		super.hideNotify();
		runDelayTimer.cancel();
		client.close();
	}
}
