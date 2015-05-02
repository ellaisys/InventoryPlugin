package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.InventoryTransactionBrowser;

public class InventoryTransactionBrowserAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2530040256060749499L;

	public InventoryTransactionBrowserAction() {
		super("Inventory Transaction Browser");
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = BackOfficeWindow.getInstance();
		JTabbedPane tabbedPane = window.getTabbedPane();

		InventoryTransactionBrowser browser = null;

		int index = tabbedPane.indexOfTab("Inventory Transaction Browser");
		if (index == -1) {
			browser = new InventoryTransactionBrowser();
			tabbedPane.addTab("Inventory Transaction Browser", browser);
			browser.loadData();
			browser.refreshUITable();
		} else {
			browser = (InventoryTransactionBrowser) tabbedPane.getComponentAt(index);
		}

		tabbedPane.setSelectedComponent(browser);
	}

}
