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

import carl.urc.common.host.touchpad.TouchPadPreferences;

public class MidletTouchPadPreferences extends TouchPadPreferences {
	
	public static final int preferenceImmobileDistance = 10;
	public static final int preferenceWheelBarWidth = 30;
	public static final long preferenceHoldDelay = 1000l;
	public static final float preferenceMoveSensitivity = 1.1f;
	public static final float preferenceMoveAcceleration = 1.5f;
	public static final float preferenceWheelSensitivity = 0.7f;
	public static final float preferenceWheelAcceleration = 0.7f;
	public static final long preferenceClickDelay = 100l;
	public static final int preferenceVibrationTime = 100;

	public MidletTouchPadPreferences() {
		super(preferenceWheelBarWidth, preferenceHoldDelay, preferenceMoveSensitivity,
				preferenceMoveAcceleration, preferenceWheelSensitivity,
				preferenceWheelAcceleration, preferenceClickDelay,
				preferenceImmobileDistance, preferenceVibrationTime);
	}
}
