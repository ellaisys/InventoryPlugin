package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXComboBox;

import com.date.picker.DateTimePicker;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.ExpenseTransaction;
import com.floreantpos.model.ExpenseTransactionType;
import com.floreantpos.model.ExpenseTypeEnum;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.ExpenseTransactionDAO;
import com.floreantpos.model.dao.ExpenseTransactionTypeDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class ExpenseTransactionEntryForm extends BeanEditor<ExpenseTransaction> implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5079161684081841222L;
	// private JTextField tfVendor;
	private DoubleTextField tfAmount;
	private DoubleTextField tfVAT;
	private JXComboBox cbTransactionType;
	private JXComboBox cbVendor;
	private DateTimePicker datePicker;
	private JTextArea taNote;
	private JCheckBox creditCheck;
	private JLabel vendorLabel;
	private JLabel transTypeLabel;
	private JLabel amountLabel;
	private JLabel vatLabel;
	private JLabel dateLabel;
	private InventoryVendor vendor;
	JPanel mainPanel = new JPanel();

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

		List<InventoryVendor> vendors = InventoryVendorDAO.getInstance().findAll();
		this.cbVendor.setModel(new DefaultComboBoxModel(vendors.toArray(new InventoryVendor[0])));
	}

	private void createUI() {
		setLayout(new MigLayout());

		add(vendorLabel = new JLabel("Vendor"));
		// this.tfVendor = new JTextField(20);
		// this.tfVendor.setEnabled(false);
		// add(this.tfVendor, "grow, wrap");

		this.cbVendor = new JXComboBox();
		this.cbVendor.addActionListener(this);
		add(this.cbVendor, "wrap, w 150px");

		add(transTypeLabel = new JLabel("Transaction Type"));
		this.cbTransactionType = new JXComboBox();
		this.cbTransactionType.addActionListener(this);
		add(this.cbTransactionType, "wrap, w 150px");

		add(amountLabel = new JLabel("Amount"));
		this.tfAmount = new DoubleTextField(20);
		add(this.tfAmount, "grow, wrap");

		add(vatLabel = new JLabel("VAT Paid"));
		this.tfVAT = new DoubleTextField(20);
		this.tfVAT.setText("0.0");
		add(this.tfVAT, "grow, wrap");

		add(this.creditCheck = new JCheckBox("Credit", false));
		add(this.creditCheck, "grow, wrap");

		add(dateLabel = new JLabel("Date"));
		this.datePicker = new DateTimePicker();
		datePicker.setFormats("dd-MM-yyyy HH:mm");
		datePicker.setDate(new Date());
		add(this.datePicker, "wrap, w 200px");

		add(new JLabel("Note"));
		this.taNote = new JTextArea();
		add(new JScrollPane(this.taNote), "grow, h 100px, wrap");

	}

	public void setInventoryVendor(InventoryVendor bean) {
		this.vendor = bean;
		// this.tfVendor.setText(vendor.getName());
	}

	public void createNew() {
		clearFields();
	}

	public void clearFields() {
		// this.tfVendor.setText("");
		this.tfAmount.setText("");
		this.tfVAT.setText("");
		this.taNote.setText("");
		this.creditCheck.setSelected(false);
		this.cbVendor.setSelectedIndex(-1);
	}

	public void setFieldsEnable(boolean enable) {
		// this.tfVendor.setEnabled(enable);
		this.tfAmount.setEnabled(enable);
		this.tfVAT.setEnabled(enable);
		this.cbTransactionType.setEnabled(enable);
		this.datePicker.setEnabled(enable);
		this.taNote.setEnabled(enable);
		this.creditCheck.setEnabled(enable);
		this.cbVendor.setEnabled(false);
	}

	public void setFieldsEnableEdit() {
		// this.tfVendor.setEnabled(false);
		this.tfAmount.setEnabled(false);
		this.tfVAT.setEnabled(false);
		this.cbTransactionType.setEnabled(false);
		this.datePicker.setEnabled(false);
		this.taNote.setEnabled(true);
		this.creditCheck.setEnabled(false);
		this.vendorLabel.setEnabled(false);
		this.transTypeLabel.setEnabled(false);
		this.amountLabel.setEnabled(false);
		this.vatLabel.setEnabled(false);
		this.dateLabel.setEnabled(false);
		this.cbVendor.setEnabled(false);
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
				} else if (expenseTransaction.getVendor() == null) {
					POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please select a valid Vendor!!");
					actionPerformed = false;
				} else {
					ExpenseTransactionDAO dao = ExpenseTransactionDAO.getInstance();
					dao.saveOrUpdate(expenseTransaction);
					actionPerformed = true;
				}
			}
			if (actionPerformed) {
				tx.commit();
				POSMessageDialog.showMessage(BackOfficeWindow.getInstance(), "Expense Transaction done successfully");
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
		ExpenseTransaction expenseTransaction = (ExpenseTransaction) getBean();
		if (expenseTransaction == null) {
			return;
		}
		if (expenseTransaction.getVendor() != null) {
			// this.tfVendor.setText(expenseTransaction.getVendor().getName());
			this.cbVendor.setSelectedItem(expenseTransaction.getVendor());
			setInventoryVendor(expenseTransaction.getVendor());
		}
		if (expenseTransaction.getAmount() != null && !expenseTransaction.getAmount().isNaN()) {
			this.tfAmount.setText(expenseTransaction.getAmount().toString());
		}
		if (expenseTransaction.getVatPaid() != null && !expenseTransaction.getVatPaid().isNaN()) {
			this.tfVAT.setText(expenseTransaction.getVatPaid().toString());
		}
		if (expenseTransaction.getRemark() != null) {
			this.taNote.setText(expenseTransaction.getRemark().toString());
		}
		if (expenseTransaction.getTransactionType() != null) {
			this.cbTransactionType.setSelectedItem(expenseTransaction.getTransactionType());
		}
		if (expenseTransaction.getTransactionDate() != null) {
			this.datePicker.setDate(expenseTransaction.getTransactionDate());
		}
		if (expenseTransaction.getCreditCheck() != null) {
			this.creditCheck.setSelected(expenseTransaction.getCreditCheck());
		}

	}

	protected boolean updateModel() throws IllegalModelStateException {
		ExpenseTransaction model = (ExpenseTransaction) getBean();
		if (model == null) {
			model = new ExpenseTransaction();
			setBean(model);
		}
		if (model.getVendor() == null) {
			ExpenseTransactionType transType = (ExpenseTransactionType) this.cbTransactionType.getSelectedItem();
			InventoryVendor iVendor = (InventoryVendor) this.cbVendor.getSelectedItem();
			model.setVendor(iVendor);
			model.setTransactionType(transType);
			model.setAmount(Double.valueOf(this.tfAmount.getDouble()));
			model.setVatPaid(Double.valueOf(this.tfVAT.getDouble()));
			model.setCreditCheck(creditCheck.isSelected());
			model.setTransactionDate(this.datePicker.getDate());
		}
		model.setRemark(this.taNote.getText());
		return true;
	}

	public String getDisplayText() {
		return "New Expense";
	}

	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0.paramString());
		// this.tfVendor.setVisible(true);
		this.tfAmount.setVisible(true);
		this.tfVAT.setVisible(true);
		this.cbTransactionType.setVisible(true);
		this.datePicker.setVisible(true);
		this.taNote.setVisible(true);
		this.creditCheck.setVisible(true);
	}

}
