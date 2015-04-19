package com.orostock.inventory.ui;

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

public class InventoryVendorBrowser extends ModelBrowser<InventoryVendor> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3216688728242415755L;
	private JButton btnNewExpense = new JButton("NEW EXPENSE");

	public InventoryVendorBrowser() {
		super(new InventoryVendorEntryForm());
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		this.btnNewExpense.setActionCommand(Command.NEW_EXPENSE.name());
		this.btnNewExpense.setEnabled(false);
		init(new InventoryVendorTableModel());
		hideDeleteBtn();
	}

	public void loadData() {
		List<InventoryVendor> inventoryVendors = InventoryVendorDAO.getInstance().findAll();
		InventoryVendorTableModel tableModel = (InventoryVendorTableModel) this.browserTable.getModel();
		tableModel.setRows(inventoryVendors);
	}

	public void refreshTable() {
		loadData();
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
		InventoryVendor bean = (InventoryVendor) this.beanEditor.getBean();
		if ((bean != null) && (bean.getId() != null)) {
			this.btnNewExpense.setEnabled(true);
		} else
			this.btnNewExpense.setEnabled(false);
	}

	protected void searchInventoryVendor() {
	}

	static class InventoryVendorTableModel extends ListTableModel<InventoryVendor> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8008682351957964208L;

		public InventoryVendorTableModel() {
			super(new String[] { "VENDOR NAME", "PHONE", "EMAIL", "ADDRESS" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryVendor row = (InventoryVendor) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getName();
			case 1:
				return row.getPhone();
			case 2:
				return row.getEmail();
			case 3:
				return row.getAddress();
			}
			return row.getName();
		}
	}
}
