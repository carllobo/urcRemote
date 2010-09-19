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
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;

import carl.urc.j2me.UrcMidlet;

public class WelcomeForm extends Form implements CommandListener,
		ItemCommandListener {

	private TextField channel;

	private TextField host;

	private ChoiceGroup connectionProtocols;

	private UrcMidlet app;

	private int[] indexMap;

	private String[] protocols;

	public WelcomeForm(UrcMidlet app) {
		super("urc Remote");
		this.app = app;

		append("urc Remote");

		connectionProtocols = new ChoiceGroup("Protocol", ChoiceGroup.EXCLUSIVE);
		protocols = app.getProtocols();
		indexMap = new int[protocols.length];
		for(int i = 0; i < protocols.length; ++i) {
			int index = connectionProtocols.append(protocols[i], null);
			indexMap[i] = index;
		}
		connectionProtocols.addCommand(new Command("Select", Command.ITEM, 0));
		connectionProtocols.setItemCommandListener(this);
		append(connectionProtocols);

		host = new TextField("BTAddr/IP: ",
				app.getDefaultDevice(protocols[0]), 15,
				TextField.ANY);
		append(host);
		channel = new TextField("Channel/Port: ",
				app.getDefaultChannel(protocols[0]), 5,
				TextField.ANY);
		append(channel);
		addCommand(new Command("Connect", Command.OK, 0));
		addCommand(new Command("Exit", Command.EXIT, 1));
		setCommandListener(this);
	}

	public void commandAction(Command arg0, Displayable arg1) {
		if (arg0.getCommandType() == Command.EXIT) {
			app.exit();
		} else if (arg0.getCommandType() == Command.OK) {
			try {
				app.doConnection(host.getString(), channel.getString(),
						stringFromSelectedIndex());
			} catch (Exception e) {
				app.showError("Input Error", e);
			}
		}
	}

	private String stringFromSelectedIndex() {
		int index = connectionProtocols.getSelectedIndex();
		for(int i = 0; i < indexMap.length; ++i) {
			if(indexMap[i] == index) return protocols[i];
		}
		return null;
	}

	public void commandAction(Command arg0, Item arg1) {
		String protocol = stringFromSelectedIndex();
		channel.setString(app.getDefaultChannel(protocol));
		host.setString(app.getDefaultDevice(protocol));
	}
}
