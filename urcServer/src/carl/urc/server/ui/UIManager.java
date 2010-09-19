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
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import carl.urc.server.UrcServer;

public class UIManager {

	private TrayIcon trayIcon;
	private UrcServer host;

	public UIManager(UrcServer host) throws AWTException {
		this.host = host;
		initTrayIcon();
	}

	public void close() {
		SystemTray.getSystemTray().remove(this.trayIcon);
	}

	public void showServerInformation() {
		String message = host.getServerInformation();
		JOptionPane.showMessageDialog(null, message, "URC Server Info",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void showPreferences() {
		showError("Not implemented yet, sorry!");
	}

	private void initTrayIcon() throws AWTException {
		PopupMenu menu = new PopupMenu();

		MenuItem menuItemPreferences = new MenuItem("Preferences");
		menuItemPreferences.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showPreferences();
			}
		});
		menu.add(menuItemPreferences);

		MenuItem menuItemInfo = new MenuItem("Server Information");
		menuItemInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showServerInformation();
			}

		});
		menu.add(menuItemInfo);

		menu.addSeparator();

		MenuItem menuItemExit = new MenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				host.exit();
			}
		});
		menu.add(menuItemExit);
		BufferedImage image = null;
		try {
			image = ImageIO.read(this.getClass().getResourceAsStream(
					"/icon.png"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		trayIcon = new TrayIcon(image);
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("urc Remote Server");
		trayIcon.setPopupMenu(menu);

		SystemTray.getSystemTray().add(trayIcon);
		showMessage("urc Remote Server", "Server Started",
				MessageType.INFO);
	}

	public void showError(String error) {
		showMessage("Urc Error", error, MessageType.ERROR);
	}

	public void showMessage(String message) {
		showMessage("Urc Info", message, MessageType.INFO);
	}

	private void showMessage(String title, String text, MessageType type) {
		trayIcon.displayMessage(title, text, type);
	}
}
