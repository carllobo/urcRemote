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

import java.awt.Robot;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import carl.urc.common.network.method.KeyboardMethod;

class KeyboardConverter {
	private static boolean controlHeld = false;

	public static void keyboard(KeyboardMethod method, Robot robot) throws Exception {
		boolean shift = true;
		int keyCode = KeyboardMethod.KEY_INVALID;
		int unicode = method.getUnicode();
		boolean skip = false;

		switch (unicode) {
		case KeyboardMethod.KEY_CONTROL:
			controlHeld = !controlHeld;
			skip = true;
			break;
		case KeyboardMethod.KEY_ENTER:
			keyCode = KeyEvent.VK_ENTER;
			shift = false;
			break;
		case KeyboardMethod.KEY_ESCAPE:
			keyCode = KeyEvent.VK_ESCAPE;
			shift = false;
		case KeyboardMethod.KEY_BACKSPACE:
			keyCode = KeyEvent.VK_BACK_SPACE;
			shift = false;
			break;
		case KeyboardMethod.KEY_DPAD_DOWN:
			keyCode = KeyEvent.VK_DOWN;
			shift = false;
			break;
		case KeyboardMethod.KEY_DPAD_LEFT:
			keyCode = KeyEvent.VK_LEFT;
			shift = false;
			break;
		case KeyboardMethod.KEY_DPAD_RIGHT:
			keyCode = KeyEvent.VK_RIGHT;
			shift = false;
			break;
		case KeyboardMethod.KEY_DPAD_UP:
			keyCode = KeyEvent.VK_UP;
			shift = false;
			break;
		case ' ':
			keyCode = KeyEvent.VK_SPACE;
			break;
		case '~':
			keyCode = KeyEvent.VK_BACK_QUOTE;
			break;
		case '!':
			keyCode = KeyEvent.VK_1;
			break;
		case '@':
			keyCode = KeyEvent.VK_2;
			break;
		case '#':
			keyCode = KeyEvent.VK_3;
			break;
		case '$':
			keyCode = KeyEvent.VK_4;
			break;
		case '%':
			keyCode = KeyEvent.VK_5;
			break;
		case '^':
			keyCode = KeyEvent.VK_6;
			break;
		case '&':
			keyCode = KeyEvent.VK_7;
			break;
		case '*':
			keyCode = KeyEvent.VK_8;
			break;
		case '(':
			keyCode = KeyEvent.VK_9;
			break;
		case ')':
			keyCode = KeyEvent.VK_0;
			break;
		case ':':
			keyCode = KeyEvent.VK_SEMICOLON;
			break;
		case '_':
			keyCode = KeyEvent.VK_MINUS;
			break;
		case '+':
			keyCode = KeyEvent.VK_EQUALS;
			break;
		case '|':
			keyCode = KeyEvent.VK_BACK_SLASH;
			break;
		case '"':
			keyCode = KeyEvent.VK_QUOTE;
			break;
		case '?':
			keyCode = KeyEvent.VK_SLASH;
			break;
		case '{':
			keyCode = KeyEvent.VK_OPEN_BRACKET;
			break;
		case '}':
			keyCode = KeyEvent.VK_CLOSE_BRACKET;
			break;
		case '<':
			keyCode = KeyEvent.VK_COMMA;
			break;
		case '>':
			keyCode = KeyEvent.VK_PERIOD;
			break;
		case '`':
			keyCode = KeyEvent.VK_BACK_QUOTE;
			shift = false;
			break;
		case ';':
			keyCode = KeyEvent.VK_SEMICOLON;
			shift = false;
			break;
		case '-':
			keyCode = KeyEvent.VK_MINUS;
			shift = false;
			break;
		case '=':
			keyCode = KeyEvent.VK_EQUALS;
			shift = false;
			break;
		case '\\':
			keyCode = KeyEvent.VK_BACK_SLASH;
			shift = false;
			break;
		case '\'':
			keyCode = KeyEvent.VK_QUOTE;
			shift = false;
			break;
		case '/':
			keyCode = KeyEvent.VK_SLASH;
			shift = false;
			break;
		case '[':
			keyCode = KeyEvent.VK_OPEN_BRACKET;
			shift = false;
			break;
		case ']':
			keyCode = KeyEvent.VK_CLOSE_BRACKET;
			shift = false;
			break;
		case ',':
			keyCode = KeyEvent.VK_COMMA;
			shift = false;
			break;
		case '.':
			keyCode = KeyEvent.VK_PERIOD;
			shift = false;
			break;
		case '\t':
			keyCode = KeyEvent.VK_TAB;
			shift = false;
			break;
		default:
			shift = !(Character.isLowerCase(unicode) || Character
					.isDigit(unicode));
			keyCode = Character.toUpperCase(unicode);
			KeyStroke stroke = KeyStroke.getKeyStroke("pressed "
					+ ((char) keyCode));
			if (stroke == null) {
				throw new Exception("Error Converting Stroke: " + keyCode + "("
						+ ((char) keyCode) + ")");
			} else {
				keyCode = stroke.getKeyCode();
			}
		}
		if (!skip) {
			if (controlHeld)
				robot.keyPress(KeyEvent.VK_CONTROL);
			if (shift)
				robot.keyPress(KeyEvent.VK_SHIFT);

			try {
				robot.keyPress(keyCode);
				robot.keyRelease(keyCode);
			} catch (IllegalArgumentException e) {
				throw new Exception("Error pressing key: " + keyCode + ", char="
						+ ((char) keyCode) + ", Method Unicode=" + unicode
						+ ", char=" + ((char) unicode));
			}

			if (shift)
				robot.keyRelease(KeyEvent.VK_SHIFT);
			if (controlHeld)
				robot.keyRelease(KeyEvent.VK_CONTROL);
			controlHeld = false;
		}
	}

	public static void keyboardUnicodeWindowsAltTrick(KeyboardMethod method,
			Robot robot) {
		switch (method.getUnicode()) {
		case KeyboardMethod.KEY_BACKSPACE:
			robot.keyPress(KeyEvent.VK_BACK_SPACE);
			robot.keyRelease(KeyEvent.VK_BACK_SPACE);
			return;
		case KeyboardMethod.KEY_ENTER:
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_BACK_SPACE);
			return;
		}
		robot.keyPress(KeyEvent.VK_ALT);
		String unicodeString = Integer.toString(method.getUnicode());
		for (int i = 0; i < unicodeString.length(); i++) {
			int digit = Integer.parseInt(unicodeString.substring(i, i + 1));
			int keycode = digit + KeyEvent.VK_NUMPAD0;
			robot.keyPress(keycode);
			robot.keyRelease(keycode);
		}

		robot.keyRelease(KeyEvent.VK_ALT);
	}
}
