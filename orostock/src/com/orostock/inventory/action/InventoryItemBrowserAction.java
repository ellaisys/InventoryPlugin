package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.InventoryItemBrowser;

public class InventoryItemBrowserAction extends AbstractAction {
	private static final long serialVersionUID = -1465706143493039330L;

	public InventoryItemBrowserAction() {
		super("Inventory Item Browser");
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = BackOfficeWindow.getInstance();
		JTabbedPane tabbedPane = window.getTabbedPane();

		InventoryItemBrowser browser = null;

		int index = tabbedPane.indexOfTab("Inventory Item Browser");
		if (index == -1) {
			browser = new InventoryItemBrowser();
			tabbedPane.addTab("Inventory Item Browser", browser);
			browser.loadData();
			browser.refreshUITable();
		} else {
			browser = (InventoryItemBrowser) tabbedPane.getComponentAt(index);
		}

		tabbedPane.setSelectedComponent(browser);
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.action.InventoryItemBrowserAction JD-Core Version:
 * 0.6.0
 */