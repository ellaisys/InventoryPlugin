package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.ExpenseTransaction;
import com.floreantpos.model.dao.ExpenseTransactionDAO;
import com.floreantpos.ui.dialog.BeanEditorDialog;

public class ExpenseTransactionBrowser extends ModelBrowser<ExpenseTransaction> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4133361713025286200L;
	private static ExpenseTransactionEntryForm et = new ExpenseTransactionEntryForm();
	private JButton btnNewExpense = new JButton("NEW EXPENSE");

	public ExpenseTransactionBrowser() {
		super(et);
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		this.btnNewExpense.setActionCommand(Command.NEW_EXPENSE.name());
		this.btnNewExpense.setEnabled(true);
		init(new ExpenseTransactionTableModel());
		hideDeleteBtn();
		hideNewBtn();
		et.setFieldsEnableEdit();
		refreshTable();
	}

	public void loadData() {
		List<ExpenseTransaction> expense = ExpenseTransactionDAO.getInstance().findByCurrentMonth();
		ExpenseTransactionTableModel tableModel = (ExpenseTransactionTableModel) this.browserTable.getModel();
		tableModel.setRows(expense);
		tableModel.setPageSize(25);

	}

	protected JButton getAdditionalButton() {
		return this.btnNewExpense;
	}

	public void refreshTable() {
		loadData();
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		ExpenseTransaction bean = (ExpenseTransaction) this.beanEditor.getBean();
		if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			et.setFieldsEnableEdit();
		} else if (e.getActionCommand().equalsIgnoreCase(Command.NEW_EXPENSE.name())) {
			ExpenseTransactionEntryForm form = new ExpenseTransactionEntryForm();
			form.setBean(new ExpenseTransaction());
			BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
			dialog.pack();
			dialog.open();
			refreshTable();
		} else if (e.getActionCommand().equalsIgnoreCase(Command.CANCEL.name())) {
			this.btnNewExpense.setEnabled(true);
		} else {
			if ((bean != null) && (bean.getId() != null)) {
				this.btnNewExpense.setEnabled(true);
			} else
				this.btnNewExpense.setEnabled(false);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		ExpenseTransaction bean = (ExpenseTransaction) this.beanEditor.getBean();
		if ((bean != null) && (bean.getId() != null)) {
			this.btnNewExpense.setEnabled(true);
		} else
			this.btnNewExpense.setEnabled(false);
	}

	protected void searchPackagingUnit() {
	}

	static class ExpenseTransactionTableModel extends ListTableModel<ExpenseTransaction> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2312553573667836103L;

		public ExpenseTransactionTableModel() {
			super(new String[] { "TYPE", "DATE", "VENDOR", "AMOUNT", "VAT", "CREDIT", "REMARKS" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			ExpenseTransaction row = (ExpenseTransaction) getRowData(rowIndex);
			try {
				switch (columnIndex) {
				case 0:
					if (row.getTransactionType() != null) {
						return row.getTransactionType().getName();
					} else {
						return "";
					}

				case 1:
					if (row.getTransactionDate() != null) {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
						return simpleDateFormat.format(row.getTransactionDate());
					} else {
						return "";
					}
				case 2:
					if (row.getVendor().getName() != null) {
						return row.getVendor().getName();
					} else {
						return "";
					}
				case 3:
					return row.getAmount();

				case 4:
					return row.getVatPaid();
				case 5:
					if (row.getCreditCheck()) {
						return "T";
					} else {
						return "F";
					}
				case 6:
					return row.getRemark();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
