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
 
package carl.urc.server.ui;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import carl.urc.common.network.method.MouseClickMethod;
import carl.urc.common.network.method.MouseMoveMethod;
import carl.urc.common.network.method.MouseWheelMethod;

class MouseEventConverter {

	public static void mouseWheel(MouseWheelMethod method, Robot robot) {
		robot.mouseWheel(method.getAmount());
	}

	public static void moveMouse(MouseMoveMethod method, Robot robot) {
		Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		int x = mouseLocation.x + method.getMoveX();
		int y = mouseLocation.y + method.getMoveY();
		robot.mouseMove(x, y);
	}

	public static void mouseClick(MouseClickMethod method, Robot robot) {
		int button;

		switch (method.getButton()) {
		case MouseClickMethod.BUTTON_LEFT:
			button = InputEvent.BUTTON1_MASK;
			break;
		case MouseClickMethod.BUTTON_RIGHT:
			button = InputEvent.BUTTON3_MASK;
			break;
		case MouseClickMethod.BUTTON_MIDDLE:
			button = InputEvent.BUTTON2_MASK;
			break;
		default:
			return;
		}

		if (method.getState() == MouseClickMethod.STATE_DOWN) {
			robot.mousePress(button);
		} else if (method.getState() == MouseClickMethod.STATE_UP) {
			robot.mouseRelease(button);
		}
	}
}
