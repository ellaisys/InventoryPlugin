package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.PersonBrowser;

public class PersonBrowserAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8351943608003054308L;

	public PersonBrowserAction() {
		super("People Directory");
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = BackOfficeWindow.getInstance();
		JTabbedPane tabbedPane = window.getTabbedPane();

		PersonBrowser browser = null;

		int index = tabbedPane.indexOfTab("People Directory");
		if (index == -1) {
			browser = new PersonBrowser();
			tabbedPane.addTab("People Directory", browser);
			browser.loadData();
		} else {
			browser = (PersonBrowser) tabbedPane.getComponentAt(index);
		}

		tabbedPane.setSelectedComponent(browser);
	}
}
