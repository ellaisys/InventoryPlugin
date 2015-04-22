package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

import com.date.picker.DateTimePicker;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InOutEnum;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.InventoryTransactionType;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.InventoryWarehouseItem;
import com.floreantpos.model.PurchaseOrder;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.InventoryLocationDAO;
import com.floreantpos.model.dao.InventoryTransactionDAO;
import com.floreantpos.model.dao.InventoryTransactionTypeDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.model.dao.InventoryWarehouseItemDAO;
import com.floreantpos.model.dao.PurchaseOrderDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class InventoryTransactionEntryForm extends BeanEditor<InventoryTransaction> implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2509122276993566916L;

	private JTextField tfItem;
	private DoubleTextField tfUnitPrice;
	private DoubleTextField tfVAT;
	private JXComboBox cbTransactionType;
	private JXComboBox cbVendor;
	private JXComboBox inWareHouse;
	private JXComboBox outWareHouse;
	private JLabel vendorLabel;
	private JLabel inWareHouseLabel;
	private JLabel outWareHouseLabel;
	private JLabel priceLabel;
	private JLabel itemCountLabel;
	private DateTimePicker datePicker;
	private JTextArea taNote;
	private JCheckBox creditCheck;
	private DoubleTextField tfUnit;
	private InventoryItem inventoryItem;
	private JLabel dateLabel;
	private JLabel vatLabel;
	private JLabel itemLabel;
	private JLabel transLabel;

	public InventoryTransactionEntryForm() {
		createUI();

		List<InventoryVendor> vendors = InventoryVendorDAO.getInstance().findAll();
		List<InventoryWarehouse> warehouse = InventoryWarehouseDAO.getInstance().findAll();

		List<InventoryTransactionType> transactionTypes = InventoryTransactionTypeDAO.getInstance().findAll();

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
			transactionType.setName("ADJUSTMENT");
			transactionType.setInOrOutEnum(InOutEnum.ADJUSTMENT);
			InventoryTransactionTypeDAO.getInstance().save(transactionType);

			transactionType = new InventoryTransactionType();
			transactionType.setName("WASTAGE");
			transactionType.setInOrOutEnum(InOutEnum.WASTAGE);
			InventoryTransactionTypeDAO.getInstance().save(transactionType);

			transactionType = new InventoryTransactionType();
			transactionType.setName("STOCK MOVEMENT");
			transactionType.setInOrOutEnum(InOutEnum.MOVEMENT);
			InventoryTransactionTypeDAO.getInstance().save(transactionType);

			transactionTypes = InventoryTransactionTypeDAO.getInstance().findAll();
		}

		this.cbTransactionType.setModel(new DefaultComboBoxModel(transactionTypes.toArray(new InventoryTransactionType[0])));
		this.cbTransactionType.setSelectedIndex(1);

		this.cbVendor.setModel(new DefaultComboBoxModel(vendors.toArray(new InventoryVendor[0])));
		this.inWareHouse.setModel(new DefaultComboBoxModel(warehouse.toArray(new InventoryWarehouse[0])));
		this.outWareHouse.setModel(new DefaultComboBoxModel(warehouse.toArray(new InventoryWarehouse[0])));

	}

	private void updateAverageItemPrice(InventoryItem item, int newRecepieUnits, double totalPaid) {
		InventoryWarehouseItemDAO dao = InventoryWarehouseItemDAO.getInstance();
		List<InventoryWarehouseItem> wareItemList = dao.findByInventoryItem(item);
		double totalRecepieUnits = 0;
		if (wareItemList != null && !wareItemList.isEmpty()) {
			for (InventoryWarehouseItem i : wareItemList) {
				totalRecepieUnits = totalRecepieUnits + i.getTotalRecepieUnits();
			}
		}
		double averagePrice = inventoryItem.getAverageRUnitPrice();
		if (newRecepieUnits > 0) {
			averagePrice = ((totalRecepieUnits * item.getAverageRUnitPrice()) + totalPaid) / (totalRecepieUnits + newRecepieUnits);
		}
		inventoryItem.setAverageRUnitPrice(averagePrice);
		InventoryItemDAO.getInstance().saveOrUpdate(inventoryItem);
	}

	private void createUI() {
		setLayout(new MigLayout());

		add(this.transLabel = new JLabel("Transaction Type"));
		this.cbTransactionType = new JXComboBox();
		this.cbTransactionType.addActionListener(this);
		add(this.cbTransactionType, "wrap, w 150px");

		add(this.itemLabel = new JLabel("Item"));
		this.tfItem = new JTextField(20);
		this.tfItem.setEnabled(false);
		add(this.tfItem, "grow, wrap");

		add(this.priceLabel = new JLabel("Total Price"));
		this.tfUnitPrice = new DoubleTextField(20);
		add(this.tfUnitPrice, "grow, wrap");

		add(this.vatLabel = new JLabel("VAT Paid"));
		this.tfVAT = new DoubleTextField(20);
		add(this.tfVAT, "grow, wrap");

		// add(new JLabel("Credit"));
		add(this.creditCheck = new JCheckBox("Credit", false));
		add(this.creditCheck, "grow, wrap");

		add(this.itemCountLabel = new JLabel("No of items"));
		this.tfUnit = new DoubleTextField(20);
		add(this.tfUnit, "grow, wrap");

		add(this.dateLabel = new JLabel("Date"));
		this.datePicker = new DateTimePicker(new Date());
		add(this.datePicker, "wrap, w 200px");

		add(this.vendorLabel = new JLabel("Vendor"));
		this.cbVendor = new JXComboBox();
		add(this.cbVendor, "wrap, w 200px");

		add(this.outWareHouseLabel = new JLabel("Out-Warehouse"));
		this.outWareHouse = new JXComboBox();
		add(this.outWareHouse, "wrap, w 200px");

		add(this.inWareHouseLabel = new JLabel("In-Warehouse"));
		this.inWareHouse = new JXComboBox();
		add(this.inWareHouse, "wrap, w 200px");

		add(new JLabel("Note"));
		this.taNote = new JTextArea();
		add(new JScrollPane(this.taNote), "grow, h 100px, wrap");
		this.outWareHouse.setVisible(false);
		this.outWareHouseLabel.setVisible(false);
	}

	public void setInventoryItem(InventoryItem item) {
		this.inventoryItem = item;
		this.tfItem.setText(item.getName());
		this.itemCountLabel.setText("Units (" + item.getPackagingUnit().getName() + ")");
	}

	String formatDouble(double d) {
		NumberFormat f = new DecimalFormat("0.##");
		return f.format(d);
	}

	public boolean save() {
		Session session = InventoryTransactionDAO.getInstance().createNewSession();
		if (session != null) {
			Transaction tx = session.beginTransaction();

			boolean actionPerformed = false;
			try {
				if (!updateModel()) {
					return false;
				}
				int reorderLevel = inventoryItem.getPackageReorderLevel();
				int replenishLevel = inventoryItem.getPackageReplenishLevel();
				InventoryTransaction inventoryTransaction = (InventoryTransaction) getBean();
				if (inventoryTransaction.getQuantity() == null || inventoryTransaction.getQuantity().isNaN()
						|| inventoryTransaction.getQuantity() <= 0) {
					POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Quantity!!");
					actionPerformed = false;
					return false;
				} else if (inventoryTransaction.getVatPaid() == null || inventoryTransaction.getVatPaid().isNaN()) {
					POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid VAT!!");
					actionPerformed = false;
					return false;
				} else if (inventoryTransaction.getUnitPrice() == null || inventoryTransaction.getUnitPrice().isNaN()) {
					POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Price!!");
					actionPerformed = false;
					return false;
				} else {
					InventoryTransactionDAO.getInstance().saveOrUpdate(inventoryTransaction, session);
				}
				InventoryLocationDAO locDAO = InventoryLocationDAO.getInstance();
				List<InventoryLocation> listLocIn = locDAO.findByInventoryItem((InventoryWarehouse) this.inWareHouse.getSelectedItem());
				InventoryLocation locationIN = null;
				if (listLocIn != null && !listLocIn.isEmpty()) {
					locationIN = listLocIn.get(0);
				}

				List<InventoryLocation> listLocOut = locDAO.findByInventoryItem((InventoryWarehouse) this.outWareHouse.getSelectedItem());
				InventoryLocation locationOUT = null;
				if (listLocOut != null && !listLocOut.isEmpty()) {
					locationOUT = listLocOut.get(0);
				}
				InOutEnum inOutEnum = InOutEnum.fromInt(inventoryTransaction.getTransactionType().getInOrOut().intValue());
				switch (inOutEnum) {
				case IN:
					InventoryWarehouseItemDAO dao1 = InventoryWarehouseItemDAO.getInstance();
					InventoryWarehouseItem inventoryWarehouseItem1 = null;
					if (dao1 != null) {
						inventoryWarehouseItem1 = dao1.findByInventoryItemAndInventoryLocation(inventoryItem, locationIN);
						double recepieUnits1 = inventoryWarehouseItem1.getTotalRecepieUnits();
						inventoryWarehouseItem1.setTotalRecepieUnits(recepieUnits1
								+ (inventoryTransaction.getQuantity() * inventoryItem.getPackagingUnit().getFactor()));
						inventoryWarehouseItem1.setLastUpdateDate(new Date());
						inventoryWarehouseItem1.setUnitPurchasePrice(0.0d);
						dao1.saveOrUpdate(inventoryWarehouseItem1);
						actionPerformed = true;
					}
					break;
				case OUT:
					InventoryWarehouseItemDAO dao2 = InventoryWarehouseItemDAO.getInstance();
					InventoryWarehouseItem inventoryWarehouseItem2 = null;
					if (dao2 != null) {
						inventoryWarehouseItem2 = dao2.findByInventoryItemAndInventoryLocation(inventoryItem, locationOUT);
						double recepieUnits2 = inventoryWarehouseItem2.getTotalRecepieUnits();
						double unitsToBeRemoved = (inventoryTransaction.getQuantity() * inventoryItem.getPackagingUnit().getFactor());
						if (recepieUnits2 >= unitsToBeRemoved) {
							inventoryWarehouseItem2.setTotalRecepieUnits(recepieUnits2 - unitsToBeRemoved);
							inventoryWarehouseItem2.setLastUpdateDate(new Date());
							inventoryWarehouseItem2.setUnitPurchasePrice(0.0d);
							dao2.saveOrUpdate(inventoryWarehouseItem2);
							actionPerformed = true;
							int noOfItemsNow = (int) ((recepieUnits2 - unitsToBeRemoved) / inventoryItem.getPackagingUnit().getFactor());
							if (locationOUT.getName().toLowerCase().contains("cafe")) {
								if (noOfItemsNow <= replenishLevel) {
									POSMessageDialog.showError(BackOfficeWindow.getInstance(),
											"WARNING!! Just " + noOfItemsNow + " " + inventoryItem.getName()
													+ "left in Cafe. Please bring more from Godown!");
								}
							} else if (locationOUT.getName().toLowerCase().contains("godown")) {
								if (noOfItemsNow <= reorderLevel) {
									POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " "
											+ inventoryItem.getPackagingUnit().getName() + " " + inventoryItem.getName()
											+ " left in godown. Please order now!");
								}
							}
						} else {
							POSMessageDialog.showError(BackOfficeWindow.getInstance(), "No. of Items to be removed should be less than "
									+ formatDouble(recepieUnits2 / inventoryItem.getPackagingUnit().getFactor()));
						}
					}
					break;
				case MOVEMENT:
					if (locationIN.getId().intValue() != locationOUT.getId().intValue()) {
						InventoryWarehouseItemDAO dao3 = InventoryWarehouseItemDAO.getInstance();
						InventoryWarehouseItem inventoryWarehouseItemIN = null;
						InventoryWarehouseItem inventoryWarehouseItemOUT = null;
						if (dao3 != null) {
							inventoryWarehouseItemIN = dao3.findByInventoryItemAndInventoryLocation(inventoryItem, locationIN);
							inventoryWarehouseItemOUT = dao3.findByInventoryItemAndInventoryLocation(inventoryItem, locationOUT);
							double recepieUnitsIN = inventoryWarehouseItemIN.getTotalRecepieUnits();
							double recepieUnitsOUT = inventoryWarehouseItemOUT.getTotalRecepieUnits();
							double unitsToBeMoved = (inventoryTransaction.getQuantity() * inventoryItem.getPackagingUnit().getFactor());
							if (recepieUnitsOUT >= unitsToBeMoved) {
								inventoryWarehouseItemOUT.setTotalRecepieUnits(recepieUnitsOUT - unitsToBeMoved);
								inventoryWarehouseItemOUT.setLastUpdateDate(new Date());
								inventoryWarehouseItemOUT.setUnitPurchasePrice(0.0d);

								inventoryWarehouseItemIN.setTotalRecepieUnits(recepieUnitsIN + unitsToBeMoved);
								inventoryWarehouseItemIN.setLastUpdateDate(new Date());
								inventoryWarehouseItemIN.setUnitPurchasePrice(0.0d);

								dao3.saveOrUpdate(inventoryWarehouseItemOUT);
								dao3.saveOrUpdate(inventoryWarehouseItemIN);
								actionPerformed = true;

								int noOfItemsNow = (int) ((recepieUnitsOUT - unitsToBeMoved) / inventoryItem.getPackagingUnit().getFactor());
								if (locationOUT.getName().toLowerCase().contains("cafe")) {
									if (noOfItemsNow <= replenishLevel) {
										POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " "
												+ inventoryItem.getPackagingUnit().getName() + " " + inventoryItem.getName()
												+ " left in CAFE. Please bring more from Godown now!");
									}
								} else if (locationOUT.getName().toLowerCase().contains("godown")) {
									if (noOfItemsNow <= reorderLevel) {
										POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " "
												+ inventoryItem.getName() + "left in godown. Please order now!");
									}
								}
							} else {
								POSMessageDialog.showError(BackOfficeWindow.getInstance(), "No. of Items to be moved should be less than "
										+ formatDouble(recepieUnitsOUT / inventoryItem.getPackagingUnit().getFactor()));
							}
						}
					} else {
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "In-location and Out-location can't be same!");
					}
					break;
				case ADJUSTMENT:
				case WASTAGE:
					InventoryWarehouseItemDAO dao3 = InventoryWarehouseItemDAO.getInstance();
					InventoryWarehouseItem inventoryWarehouseItem3 = null;
					if (dao3 != null) {
						inventoryWarehouseItem3 = dao3.findByInventoryItemAndInventoryLocation(inventoryItem, locationOUT);
						double recepieUnits3 = inventoryWarehouseItem3.getTotalRecepieUnits();
						double unitsToBeAdjusted = inventoryTransaction.getQuantity();
						if (recepieUnits3 >= unitsToBeAdjusted) {
							inventoryWarehouseItem3.setTotalRecepieUnits(recepieUnits3 - unitsToBeAdjusted);
							inventoryWarehouseItem3.setLastUpdateDate(new Date());
							inventoryWarehouseItem3.setUnitPurchasePrice(0.0d);
							dao3.saveOrUpdate(inventoryWarehouseItem3);
							actionPerformed = true;
							int noOfItemsNow = (int) ((recepieUnits3 - unitsToBeAdjusted) / inventoryItem.getPackagingUnit().getFactor());
							if (locationOUT.getName().toLowerCase().contains("cafe")) {
								if (noOfItemsNow <= replenishLevel) {
									POSMessageDialog.showError(BackOfficeWindow.getInstance(),
											"WARNING!! Just " + noOfItemsNow + " " + inventoryItem.getName()
													+ "left in Cafe. Please bring more from Godown!");
								}
							} else if (locationOUT.getName().toLowerCase().contains("godown")) {
								if (noOfItemsNow <= reorderLevel) {
									POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " "
											+ inventoryItem.getPackagingUnit().getName() + " " + inventoryItem.getName()
											+ " left in godown. Please order now!");
								}
							}
						} else {
							POSMessageDialog.showError(BackOfficeWindow.getInstance(), "No. of Items to be removed should be less than "
									+ formatDouble(recepieUnits3 / inventoryItem.getPackagingUnit().getFactor()));
						}
					}
					break;
				}

				PurchaseOrder purchaseOrder = inventoryTransaction.getReferenceNo();
				PurchaseOrderDAO.getInstance().saveOrUpdate(purchaseOrder, session);
				if (actionPerformed) {
					tx.commit();
					if (inOutEnum == InOutEnum.IN || inOutEnum == InOutEnum.OUT) {
						updateAverageItemPrice(inventoryItem, (int) (inventoryTransaction.getQuantity() * inventoryItem.getPackagingUnit()
								.getFactor()), inventoryTransaction.getUnitPrice() * inventoryTransaction.getQuantity());
					}
				} else {
					tx.rollback();
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				if (tx != null) {
					tx.rollback();
				}
				session.close();
				POSMessageDialog.showError(e.getMessage(), e);
				return false;
			}
			return true;
		}
		return false;
	}

	protected void updateView() {
		InventoryTransaction transaction = (InventoryTransaction) getBean();
		if (transaction == null) {
			return;
		}
		if (transaction.getInventoryItem() != null) {
			this.tfItem.setText(transaction.getInventoryItem().getName());
		}
		if (transaction.getTransactionType() != null) {
			this.cbTransactionType.setSelectedItem(transaction.getTransactionType());
			InOutEnum inOutEnum = InOutEnum.fromInt(transaction.getTransactionType().getInOrOut().intValue());
			if (inOutEnum == InOutEnum.IN) {
				this.inWareHouse.setSelectedItem(transaction.getToWarehouse());
			} else if (inOutEnum == InOutEnum.OUT || inOutEnum == InOutEnum.ADJUSTMENT || inOutEnum == InOutEnum.WASTAGE) {
				this.outWareHouse.setSelectedItem(transaction.getFromWarehouse());
			} else if (inOutEnum == InOutEnum.MOVEMENT) {
				this.inWareHouse.setSelectedItem(transaction.getToWarehouse());
				this.outWareHouse.setSelectedItem(transaction.getFromWarehouse());
			}
		}
		if (transaction.getUnitPrice() != null && !transaction.getUnitPrice().isNaN()) {
			this.tfUnitPrice.setText(transaction.getUnitPrice().toString());
		}
		if (transaction.getVatPaid() != null && !transaction.getVatPaid().isNaN()) {
			this.tfVAT.setText(transaction.getVatPaid().toString());
		}
		if (transaction.getQuantity() != null && !transaction.getQuantity().isNaN()) {
			this.tfUnit.setText(transaction.getQuantity().toString());
		}
		if (transaction.getVendor() != null) {
			this.cbVendor.setSelectedItem(transaction.getVendor());
		}
		if (transaction.getRemark() != null) {
			this.taNote.setText(transaction.getRemark());
		}
		if (transaction.getTransactionDate() != null) {
			this.datePicker.setDate(transaction.getTransactionDate());
		}
		if (transaction.getCreditCheck() != null) {
			this.creditCheck.setSelected(transaction.getCreditCheck());
		}
	}

	protected boolean updateModel() throws IllegalModelStateException {
		InventoryTransaction transaction = (InventoryTransaction) getBean();

		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setOrderId(UUID.randomUUID().toString());
		InventoryTransactionType transType = (InventoryTransactionType) this.cbTransactionType.getSelectedItem();
		transaction.setTransactionType(transType);
		transaction.setReferenceNo(purchaseOrder);
		transaction.setInventoryItem(this.inventoryItem);
		transaction.setVendor((InventoryVendor) this.cbVendor.getSelectedItem());
		switch (transType.getInOutEnum()) {
		case IN:
			transaction.setVendor((InventoryVendor) this.cbVendor.getSelectedItem());
			transaction.setToWarehouse((InventoryWarehouse) this.inWareHouse.getSelectedItem());
			transaction.setUnitPrice(Double.valueOf(this.tfUnitPrice.getDouble()));
			transaction.setVatPaid(Double.valueOf(this.tfVAT.getDouble()));
			transaction.setCreditCheck(creditCheck.isSelected());
			break;
		case OUT:
			transaction.setVendor((InventoryVendor) this.cbVendor.getSelectedItem());
			transaction.setFromWarehouse((InventoryWarehouse) this.outWareHouse.getSelectedItem());
			transaction.setUnitPrice(Double.valueOf(this.tfUnitPrice.getDouble()));
			transaction.setVatPaid(Double.valueOf(this.tfVAT.getDouble()));
			transaction.setCreditCheck(creditCheck.isSelected());
			break;
		case MOVEMENT:
			transaction.setToWarehouse((InventoryWarehouse) this.inWareHouse.getSelectedItem());
			transaction.setFromWarehouse((InventoryWarehouse) this.outWareHouse.getSelectedItem());
			break;
		case ADJUSTMENT:
		case WASTAGE:
			transaction.setFromWarehouse((InventoryWarehouse) this.outWareHouse.getSelectedItem());
			transaction.setUnitPrice(0.0d);
			break;
		}
		transaction.setQuantity(Double.valueOf(this.tfUnit.getDouble()));
		transaction.setTransactionDate(this.datePicker.getDate());
		return true;
	}

	public String getDisplayText() {
		return "New transaction";
	}

	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0.paramString());
		InventoryTransactionType type = (InventoryTransactionType) (((DefaultComboBoxModel<?>) ((JXComboBox) arg0.getSource()).getModel())
				.getSelectedItem());
		switch (type.getInOutEnum()) {
		case IN:
			this.outWareHouse.setVisible(false);
			this.inWareHouse.setVisible(true);
			this.cbVendor.setVisible(true);
			this.outWareHouseLabel.setVisible(false);
			this.inWareHouseLabel.setVisible(true);
			this.vendorLabel.setVisible(true);
			this.priceLabel.setVisible(true);
			this.tfUnitPrice.setVisible(true);
			this.tfVAT.setVisible(true);
			this.creditCheck.setVisible(true);
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
			this.tfVAT.setVisible(true);
			this.creditCheck.setVisible(true);
			break;
		case MOVEMENT:
			this.outWareHouse.setVisible(true);
			this.inWareHouse.setVisible(true);
			this.cbVendor.setVisible(false);
			this.outWareHouseLabel.setVisible(true);
			this.inWareHouseLabel.setVisible(true);
			this.vatLabel.setVisible(false);
			this.tfVAT.setVisible(false);
			this.vendorLabel.setVisible(false);
			this.priceLabel.setVisible(false);
			this.tfUnitPrice.setVisible(false);
			this.creditCheck.setVisible(false);
			break;
		case ADJUSTMENT:
		case WASTAGE:
			this.outWareHouse.setVisible(true);
			this.inWareHouse.setVisible(false);
			this.cbVendor.setVisible(false);
			this.outWareHouseLabel.setVisible(true);
			this.inWareHouseLabel.setVisible(false);
			this.vatLabel.setVisible(false);
			this.tfVAT.setVisible(false);
			this.vendorLabel.setVisible(false);
			this.priceLabel.setVisible(false);
			this.tfUnitPrice.setVisible(false);
			this.creditCheck.setVisible(false);
			this.itemCountLabel.setText("Units (" + inventoryItem.getPackagingUnit().getShortName() + ")");
			break;
		}

	}

	public void setFieldsEnableEdit() {
		this.tfItem.setEnabled(false);
		this.tfUnitPrice.setEnabled(false);
		this.tfVAT.setEnabled(false);
		this.cbTransactionType.setEnabled(false);
		this.cbVendor.setEnabled(false);
		this.inWareHouse.setEnabled(false);
		this.outWareHouse.setEnabled(false);
		this.vendorLabel.setEnabled(false);
		this.inWareHouseLabel.setEnabled(false);
		this.outWareHouseLabel.setEnabled(false);
		this.priceLabel.setEnabled(false);
		this.itemCountLabel.setEnabled(false);
		this.datePicker.setEnabled(false);
		this.taNote.setEnabled(true);
		this.tfUnit.setEnabled(false);
		this.dateLabel.setEnabled(false);
		this.vatLabel.setEnabled(false);
		this.itemLabel.setEnabled(false);
		this.transLabel.setEnabled(false);
	}
}
