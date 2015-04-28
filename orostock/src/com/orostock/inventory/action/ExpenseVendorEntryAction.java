package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.form.ExpenseVendorEntryForm;

public class ExpenseVendorEntryAction extends AbstractAction {
	private static final long serialVersionUID = 3242037186499011678L;

	public ExpenseVendorEntryAction() {
		super("New Expense Vendor");
	}

	public void actionPerformed(ActionEvent e) {
		ExpenseVendorEntryForm form = new ExpenseVendorEntryForm();
		form.setBean(new InventoryVendor());
		BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
		dialog.setBeanEditor(form);
		dialog.pack();
		dialog.setLocationRelativeTo(BackOfficeWindow.getInstance());
		dialog.open();
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.action.InventoryVendorEntryAction JD-Core Version:
 * 0.6.0
 */