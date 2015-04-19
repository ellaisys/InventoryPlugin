package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
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

	public InventoryTransactionBrowserAction(String name) {
		super(name);
	}

	public InventoryTransactionBrowserAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow backOfficeWindow = BackOfficeWindow.getInstance();
		JTabbedPane tabbedPane;
		InventoryTransactionBrowser inventoryTrans;
		tabbedPane = backOfficeWindow.getTabbedPane();
		int index = tabbedPane.indexOfTab("Inventory Transaction Browser");
		if (index == -1) {
			inventoryTrans = new InventoryTransactionBrowser();
			tabbedPane.addTab("Inventory Transaction Browser", inventoryTrans);
		} else {
			inventoryTrans = (InventoryTransactionBrowser) tabbedPane.getComponentAt(index);
		}
		tabbedPane.setSelectedComponent(inventoryTrans);

	}

}
