package com.orostock.inventory.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.ExpenseTransaction;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.form.ExpenseTransactionEntryForm;
import com.orostock.inventory.ui.form.ExpenseVendorEntryForm;

public class ExpenseVendorBrowser extends ModelBrowser<InventoryVendor> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3216688728242415755L;
	private JButton btnNewExpense = new JButton("NEW EXPENSE");

	public ExpenseVendorBrowser() {
		super(new ExpenseVendorEntryForm());
		beanEditor.clearTableModel();
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		this.btnNewExpense.setActionCommand(Command.NEW_EXPENSE.name());
		this.btnNewExpense.setEnabled(false);
		init(new ExpenseVendorTableModel(), new Dimension(400, 400), new Dimension(500, 400));
		beanEditor.setFieldsEnable(false);
		hideDeleteBtn();
		refreshTable();
	}

	public void loadData() {
		List<InventoryVendor> inventoryVendors = InventoryVendorDAO.getInstance().findAllExpenseVendors(true);
		ExpenseVendorTableModel tableModel = (ExpenseVendorTableModel) this.browserTable.getModel();
		tableModel.setRows(inventoryVendors);
	}

	public void refreshTable() {
		loadData();
		super.refreshTable();
	}

	public void refreshUITable() {
		super.refreshTable();
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(Command.NEW_EXPENSE.name())) {
			InventoryVendor bean = (InventoryVendor) this.beanEditor.getBean();
			ExpenseTransactionEntryForm form = new ExpenseTransactionEntryForm();
			form.setBean(new ExpenseTransaction());
			form.setInventoryVendor(bean);
			BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
			dialog.pack();
			dialog.open();
			refreshTable();
		} else if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			this.btnNewExpense.setEnabled(false);
		} else {
			InventoryVendor bean = (InventoryVendor) this.beanEditor.getBean();
			if ((bean != null) && (bean.getId() != null)) {
				this.btnNewExpense.setEnabled(true);
			} else
				this.btnNewExpense.setEnabled(false);
		}
	}

	protected JButton getAdditionalButton() {
		return this.btnNewExpense;
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		beanEditor.setFieldsEnable(false);
		InventoryVendor bean = (InventoryVendor) this.beanEditor.getBean();
		if ((bean != null) && (bean.getId() != null)) {
			this.btnNewExpense.setEnabled(true);
		} else
			this.btnNewExpense.setEnabled(false);
	}

	protected void searchInventoryVendor() {
	}

	static class ExpenseVendorTableModel extends ListTableModel<InventoryVendor> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8008682351957964208L;

		public ExpenseVendorTableModel() {
			super(new String[] { "VENDOR NAME", "PHONE", "ADDRESS" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryVendor row = (InventoryVendor) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getName();
			case 1:
				return row.getPhone();
			case 2:
				return row.getAddress();
			}
			return row.getName();
		}
	}

}
