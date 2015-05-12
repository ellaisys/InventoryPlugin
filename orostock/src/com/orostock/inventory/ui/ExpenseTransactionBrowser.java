package com.orostock.inventory.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.ExpenseTransactionDAO;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.form.ExpenseTransactionEntryForm;

public class ExpenseTransactionBrowser extends ModelBrowser<ExpenseTransaction> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4133361713025286200L;
	private JButton btnNewExpense = new JButton("NEW EXPENSE");

	public ExpenseTransactionBrowser() {
		super(new ExpenseTransactionEntryForm());
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		this.btnNewExpense.setActionCommand(Command.NEW_EXPENSE.name());
		this.btnNewExpense.setEnabled(true);
		init(new ExpenseTransactionTableModel(), new Dimension(600, 400), new Dimension(400, 400));
		// "DATE", "TYPE", "HEAD", "VENDOR", "AMOUNT", "VAT", "CREDIT"
		browserTable.getColumn("DATE").setPreferredWidth(70);
		browserTable.getColumn("TYPE").setPreferredWidth(70);
		browserTable.getColumn("HEAD").setPreferredWidth(70);
		browserTable.getColumn("VENDOR").setPreferredWidth(120);
		browserTable.getColumn("AMOUNT").setPreferredWidth(40);
		browserTable.getColumn("VAT").setPreferredWidth(20);
		browserTable.getColumn("CREDIT").setPreferredWidth(10);
		this.invalidate();
		hideDeleteBtn();
		hideNewBtn();
		beanEditor.setFieldsEnable(false);
		refreshTable();
	}

	public void loadData() {
		List<ExpenseTransaction> expense = ExpenseTransactionDAO.getInstance().findByCurrentMonth();
		ExpenseTransactionTableModel tableModel = (ExpenseTransactionTableModel) this.browserTable.getModel();
		tableModel.setRows(expense);
	}

	protected JButton getAdditionalButton() {
		return this.btnNewExpense;
	}

	public void refreshTable() {
		loadData();
		super.refreshTable();
	}

	public void refreshUITable() {
		super.refreshTable();
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		ExpenseTransaction bean = (ExpenseTransaction) this.beanEditor.getBean();
		if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			beanEditor.setFieldsEnableEdit();
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
		beanEditor.setFieldsEnable(false);
		ExpenseTransaction bean = (ExpenseTransaction) this.beanEditor.getBean();
		if ((bean != null) && (bean.getInventoryVendor() != null)) {
			this.btnNewExpense.setEnabled(true);
			Method m;
			try {
				m = Class.forName("com.orostock.inventory.ui.form.ExpenseTransactionEntryForm").getMethod("setInventoryVendor", InventoryVendor.class);
				m.invoke(beanEditor, false);
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
			// et.setInventoryVendor(bean.getInventoryVendor());
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
			super(new String[] { "DATE", "TYPE", "HEAD", "VENDOR", "AMOUNT", "VAT", "CREDIT" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			ExpenseTransaction row = (ExpenseTransaction) getRowData(rowIndex);
			try {
				switch (columnIndex) {
				case 0:
					if (row.getTransactionDate() != null) {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
						return simpleDateFormat.format(row.getTransactionDate());
					} else {
						return "";
					}
				case 1:
					if (row.getExpenseTransactionType() != null) {
						return row.getExpenseTransactionType().getName();
					} else {
						return "";
					}
				case 2:
					if (row.getExpenseHead() != null) {
						return row.getExpenseHead().getName();
					} else {
						return "";
					}
				case 3:
					if (row.getInventoryVendor().getName() != null) {
						return row.getInventoryVendor().getName();
					} else {
						return "";
					}
				case 4:
					return "Rs" + formatDouble(row.getAmount());

				case 5:
					return formatDouble(row.getVatPaid().getRate()) + " %";
				case 6:
					if (row.isCreditCheck()) {
						return "T";
					} else {
						return "F";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
