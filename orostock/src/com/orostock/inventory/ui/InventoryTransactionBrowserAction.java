package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;

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
		} else {
			browser = (InventoryTransactionBrowser) tabbedPane.getComponentAt(index);
		}

		tabbedPane.setSelectedComponent(browser);
	}

}
