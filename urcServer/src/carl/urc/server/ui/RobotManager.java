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

import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import carl.urc.common.network.method.KeyboardMethod;
import carl.urc.common.network.method.MouseClickMethod;
import carl.urc.common.network.method.MouseMoveMethod;
import carl.urc.common.network.method.MouseWheelMethod;
import carl.urc.server.UrcServer;

public class RobotManager  {
	
	private Robot robot;
	private ExecutorService eventThread;
	private UrcServer application;
	
	public RobotManager(UrcServer application) throws AWTException {
		this.application = application;
		robot = new Robot();
		robot.setAutoDelay(10);
		eventThread = Executors.newSingleThreadExecutor();
	}

	public void moveMouse(final MouseMoveMethod method) {
		eventThread.submit(new Runnable() {

			@Override
			public void run() {
				MouseEventConverter.moveMouse(method, robot);
			}
		});
	}

	public void mouseClick(final MouseClickMethod method) {
		eventThread.submit(new Runnable() {
			
			@Override
			public void run() {
				MouseEventConverter.mouseClick(method, robot);
			}
		});
	}

	public void mouseWheel(final MouseWheelMethod method, RobotManager robot2) {
		eventThread.submit(new Runnable() {
			
			@Override
			public void run() {
				MouseEventConverter.mouseWheel(method, robot);
			}
		});
	}

	public void keyboard(final KeyboardMethod method, final boolean dosAlt) {
		eventThread.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					if(dosAlt)
						KeyboardConverter.keyboardUnicodeWindowsAltTrick(method, robot);
					else
						KeyboardConverter.keyboard(method, robot);
				} catch(Exception e) {
					application.showError("Error sending key", e);
				}
			}
		});
	}

	public void shutdown() {
		if(eventThread != null) eventThread.shutdown();
	}
}
