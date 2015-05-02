package com.orostock.inventory.ui.form;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
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
import com.floreantpos.model.ExpenseHead;
import com.floreantpos.model.ExpenseTransaction;
import com.floreantpos.model.ExpenseTransactionType;
import com.floreantpos.model.ExpenseTypeEnum;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.ExpenseHeadDAO;
import com.floreantpos.model.dao.ExpenseTransactionDAO;
import com.floreantpos.model.dao.ExpenseTransactionTypeDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class ExpenseTransactionEntryForm extends BeanEditor<ExpenseTransaction> implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5079161684081841222L;
	private DoubleTextField tfAmount;
	private JXComboBox cbVat;
	private JXComboBox cbTransactionType;
	// private JTextField tfVendor;
	private JXComboBox cbVendor;
	private DateTimePicker datePicker;
	private JTextArea taNote;
	private JCheckBox creditCheck;
	private JLabel vendorLabel;
	private JLabel transTypeLabel;
	private JLabel amountLabel;
	private JLabel vatLabel;
	private JLabel dateLabel;
	private JLabel exLabel;
	private InventoryVendor vendor;
	JPanel mainPanel = new JPanel();
	private final JXComboBox cbExHead = new JXComboBox();
	private final JButton btnNewHead = new JButton("New Head");

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

		List<ExpenseHead> heads = ExpenseHeadDAO.getInstance().findAll();
		this.cbExHead.setModel(new DefaultComboBoxModel(heads.toArray(new ExpenseHead[0])));
		cbExHead.setSelectedIndex(-1);

		List<Tax> taxes = TaxDAO.getInstance().findAll();
		this.cbVat.setModel(new DefaultComboBoxModel(taxes.toArray(new Tax[0])));
		cbVat.setSelectedIndex(-1);

		List<InventoryVendor> vendors = InventoryVendorDAO.getInstance().findAllExpenseVendors(true);
		this.cbVendor.setModel(new DefaultComboBoxModel(vendors.toArray(new InventoryVendor[0])));
	}

	public void disableVendorCBox() {
		this.cbVendor.setVisible(false);
	}

	public void enableVendorCBox() {
		this.cbVendor.setVisible(true);

	}

	private void createUI() {
		setLayout(new BorderLayout());
		add(this.mainPanel);
		this.mainPanel.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));

		this.mainPanel.add(vendorLabel = new JLabel("Expense Vendor"), "cell 0 0,alignx trailing");
		this.cbVendor = new JXComboBox();
		this.cbVendor.addActionListener(this);
		this.mainPanel.add(this.cbVendor, "cell 1 0");

		this.mainPanel.add(transTypeLabel = new JLabel("Transaction Type"), "cell 0 3,alignx trailing");
		this.cbTransactionType = new JXComboBox();
		this.cbTransactionType.addActionListener(this);
		this.mainPanel.add(this.cbTransactionType, "cell 1 3");

		this.mainPanel.add(exLabel = new JLabel("Expense head"), "cell 0 5,alignx trailing");
		this.mainPanel.add(this.cbExHead, "cell 1 5");
		this.btnNewHead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExpenseTransactionEntryForm.this.createNewHead();
			}
		});
		this.mainPanel.add(this.btnNewHead, "cell 2 5");

		this.mainPanel.add(amountLabel = new JLabel("Amount (excluding VAT)"), "cell 0 7,alignx trailing");
		this.tfAmount = new DoubleTextField(20);
		this.mainPanel.add(this.tfAmount, "cell 1 7");

		this.mainPanel.add(vatLabel = new JLabel("VAT in %"), "cell 0 9,alignx trailing");
		this.cbVat = new JXComboBox();
		this.cbVat.addActionListener(this);
		this.mainPanel.add(this.cbVat, "cell 1 9");

		this.mainPanel.add(this.creditCheck = new JCheckBox("Credit", false));
		this.mainPanel.add(this.creditCheck, "cell 0 11");

		this.mainPanel.add(dateLabel = new JLabel("Date"), "cell 0 14,alignx trailing");
		this.datePicker = new DateTimePicker();
		datePicker.setFormats("dd-MM-yyyy HH:mm");
		datePicker.setDate(new Date());
		this.mainPanel.add(this.datePicker, "cell 1 14");

		this.mainPanel.add(new JLabel("Note"), "cell 0 16,alignx trailing");
		this.taNote = new JTextArea(4, 10);
		this.taNote.setTabSize(4);
		this.mainPanel.add(new JScrollPane(this.taNote), "cell 1 16");
	}

	protected void createNewHead() {
		ExpenseHeadEntryForm form = new ExpenseHeadEntryForm(new ExpenseHead());
		BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
		dialog.pack();
		dialog.open();
		if (dialog.isCanceled()) {
			return;
		}
		ExpenseHead exHead = (ExpenseHead) form.getBean();
		DefaultComboBoxModel<ExpenseHead> cbModel = (DefaultComboBoxModel) this.cbExHead.getModel();
		cbModel.addElement(exHead);
		cbModel.setSelectedItem(exHead);
	}

	public void setInventoryVendor(InventoryVendor bean) {
		this.vendor = bean;
		this.cbVendor.setSelectedItem(bean);
		this.cbVendor.setEnabled(false);
	}

	public void clearFields() {
		this.tfAmount.setText("");
		this.cbVat.setSelectedIndex(-1);
		this.cbTransactionType.setSelectedIndex(-1);
		this.datePicker.setDate(null);
		this.taNote.setText("");
		this.creditCheck.setSelected(false);
		this.cbVendor.setSelectedIndex(-1);
		this.cbExHead.setSelectedIndex(-1);
	}

	public void setFieldsEnable(boolean enable) {
		this.tfAmount.setEnabled(enable);
		this.cbVat.setEnabled(enable);
		this.cbTransactionType.setEnabled(enable);
		this.datePicker.setEnabled(enable);
		this.taNote.setEnabled(enable);
		this.creditCheck.setEnabled(enable);
		this.cbVendor.setEnabled(enable);
		this.btnNewHead.setEnabled(enable);
		this.vendorLabel.setEnabled(enable);
		this.transTypeLabel.setEnabled(enable);
		this.amountLabel.setEnabled(enable);
		this.vatLabel.setEnabled(enable);
		this.dateLabel.setEnabled(enable);
		this.cbExHead.setEnabled(enable);
		this.exLabel.setEnabled(enable);
	}

	public void setFieldsEnableEdit() {
		this.exLabel.setEnabled(false);
		this.tfAmount.setEnabled(false);
		this.cbVat.setEnabled(false);
		this.cbTransactionType.setEnabled(false);
		this.datePicker.setEnabled(false);
		this.taNote.setEnabled(true);
		this.vendorLabel.setEnabled(false);
		this.transTypeLabel.setEnabled(false);
		this.amountLabel.setEnabled(false);
		this.vatLabel.setEnabled(false);
		this.dateLabel.setEnabled(false);
		this.cbVendor.setEnabled(false);
		this.creditCheck.setEnabled(true);
		this.cbExHead.setEnabled(false);
		this.btnNewHead.setEnabled(false);
	}

	public boolean save() {
		Session session = ExpenseTransactionDAO.getInstance().createNewSession();
		if (session != null) {
			Transaction tx = session.beginTransaction();
			boolean actionPerformed = false;
			try {
				if (updateModel()) {
					ExpenseTransaction expenseTransaction = (ExpenseTransaction) getBean();
					if (expenseTransaction.getAmount() == null || expenseTransaction.getAmount().isNaN()) {
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Amount!!");
						actionPerformed = false;
					} else if (expenseTransaction.getVatPaid() == null || expenseTransaction.getVatPaid().getRate() < 0) {
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid VAT!!");
						actionPerformed = false;
					} else if (expenseTransaction.getInventoryVendor() == null) {
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
				} else {
					tx.rollback();
					return false;
				}
			} catch (RuntimeException e) {
				POSMessageDialog.showError(e.getMessage(), e);
			} catch (Exception e) {
				if (tx != null) {
					tx.rollback();
				}
				session.close();
				POSMessageDialog.showError(e.getMessage(), e);
				return false;
			}
		}
		return true;
	}

	protected void updateView() {
		ExpenseTransaction expenseTransaction = (ExpenseTransaction) getBean();
		if (expenseTransaction == null) {
			return;
		}
		if (expenseTransaction.getInventoryVendor() != null) {
			this.cbVendor.setSelectedItem(expenseTransaction.getInventoryVendor());
		}
		if (expenseTransaction.getAmount() != null && !expenseTransaction.getAmount().isNaN()) {
			this.tfAmount.setText(expenseTransaction.getAmount().toString());
		}
		if (expenseTransaction.getVatPaid() != null) {
			this.cbVat.setSelectedItem(expenseTransaction.getVatPaid());
		}
		if (expenseTransaction.getRemark() != null) {
			this.taNote.setText(expenseTransaction.getRemark());
		}
		if (expenseTransaction.getExpenseTransactionType() != null) {
			this.cbTransactionType.setSelectedItem(expenseTransaction.getExpenseTransactionType());
		}
		if (expenseTransaction.getTransactionDate() != null) {
			this.datePicker.setDate(expenseTransaction.getTransactionDate());
		}
		this.creditCheck.setSelected(expenseTransaction.isCreditCheck());
		if (expenseTransaction.getExpenseHead() != null) {
			this.cbExHead.setSelectedItem(expenseTransaction.getExpenseHead());
		}
	}

	public void createNew() {
		ExpenseTransaction exp = new ExpenseTransaction();
		setBean(exp);
	}

	protected boolean updateModel() throws IllegalModelStateException {
		ExpenseTransaction transaction = (ExpenseTransaction) getBean();
		if (transaction == null) {
			transaction = new ExpenseTransaction();
		}
		transaction.setInventoryVendor(this.vendor);
		if (transaction.getInventoryVendor() == null) {
			transaction.setInventoryVendor((InventoryVendor) this.cbVendor.getSelectedItem());
		}
		ExpenseTransactionType transType = (ExpenseTransactionType) this.cbTransactionType.getSelectedItem();
		transaction.setExpenseTransactionType(transType);
		transaction.setAmount(Double.valueOf(this.tfAmount.getDouble()));
		if (this.cbVat.getSelectedItem() != null) {
			transaction.setVatPaid(((Tax) this.cbVat.getSelectedItem()));
		}
		transaction.setCreditCheck(creditCheck.isSelected());
		transaction.setTransactionDate(this.datePicker.getDate());
		transaction.setExpenseHead((ExpenseHead) this.cbExHead.getSelectedItem());
		transaction.setRemark(this.taNote.getText());
		return true;
	}

	public String getDisplayText() {
		return "New Expense";
	}

	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0.paramString());
		this.tfAmount.setVisible(true);
		this.cbVat.setVisible(true);
		this.cbTransactionType.setVisible(true);
		this.datePicker.setVisible(true);
		this.taNote.setVisible(true);
		this.creditCheck.setVisible(true);
		this.cbExHead.setVisible(true);
		this.btnNewHead.setVisible(true);
	}

}
