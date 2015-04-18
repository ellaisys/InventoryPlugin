package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXDatePicker;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.ExpenseTransaction;
import com.floreantpos.model.ExpenseTransactionType;
import com.floreantpos.model.ExpenseTypeEnum;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.ExpenseTransactionDAO;
import com.floreantpos.model.dao.ExpenseTransactionTypeDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class ExpenseTransactionEntryForm extends BeanEditor<ExpenseTransaction> implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5079161684081841222L;
	private JTextField tfVendor;
	private DoubleTextField tfAmount;
	private DoubleTextField tfVAT;
	private JXComboBox cbTransactionType;
	private JXDatePicker datePicker;
	private JTextArea taNote;
	private JCheckBox creditCheck;
	private InventoryVendor vendor;

	public ExpenseTransactionEntryForm() {
		createUI();
		List<ExpenseTransactionType> transactionTypes = ExpenseTransactionTypeDAO.getInstance().findAll();
		if (transactionTypes.size() == 0) {
			ExpenseTransactionType transactionType = new ExpenseTransactionType();
			transactionType.setName("FIXED");
			transactionType.setExpenseTypeEnum(ExpenseTypeEnum.FIXED);
			ExpenseTransactionTypeDAO.getInstance().save(transactionType);

			transactionType = new ExpenseTransactionType();
			transactionType.setName("FIXED RECURRING");
			transactionType.setExpenseTypeEnum(ExpenseTypeEnum.FIXED_RECURRING);
			ExpenseTransactionTypeDAO.getInstance().save(transactionType);

			transactionType = new ExpenseTransactionType();
			transactionType.setName("VARIABLE");
			transactionType.setExpenseTypeEnum(ExpenseTypeEnum.VARIABLE);
			ExpenseTransactionTypeDAO.getInstance().save(transactionType);

			transactionTypes = ExpenseTransactionTypeDAO.getInstance().findAll();
		}

		this.cbTransactionType.setModel(new DefaultComboBoxModel(transactionTypes.toArray(new ExpenseTransactionType[0])));
	}

	private void createUI() {
		setLayout(new MigLayout());

		add(new JLabel("Vendor"));
		this.tfVendor = new JTextField(20);
		this.tfVendor.setEnabled(false);
		add(this.tfVendor, "grow, wrap");

		add(new JLabel("Transaction Type"));
		this.cbTransactionType = new JXComboBox();
		this.cbTransactionType.addActionListener(this);
		add(this.cbTransactionType, "wrap, w 150px");

		add(new JLabel("Amount"));
		this.tfAmount = new DoubleTextField(20);
		add(this.tfAmount, "grow, wrap");

		add(new JLabel("VAT Paid"));
		this.tfVAT = new DoubleTextField(20);
		this.tfVAT.setText("0.0");
		add(this.tfVAT, "grow, wrap");

		add(this.creditCheck = new JCheckBox("Credit", false));
		add(this.creditCheck, "grow, wrap");

		add(new JLabel("Date"));
		this.datePicker = new JXDatePicker(new Date());
		add(this.datePicker, "wrap, w 200px");

		add(new JLabel("Note"));
		this.taNote = new JTextArea();
		add(new JScrollPane(this.taNote), "grow, h 100px, wrap");
	}

	public void setInventoryVendor(InventoryVendor bean) {
		this.vendor = bean;
		this.tfVendor.setText(vendor.getName());
	}

	public boolean save() {
		Session session = ExpenseTransactionDAO.getInstance().createNewSession();
		Transaction tx = session.beginTransaction();
		boolean actionPerformed = false;
		try {
			if (updateModel()) {
				ExpenseTransaction expenseTransaction = (ExpenseTransaction) getBean();
				if (expenseTransaction.getAmount() == null || expenseTransaction.getAmount().isNaN()) {
					POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Amount!!");
					actionPerformed = false;
				} else if (expenseTransaction.getVatPaid() == null || expenseTransaction.getVatPaid().isNaN()) {
					POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid VAT!!");
					actionPerformed = false;
				} else {
					ExpenseTransactionDAO dao = ExpenseTransactionDAO.getInstance();
					dao.saveOrUpdate(expenseTransaction);
					actionPerformed = true;
				}
			}
			if (actionPerformed) {
				tx.commit();
			} else {
				tx.rollback();
				return false;
			}
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			if (session != null) {
				session.close();
			}
			POSMessageDialog.showError(e.getMessage(), e);
			return false;
		}
		return true;
	}

	protected void updateView() {
	}

	protected boolean updateModel() throws IllegalModelStateException {
		ExpenseTransaction transaction = (ExpenseTransaction) getBean();
		ExpenseTransactionType transType = (ExpenseTransactionType) this.cbTransactionType.getSelectedItem();
		transaction.setVendor(vendor);
		transaction.setTransactionType(transType);
		transaction.setAmount(Double.valueOf(this.tfAmount.getDouble()));
		transaction.setVatPaid(Double.valueOf(this.tfVAT.getDouble()));
		transaction.setCreditCheck(creditCheck.isSelected());
		transaction.setTransactionDate(this.datePicker.getDate());
		transaction.setRemark(this.taNote.getText());
		return true;
	}

	public String getDisplayText() {
		return "New Expense";
	}

	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0.paramString());
		this.tfVendor.setVisible(true);
		this.tfAmount.setVisible(true);
		this.tfVAT.setVisible(true);
		this.cbTransactionType.setVisible(true);
		this.datePicker.setVisible(true);
		this.taNote.setVisible(true);
		this.creditCheck.setVisible(true);
	}

	public void setFieldsEnable(boolean enable) {
		this.tfVendor.setEnabled(enable);
		this.tfAmount.setEnabled(enable);
		this.tfVAT.setEnabled(enable);
		this.cbTransactionType.setEnabled(enable);
		this.datePicker.setEnabled(enable);
		this.taNote.setEnabled(enable);
		this.creditCheck.setEnabled(enable);
	}
}
