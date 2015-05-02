package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.ExpenseVendorBrowser;

public class ExpenseVendorBrowserAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8351943608003054308L;

	public ExpenseVendorBrowserAction() {
		super("Expense Vendor Browser");
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = BackOfficeWindow.getInstance();
		JTabbedPane tabbedPane = window.getTabbedPane();

		ExpenseVendorBrowser browser = null;

		int index = tabbedPane.indexOfTab("Expense Vendor Browser");
		if (index == -1) {
			browser = new ExpenseVendorBrowser();
			tabbedPane.addTab("Expense Vendor Browser", browser);
			browser.loadData();
			browser.refreshUITable();
		} else {
			browser = (ExpenseVendorBrowser) tabbedPane.getComponentAt(index);
		}

		tabbedPane.setSelectedComponent(browser);
	}
}
