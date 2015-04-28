package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.DistributorBrowser;

public class DistributorBrowserAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8351943608003054308L;

	public DistributorBrowserAction() {
		super("Distributor Browser");
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = BackOfficeWindow.getInstance();
		JTabbedPane tabbedPane = window.getTabbedPane();

		DistributorBrowser browser = null;

		int index = tabbedPane.indexOfTab("Distributor Browser");
		if (index == -1) {
			browser = new DistributorBrowser();
			tabbedPane.addTab("Distributor Browser", browser);
			browser.loadData();
		} else {
			browser = (DistributorBrowser) tabbedPane.getComponentAt(index);
			browser.clearTableModel();
		}

		tabbedPane.setSelectedComponent(browser);
	}
}
