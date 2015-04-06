package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXDatePicker;

import com.floreantpos.model.InOutEnum;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.InventoryTransactionType;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.PurchaseOrder;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.InventoryTransactionDAO;
import com.floreantpos.model.dao.InventoryTransactionTypeDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.model.dao.PurchaseOrderDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class InventoryTransactionEntryForm extends
		BeanEditor<InventoryTransaction> implements ActionListener {
	private JTextField tfItem;
	private DoubleTextField tfUnitPrice;
	private JXComboBox cbTransactionType;
	private JXComboBox cbVendor;
	private JXComboBox inWareHouse;
	private JXComboBox outWareHouse;
	private JLabel vendorLabel;
	private JLabel inWareHouseLabel;
	private JLabel outWareHouseLabel;
	private JLabel priceLabel;
	private JXDatePicker datePicker;
	private JTextArea taNote;
	private IntegerTextField tfUnit;
	private JTextField tfPO;
	private InventoryItem inventoryItem;

	public InventoryTransactionEntryForm() {
		createUI();

		List<InventoryVendor> vendors = InventoryVendorDAO.getInstance()
				.findAll();
		List<InventoryWarehouse> warehouse = InventoryWarehouseDAO
				.getInstance().findAll();

		List<InventoryTransactionType> transactionTypes = InventoryTransactionTypeDAO
				.getInstance().findAll();

		if (transactionTypes.size() == 0) {
			InventoryTransactionType transactionType = new InventoryTransactionType();
			transactionType.setName("IN");
			transactionType.setInOrOutEnum(InOutEnum.IN);
			InventoryTransactionTypeDAO.getInstance().save(transactionType);

			transactionType = new InventoryTransactionType();
			transactionType.setName("OUT");
			transactionType.setInOrOutEnum(InOutEnum.OUT);
			InventoryTransactionTypeDAO.getInstance().save(transactionType);

			transactionType = new InventoryTransactionType();
			transactionType.setName("STOCK MOVEMENT");
			transactionType.setInOrOutEnum(InOutEnum.MOVEMENT);
			InventoryTransactionTypeDAO.getInstance().save(transactionType);

			transactionTypes = InventoryTransactionTypeDAO.getInstance()
					.findAll();
		}

		this.cbTransactionType.setModel(new DefaultComboBoxModel(
				transactionTypes.toArray(new InventoryTransactionType[0])));
		this.cbVendor.setModel(new DefaultComboBoxModel(vendors
				.toArray(new InventoryVendor[0])));
		this.inWareHouse.setModel(new DefaultComboBoxModel(warehouse
				.toArray(new InventoryWarehouse[0])));
		this.outWareHouse.setModel(new DefaultComboBoxModel(warehouse
				.toArray(new InventoryWarehouse[0])));

	}

	private void createUI() {
		setLayout(new MigLayout());

		// add(new JLabel("Reference#"));
		// this.tfPO = new JTextField(10);
		// add(this.tfPO, "wrap, w 150px");

		add(new JLabel("Transaction Type"));
		this.cbTransactionType = new JXComboBox();
		this.cbTransactionType.addActionListener(this);
		add(this.cbTransactionType, "wrap, w 150px");

		add(new JLabel("Item"));
		this.tfItem = new JTextField(20);
		this.tfItem.setEnabled(false);
		add(this.tfItem, "grow, wrap");

		add(this.priceLabel = new JLabel("Unit Price"));
		this.tfUnitPrice = new DoubleTextField(20);
		add(this.tfUnitPrice, "grow, wrap");

		add(new JLabel("Unit"));
		this.tfUnit = new IntegerTextField(20);
		add(this.tfUnit, "grow, wrap");

		add(new JLabel("Date"));
		this.datePicker = new JXDatePicker(new Date());
		add(this.datePicker, "wrap, w 200px");

		add(this.vendorLabel = new JLabel("Vendor"));
		this.cbVendor = new JXComboBox();
		add(this.cbVendor, "wrap, w 200px");

		add(this.inWareHouseLabel = new JLabel("In-Warehouse"));
		this.inWareHouse = new JXComboBox();
		add(this.inWareHouse, "wrap, w 200px");

		add(this.outWareHouseLabel = new JLabel("Out-Warehouse"));
		this.outWareHouse = new JXComboBox();
		add(this.outWareHouse, "wrap, w 200px");

		add(new JLabel("Note"));
		this.taNote = new JTextArea();
		add(new JScrollPane(this.taNote), "grow, h 100px, wrap");
		this.outWareHouse.setVisible(false);
		this.outWareHouseLabel.setVisible(false);
	}

	public void setInventoryItem(InventoryItem item) {
		this.inventoryItem = item;
		this.tfItem.setText(item.getName());
	}

	public boolean save() {
		Session session = null;
		Transaction tx = null;
		try {
			if (!updateModel()) {
				return false;
			}

			InventoryTransaction inventoryTransaction = (InventoryTransaction) getBean();

			InOutEnum inOutEnum = InOutEnum.fromInt(inventoryTransaction
					.getTransactionType().getInOrOut().intValue());
			switch (inOutEnum) {
			case IN:
				this.inventoryItem
						.setTotalPackages(Integer
								.valueOf(this.inventoryItem.getTotalPackages()
										.intValue()
										+ inventoryTransaction.getQuantity()
												.intValue()));
				this.inventoryItem.setLastUpdateDate(new Date());
				break;
			case OUT:
				this.inventoryItem
						.setTotalPackages(Integer
								.valueOf(this.inventoryItem.getTotalPackages()
										.intValue()
										- inventoryTransaction.getQuantity()
												.intValue()));
				this.inventoryItem.setLastUpdateDate(new Date());
				break;
			}

			session = InventoryTransactionDAO.getInstance().createNewSession();
//			tx = session.beginTransaction();
			PurchaseOrder purchaseOrder = inventoryTransaction.getReferenceNo();
			PurchaseOrderDAO.getInstance().saveOrUpdate(purchaseOrder, session);
			InventoryTransactionDAO.getInstance().saveOrUpdate(
					inventoryTransaction, session);
			InventoryItemDAO.getInstance().saveOrUpdate(this.inventoryItem);
//			tx.commit();
		} catch (Exception e) {
//			if (tx != null) {
////				tx.rollback();
//			}

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
		InventoryTransaction transaction = (InventoryTransaction) getBean();

		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setOrderId(UUID.randomUUID().toString());
		InventoryTransactionType transType = (InventoryTransactionType) this.cbTransactionType
				.getSelectedItem();
		transaction
				.setTransactionType(transType);
		transaction.setReferenceNo(purchaseOrder);
		transaction.setInventoryItem(this.inventoryItem);
		transaction
		.setVendor((InventoryVendor) this.cbVendor.getSelectedItem());
		switch(transType.getInOutEnum()){
		case IN:
			transaction
			.setVendor((InventoryVendor) this.cbVendor.getSelectedItem());
			transaction.setToWarehouse((InventoryWarehouse) this.inWareHouse
					.getSelectedItem());
			transaction.setUnitPrice(Double.valueOf(this.tfUnitPrice.getDouble()));
			break;
		case OUT:
			transaction
			.setVendor((InventoryVendor) this.cbVendor.getSelectedItem());
			transaction.setFromWarehouse((InventoryWarehouse) this.outWareHouse
					.getSelectedItem());
			transaction.setUnitPrice(Double.valueOf(this.tfUnitPrice.getDouble()));
			break;
		case MOVEMENT:
			transaction.setToWarehouse((InventoryWarehouse) this.inWareHouse
					.getSelectedItem());
			transaction.setFromWarehouse((InventoryWarehouse) this.outWareHouse
					.getSelectedItem());
			break;
		}
		transaction.setQuantity(Integer.valueOf(this.tfUnit.getInteger()));
		transaction.setTransactionDate(this.datePicker.getDate());
		return true;
	}

	public String getDisplayText() {
		return "New transaction";
	}

	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0.paramString());
		InventoryTransactionType type = (InventoryTransactionType) (((DefaultComboBoxModel<?>)((JXComboBox) arg0
				.getSource()).getModel()).getSelectedItem());
		switch(type.getInOutEnum()){
		case IN:
			this.outWareHouse.setVisible(false);
			this.inWareHouse.setVisible(true);
			this.cbVendor.setVisible(true);
			this.outWareHouseLabel.setVisible(false);
			this.inWareHouseLabel.setVisible(true);
			this.vendorLabel.setVisible(true);
			this.priceLabel.setVisible(true);
			this.tfUnitPrice.setVisible(true);

			break;
		case OUT:
			this.outWareHouse.setVisible(true);
			this.inWareHouse.setVisible(false);
			this.cbVendor.setVisible(true);
			
			this.outWareHouseLabel.setVisible(true);
			this.inWareHouseLabel.setVisible(false);
			this.vendorLabel.setVisible(true);
			this.priceLabel.setVisible(true);
			this.tfUnitPrice.setVisible(true);
			break;
		case MOVEMENT:
			this.outWareHouse.setVisible(true);
			this.inWareHouse.setVisible(true);
			this.cbVendor.setVisible(false);
			
			this.outWareHouseLabel.setVisible(true);
			this.inWareHouseLabel.setVisible(true);
			this.vendorLabel.setVisible(false);
			this.priceLabel.setVisible(false);
			this.tfUnitPrice.setVisible(false);
			break;
		}

	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.ui.InventoryTransactionEntryForm JD-Core Version:
 * 0.6.0
 */