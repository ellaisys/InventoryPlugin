package com.orostock.inventory.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.form.ExpenseVendorEntryForm;

public class DistributorEntryAction extends AbstractAction {
	private static final long serialVersionUID = 3242037186499011678L;

	public DistributorEntryAction() {
		super("New Distributor");
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
