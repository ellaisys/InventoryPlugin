package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.CompanyBrowser;

public class CompanyBrowserAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5110637654763546195L;

	public CompanyBrowserAction() {
		super("Company Browser");
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = BackOfficeWindow.getInstance();
		JTabbedPane tabbedPane = window.getTabbedPane();

		CompanyBrowser browser = null;

		int index = tabbedPane.indexOfTab("Company Browser");
		if (index == -1) {
			browser = new CompanyBrowser();
			tabbedPane.addTab("Company Browser", browser);
			browser.loadData();
		} else {
			browser = (CompanyBrowser) tabbedPane.getComponentAt(index);
		}

		tabbedPane.setSelectedComponent(browser);
	}
}
