package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.ExpenseTransactionBrowser;

public class ExpenseTransactionBrowserAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2530040256060749499L;

	public ExpenseTransactionBrowserAction() {
		super("Expense Transaction Browser");
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = BackOfficeWindow.getInstance();
		JTabbedPane tabbedPane = window.getTabbedPane();

		ExpenseTransactionBrowser browser = null;

		int index = tabbedPane.indexOfTab("Expense Transaction Browser");
		if (index == -1) {
			browser = new ExpenseTransactionBrowser();
			tabbedPane.addTab("Expense Transaction Browser", browser);
			browser.loadData();
		} else {
			browser = (ExpenseTransactionBrowser) tabbedPane.getComponentAt(index);
		}

		tabbedPane.setSelectedComponent(browser);
	}

}
