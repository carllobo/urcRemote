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

import android.view.MotionEvent;
import carl.urc.common.host.touchpad.TouchPadEvent;

public class AndroidTouchPadEvent extends TouchPadEvent {

	private long eventTime;

	public AndroidTouchPadEvent(MotionEvent e) {
		super((int) e.getX(), (int) e.getY(), e.getDownTime());
		this.eventTime = e.getEventTime();
	}

	@Override
	public long getEventTime() {
		return eventTime;
	}
}
