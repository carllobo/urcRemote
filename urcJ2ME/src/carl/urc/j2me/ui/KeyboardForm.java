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
 
package carl.urc.j2me.ui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import carl.urc.common.network.Middleman;
import carl.urc.common.network.method.KeyboardMethod;
import carl.urc.j2me.UrcMidlet;

public class KeyboardForm extends Form implements CommandListener {

	private UrcMidlet application;
	private Middleman middleman;
	private Command mouse;
	private TextField tf;
	private Command ok;
	private ChoiceGroup otherKeys;
	private Command backSpace;
	private Command tab;
	private int enterIndex;
	private int controlIndex;
	private int backToMouseIndex;

	public KeyboardForm(UrcMidlet app, Middleman middleman) {
		super("Keyboard");
		this.application = app;
		this.middleman = middleman;
		tf = new TextField("Enter Text", "", 100, TextField.ANY);
		otherKeys = new ChoiceGroup("Special", ChoiceGroup.MULTIPLE);
		enterIndex = otherKeys.append("Send Enter", null);
		controlIndex = otherKeys.append("Send Control", null);
		backToMouseIndex = otherKeys.append("Back To Mouse", null);
		otherKeys.setSelectedIndex(0, true);
		otherKeys.setSelectedIndex(2, true);

		tab = new Command("Tab", Command.ITEM, 5);
		backSpace = new Command("BackSpace", Command.ITEM, 4);
		mouse = new Command("Mouse", Command.ITEM, 3);
		ok = new Command("Send", Command.ITEM, 2);

		append(tf);
		append(otherKeys);
		addCommand(tab);
		addCommand(backSpace);
		addCommand(ok);
		addCommand(mouse);
		setCommandListener(this);
	}

	private void sendBackSpace() {
		middleman.sendMethod(new KeyboardMethod(KeyboardMethod.KEY_BACKSPACE));
	}

	private void sendTab() {
		middleman.sendMethod(new KeyboardMethod('\t'));
	}

	private void send() {
		try {
			if (otherKeys.isSelected(controlIndex)) {
				middleman.sendMethod(new KeyboardMethod(
						KeyboardMethod.KEY_CONTROL));
			}
			char[] text = tf.getString().toCharArray();
			for (int i = 0; i < text.length; ++i) {
				int unicode = (int) text[i];
				KeyboardMethod k = new KeyboardMethod(unicode);
				middleman.sendMethod(k);
			}
			if (otherKeys.isSelected(enterIndex)) {
				middleman.sendMethod(new KeyboardMethod(
						KeyboardMethod.KEY_ENTER));
			}
			if (otherKeys.isSelected(backToMouseIndex)) {
				application.switchToMouse();
			} else {
				tf.setString("");
			}
		} catch (Exception e) {
			application.showError("Can't send keys", e);
		}
	}

	public void commandAction(Command arg0, Displayable arg1) {
		if (arg0 == ok)
			send();
		else if (arg0 == backSpace)
			sendBackSpace();
		else if (arg0 == tab)
			sendTab();
		else if (arg0 == mouse)
			application.switchToMouse();
	}

}
