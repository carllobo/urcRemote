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
 
package carl.urc.common.host.touchpad;

import carl.urc.common.network.Middleman;
import carl.urc.common.network.method.MouseClickMethod;
import carl.urc.common.network.method.MouseMoveMethod;
import carl.urc.common.network.method.MouseWheelMethod;

public class TouchPadSimulator {

	private final Middleman client;
	private final TouchPadSimulatorHost host;
	private final TouchPadPreferences prefs;

	private boolean mouseMoveOrWheel;
	private int moveDownX;
	private int movePreviousY;
	private float moveResultX;
	private float moveResultY;
	private boolean holdPossible;
	private int movePreviousX;
	private int moveDownY;
	private int wheelPrevious;
	private float wheelResult;
	private boolean leftPressed;
	private int width;

	public TouchPadSimulator(TouchPadPreferences prefs,
			Middleman networkClient, TouchPadSimulatorHost host) {
		this.prefs = prefs;
		this.client = networkClient;
		this.host = host;
	}
	
	public TouchPadSimulator(TouchPadPreferences prefs,
			Middleman client, TouchPadSimulatorHost host, int width) {
		this.prefs = prefs;
		this.client = client;
		this.host = host;
		setWidth(width);
	}

	public void onTouchDown(TouchPadEvent event) {
		mouseMoveOrWheel = event.getX() < (width - prefs.wheelBarWidth);

		if (mouseMoveOrWheel) {
			onTouchDownMouseMove(event);
		} else {
			onTouchDownMouseWheel(event);
		}
	}

	private void onTouchDownMouseMove(TouchPadEvent event) {
		moveDownX = movePreviousX = event.getX();
		moveDownY = movePreviousY = event.getY();

		moveResultX = 0;
		moveResultY = 0;

		holdPossible = true;
	}

	private void onTouchDownMouseWheel(TouchPadEvent event) {
		wheelPrevious = event.getY();
		wheelResult = 0;
	}

	public void onTouchMove(TouchPadEvent event) {
		if (mouseMoveOrWheel) {
			onTouchMoveMouseMove(event);
		} else {
			onTouchMoveMouseWheel(event);
		}
	}

	private void onTouchMoveMouseMove(TouchPadEvent event) {
		if (holdPossible) {
			if (getDistanceFromDown(event) > prefs.immobileDistance) {
				holdPossible = false;
			} else if (event.getEventTime() - event.getDownTime() > prefs.holdDelay) {
				mouseClick(MouseClickMethod.BUTTON_LEFT,
						MouseClickMethod.STATE_DOWN);

				holdPossible = false;

				setLeftPressed(true);

				host.vibrate(100);
			}
		}

		float moveRawX = event.getX() - movePreviousX;
		float moveRawY = event.getY() - movePreviousY;

		moveRawX *= prefs.moveSensitivity;
		moveRawY *= prefs.moveSensitivity;

		moveRawX = (float) ((host.pow(Math.abs(moveRawX), prefs.moveAcceleration) * host
				
				.signum(moveRawX)));
		moveRawY = (float) ((host.pow(Math.abs(moveRawY), prefs.moveAcceleration) 
				* host
				.signum(moveRawY)));

		moveRawX += moveResultX;
		moveRawY += moveResultY;

		int moveXFinal = host.round(moveRawX);
		int moveYFinal = host.round(moveRawY);

		if (moveXFinal != 0 || moveYFinal != 0) {
			mouseMove(moveXFinal, moveYFinal);
		}

		moveResultX = moveRawX - moveXFinal;
		moveResultY = moveRawY - moveYFinal;

		movePreviousX = event.getX();
		movePreviousY = event.getY();
	}

	private void onTouchMoveMouseWheel(TouchPadEvent event) {
		float wheelRaw = event.getY() - wheelPrevious;
		wheelRaw *= prefs.wheelSensitivity;
		wheelRaw = (float) ((host.pow(Math.abs(wheelRaw), prefs.wheelAcceleration) * host
				.signum(wheelRaw)));
		wheelRaw += wheelResult;
		int wheelFinal = host.round(wheelRaw);

		if (wheelFinal != 0) {
			mouseWheel(wheelFinal);
		}

		wheelResult = wheelRaw - wheelFinal;
		wheelPrevious = event.getY();
	}

	public void onTouchUp(TouchPadEvent event) {
		if (mouseMoveOrWheel) {
			onTouchUpMouseMove(event);
		} else {
			onTouchUpMouseWheel(event);
		}
	}

	private void onTouchUpMouseMove(TouchPadEvent event) {
		if (event.getEventTime() - event.getDownTime() < prefs.clickDelay
				&& getDistanceFromDown(event) <= prefs.immobileDistance) {
			if (leftPressed) {
				mouseClick(MouseClickMethod.BUTTON_LEFT,
						MouseClickMethod.STATE_UP);
				host.vibrate(100);
				setLeftPressed(false);
			} else {
				mouseClick(MouseClickMethod.BUTTON_LEFT,
						MouseClickMethod.STATE_DOWN);

				host.vibrate(50);
				setLeftPressed(true);

				host.runDelayed(new ReleaseRunnable(this), 50);
			}
		}
	}

	private void onTouchUpMouseWheel(TouchPadEvent event) {

	}

	void setLeftPressed(boolean b) {
		leftPressed = b;
		host.setLeftPressed(b);
	}

	private double getDistanceFromDown(TouchPadEvent event) {
		return Math.sqrt(host.pow((event.getX() - moveDownX), 2)
				+ host.pow((event.getY() - moveDownY), 2));
	}

	void mouseClick(byte button, boolean stateDown) {
		MouseClickMethod method = new MouseClickMethod(button, stateDown);
		client.sendMethod(method);
	}

	private void mouseMove(int x, int y) {
		MouseMoveMethod method = new MouseMoveMethod((short) x, (short) y);
		client.sendMethod(method);
	}

	private void mouseWheel(int amount) {
		MouseWheelMethod method = new MouseWheelMethod((byte) amount);
		client.sendMethod(method);
	}

	public void setWidth(int width2) {
		this.width = width2;
	}

	public void doClick(byte button) {
		MouseClickMethod m = new MouseClickMethod(button,
				MouseClickMethod.STATE_DOWN);
		client.sendMethod(m);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		m = new MouseClickMethod(button, MouseClickMethod.STATE_UP);
		client.sendMethod(m);
	}
}

class ReleaseRunnable implements Runnable {
	
	private TouchPadSimulator s;

	public ReleaseRunnable(TouchPadSimulator s) {
		this.s = s;
	}
	
	public void run() {
		s.mouseClick(MouseClickMethod.BUTTON_LEFT,
				MouseClickMethod.STATE_UP);
		s.setLeftPressed(false);
	}
}
