package com.orostock.inventory.ui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXComboBox;

import com.date.picker.DateTimePicker;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.Company;
import com.floreantpos.model.InOutEnum;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.InventoryTransactionType;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.InventoryWarehouseItem;
import com.floreantpos.model.ItemCompVendPack;
import com.floreantpos.model.PackSize;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.InventoryLocationDAO;
import com.floreantpos.model.dao.InventoryTransactionDAO;
import com.floreantpos.model.dao.InventoryTransactionTypeDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.model.dao.InventoryWarehouseItemDAO;
import com.floreantpos.model.dao.ItemCompVendPackDAO;
import com.floreantpos.model.dao.TaxDAO;
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
	private DoubleTextField tfTotalPrice;
	private JXComboBox cbTransactionType;
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
	private JLabel noteLabel;
	private JXComboBox cbVat;
	private JLabel discountLabel;
	private DoubleTextField tfDiscount;
	List<ItemCompVendPack> icvpList;
	Map<Company, HashSet<InventoryVendor>> mapCompVend;
	Map<Pair<InventoryVendor, Company>, HashSet<PackSize>> mapCVPack;
	private JXComboBox cbCompany;
	private JXComboBox cbVendor;
	private JXComboBox cbPackSize;
	private JLabel packLabel;
	private JLabel companyLabel;
	private boolean newTransaction;

	public boolean isNewTransaction() {
		return newTransaction;
	}

	public void setNewTransaction(boolean newTransaction) {
		this.newTransaction = newTransaction;
	}

	public InventoryTransactionEntryForm() {
		newTransaction = true;
		createUI();

		List<InventoryWarehouse> warehouses = InventoryWarehouseDAO.getInstance().findAll();
		List<Tax> taxes = TaxDAO.getInstance().findAll();

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
		this.cbCompany.setSelectedIndex(-1);

		this.cbCompany.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (InventoryTransactionEntryForm.this.newTransaction) {
					JXComboBox combo = (JXComboBox) e.getSource();
					Company c = (Company) combo.getSelectedItem();
					if (c != null) {
						cbVendor.removeAllItems();
						cbPackSize.removeAllItems();
						cbVendor.setModel(new DefaultComboBoxModel(mapCompVend.get(c).toArray(new InventoryVendor[0])));
						cbVendor.setSelectedIndex(-1);
						cbVendor.setEnabled(true);
						cbPackSize.setEnabled(false);
					}
				}
			}

		});

		this.cbVendor.setModel(new DefaultComboBoxModel());
		this.cbVendor.setSelectedIndex(-1);
		this.cbVendor.setEnabled(false);
		this.cbVendor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (InventoryTransactionEntryForm.this.newTransaction) {
					JXComboBox combo = (JXComboBox) e.getSource();
					if (combo.getSelectedItem() != null) {
						cbPackSize.removeAllItems();
						Pair<InventoryVendor, Company> pair = Pair.of((InventoryVendor) cbVendor.getSelectedItem(), (Company) cbCompany.getSelectedItem());
						cbPackSize.setModel(new DefaultComboBoxModel(mapCVPack.get(pair).toArray(new PackSize[0])));
						cbPackSize.setSelectedIndex(-1);
						cbPackSize.setEnabled(true);
					}
				}
			}

		});
		this.cbPackSize.setModel(new DefaultComboBoxModel());
		cbPackSize.setSelectedIndex(-1);
		cbPackSize.setEnabled(false);

		this.inWareHouse.setModel(new DefaultComboBoxModel(warehouses.toArray(new InventoryWarehouse[0])));
		this.outWareHouse.setModel(new DefaultComboBoxModel(warehouses.toArray(new InventoryWarehouse[0])));

		this.cbVat.setModel(new DefaultComboBoxModel(taxes.toArray(new Tax[0])));
		cbVat.setSelectedIndex(-1);
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
		double averagePrice = this.inventoryItem.getAverageRunitPrice();
		if (newRecepieUnits > 0) {
			averagePrice = ((totalRecepieUnits * item.getAverageRunitPrice()) + totalPaid) / (totalRecepieUnits + newRecepieUnits);
		}
		this.inventoryItem.setAverageRunitPrice(averagePrice);
		InventoryItemDAO.getInstance().saveOrUpdate(this.inventoryItem);
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

		add(dateLabel = new JLabel("Date"));
		this.datePicker = new DateTimePicker();
		datePicker.setFormats("dd-MM-yyyy HH:mm");
		datePicker.setDate(new Date());
		add(this.datePicker, "wrap, w 200px");

		add(this.companyLabel = new JLabel("Company"));
		this.cbCompany = new JXComboBox();
		add(this.cbCompany, "wrap, w 200px");

		add(this.vendorLabel = new JLabel("Distributor"));
		this.cbVendor = new JXComboBox();
		add(this.cbVendor, "wrap, w 200px");

		add(this.packLabel = new JLabel("Pack Size"));
		this.cbPackSize = new JXComboBox();
		add(this.cbPackSize, "wrap, w 200px");

		add(this.itemCountLabel = new JLabel("No of packs"));
		this.tfUnit = new DoubleTextField(20);
		add(this.tfUnit, "grow, wrap");

		add(this.priceLabel = new JLabel("Total Price (Vat excluded)"));
		this.tfTotalPrice = new DoubleTextField(20);
		add(this.tfTotalPrice, "grow, wrap");

		add(this.vatLabel = new JLabel("VAT in %"));
		this.cbVat = new JXComboBox();
		add(this.cbVat, "wrap, w 150px");

		add(this.discountLabel = new JLabel("Discount"));
		this.tfDiscount = new DoubleTextField(20);
		add(this.tfDiscount, "grow, wrap");

		add(this.creditCheck = new JCheckBox("Credit", false));
		add(this.creditCheck, "grow, wrap");

		add(this.outWareHouseLabel = new JLabel("Out-Warehouse"));
		this.outWareHouse = new JXComboBox();
		add(this.outWareHouse, "wrap, w 200px");

		add(this.inWareHouseLabel = new JLabel("In-Warehouse"));
		this.inWareHouse = new JXComboBox();
		add(this.inWareHouse, "wrap, w 200px");

		add(this.noteLabel = new JLabel("Note"));
		this.taNote = new JTextArea();
		add(new JScrollPane(this.taNote), "grow, h 100px, wrap");
		this.outWareHouse.setVisible(false);
		this.outWareHouseLabel.setVisible(false);
	}

	public void setInventoryItem(InventoryItem item) {
		this.inventoryItem = item;
		this.tfItem.setText(item.getName());
		icvpList = ItemCompVendPackDAO.getInstance().findAllByInventoryItem(this.inventoryItem);
		mapCompVend = new HashMap<Company, HashSet<InventoryVendor>>();
		mapCVPack = new HashMap<Pair<InventoryVendor, Company>, HashSet<PackSize>>();
		this.packLabel.setText(this.inventoryItem.getPackagingUnit().getName() + " per pack");
		if (icvpList != null) {
			for (ItemCompVendPack icvp : icvpList) {
				Company c = (Company) icvp.getCompany();
				if (!mapCompVend.containsKey(c)) {
					HashSet<InventoryVendor> vendSet = new HashSet<InventoryVendor>();
					mapCompVend.put(c, vendSet);
				}
				mapCompVend.get(c).add(icvp.getInventoryVendor());

				Pair<InventoryVendor, Company> pair = Pair.of(icvp.getInventoryVendor(), icvp.getCompany());
				if (!mapCVPack.containsKey(pair)) {
					HashSet<PackSize> packSet = new HashSet<PackSize>();
					mapCVPack.put(pair, packSet);
				}
				mapCVPack.get(pair).add(icvp.getPackSize());
			}
			this.cbCompany.setModel(new DefaultComboBoxModel(mapCompVend.keySet().toArray(new Company[0])));
			if (isNewTransaction()) {
				this.cbCompany.setSelectedIndex(-1);
			}
		}

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
				if (inventoryTransaction.getInventoryTransactionType().getInOutEnum() == InOutEnum.IN || inventoryTransaction.getInventoryTransactionType().getInOutEnum() == InOutEnum.OUT) {
					if (inventoryTransaction.getVatPaid() == null || inventoryTransaction.getVatPaid().getRate() < 0) {
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid VAT!!");
						actionPerformed = false;
						return false;
					} else if (inventoryTransaction.getTotalPrice() < 0) {
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Price!!");
						actionPerformed = false;
						return false;
					} else if (inventoryTransaction.getTotalPrice() == 0) {
						int i = POSMessageDialog.ShowOkcancel(BackOfficeWindow.getInstance(), "Are you sure item cost is zero??");
						if (i == 2 || i == -1) {
							actionPerformed = false;
							return false;
						}
					}
				}
				if (inventoryTransaction.getQuantity() <= 0) {
					POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Quantity!!");
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
				InOutEnum inOutEnum = InOutEnum.fromInt(inventoryTransaction.getInventoryTransactionType().getInOrOut().intValue());
				switch (inOutEnum) {
				case IN:
					InventoryWarehouseItemDAO dao1 = InventoryWarehouseItemDAO.getInstance();
					InventoryWarehouseItem inventoryWarehouseItem1 = null;
					if (dao1 != null) {
						inventoryWarehouseItem1 = dao1.findByInventoryItemAndInventoryLocation(inventoryItem, locationIN);
						double recepieUnits1 = inventoryWarehouseItem1.getTotalRecepieUnits();
						inventoryWarehouseItem1.setTotalRecepieUnits(recepieUnits1 + (inventoryTransaction.getQuantity() * inventoryItem.getPackagingUnit().getFactor()));
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
									POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " " + inventoryItem.getName()
											+ "left in Cafe. Please bring more from Godown!");
								}
							} else if (locationOUT.getName().toLowerCase().contains("godown")) {
								if (noOfItemsNow <= reorderLevel) {
									POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " " + inventoryItem.getPackagingUnit().getName() + " "
											+ inventoryItem.getName() + " left in godown. Please order now!");
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
										POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " " + inventoryItem.getPackagingUnit().getName() + " "
												+ inventoryItem.getName() + " left in CAFE. Please bring more from Godown now!");
									}
								} else if (locationOUT.getName().toLowerCase().contains("godown")) {
									if (noOfItemsNow <= reorderLevel) {
										POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " " + inventoryItem.getName()
												+ "left in godown. Please order now!");
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
									POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " " + inventoryItem.getName()
											+ "left in Cafe. Please bring more from Godown!");
								}
							} else if (locationOUT.getName().toLowerCase().contains("godown")) {
								if (noOfItemsNow <= reorderLevel) {
									POSMessageDialog.showError(BackOfficeWindow.getInstance(), "WARNING!! Just " + noOfItemsNow + " " + inventoryItem.getPackagingUnit().getName() + " "
											+ inventoryItem.getName() + " left in godown. Please order now!");
								}
							}
						} else {
							POSMessageDialog.showError(BackOfficeWindow.getInstance(), "No. of Items to be removed should be less than "
									+ formatDouble(recepieUnits3 / inventoryItem.getPackagingUnit().getFactor()));
						}
					}
					break;
				}

				if (actionPerformed) {
					tx.commit();
					if (inOutEnum == InOutEnum.IN || inOutEnum == InOutEnum.OUT) {
						updateAverageItemPrice(inventoryItem, (int) (inventoryTransaction.getQuantity() * inventoryItem.getPackagingUnit().getFactor()), inventoryTransaction.getTotalPrice()
								* inventoryTransaction.getQuantity());
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
				POSMessageDialog.showError(e.getMessage(), e);
				return false;
			} finally {
				session.close();
			}
			return true;
		}
		return false;
	}

	public void clearFields() {
		this.tfItem.setText("");
		this.tfTotalPrice.setText("");
		this.tfDiscount.setText("");
		this.tfUnit.setText("");
		this.taNote.setText("");
		this.datePicker.setDate(null);
		this.cbVat.setSelectedIndex(-1);
		this.inWareHouse.setSelectedIndex(-1);
		this.outWareHouse.setSelectedIndex(-1);
		this.cbVendor.setSelectedIndex(-1);
		this.cbCompany.setSelectedIndex(-1);
		this.cbPackSize.setSelectedIndex(-1);
		this.cbTransactionType.setSelectedIndex(1);
	}

	protected void updateView() {
		InventoryTransaction transaction = (InventoryTransaction) getBean();
		if (transaction == null) {
			return;
		}
		if (transaction.getInventoryItem() != null) {
			this.tfItem.setText(transaction.getInventoryItem().getName());
		}
		if (transaction.getInventoryTransactionType() != null) {
			this.cbTransactionType.setSelectedItem(transaction.getInventoryTransactionType());
			InOutEnum inOutEnum = InOutEnum.fromInt(transaction.getInventoryTransactionType().getInOrOut().intValue());
			if (inOutEnum == InOutEnum.IN) {
				this.inWareHouse.setSelectedItem(transaction.getInventoryWarehouseByToWarehouseId());
			} else if (inOutEnum == InOutEnum.OUT || inOutEnum == InOutEnum.ADJUSTMENT || inOutEnum == InOutEnum.WASTAGE) {
				this.outWareHouse.setSelectedItem(transaction.getInventoryWarehouseByFromWarehouseId());
			} else if (inOutEnum == InOutEnum.MOVEMENT) {
				this.inWareHouse.setSelectedItem(transaction.getInventoryWarehouseByToWarehouseId());
				this.outWareHouse.setSelectedItem(transaction.getInventoryWarehouseByFromWarehouseId());
			}
		}
		this.tfTotalPrice.setText(Double.toString(transaction.getTotalPrice()));
		this.tfDiscount.setText(Double.toString(transaction.getDiscount()));
		if (transaction.getVatPaid() != null) {
			this.cbVat.setSelectedItem(transaction.getVatPaid());
		}
		this.tfUnit.setText(Double.toString(transaction.getQuantity()));
		if (transaction.getCompany() != null) {
			this.cbCompany.setSelectedItem(transaction.getCompany());
		}
		if (transaction.getInventoryVendor() != null) {
			if (!newTransaction && mapCompVend.containsKey(transaction.getCompany())) {
				this.cbVendor.setModel(new DefaultComboBoxModel(mapCompVend.get(transaction.getCompany()).toArray(new InventoryVendor[0])));
			}
			this.cbVendor.setSelectedItem(transaction.getInventoryVendor());
		}
		if (transaction.getRemark() != null) {
			this.taNote.setText(transaction.getRemark());
		}
		if (transaction.getTransactionDate() != null) {
			this.datePicker.setDate(transaction.getTransactionDate());
		}
		if (transaction.isCreditCheck()) {
			this.creditCheck.setSelected(transaction.isCreditCheck());
		}
		if (transaction.getPackSize() != null) {
			Pair<InventoryVendor, Company> pair = Pair.of(transaction.getInventoryVendor(), transaction.getCompany());

			if (!newTransaction && mapCVPack.containsKey(pair)) {
				cbPackSize.setModel(new DefaultComboBoxModel(mapCVPack.get(pair).toArray(new PackSize[0])));
			}
			this.cbPackSize.setSelectedItem(transaction.getPackSize());
		}
	}

	protected boolean updateModel() throws IllegalModelStateException {
		InventoryTransaction transaction = (InventoryTransaction) getBean();
		if (transaction == null) {
			transaction = new InventoryTransaction();
		}
		InventoryTransactionType transType = (InventoryTransactionType) this.cbTransactionType.getSelectedItem();
		transaction.setInventoryTransactionType(transType);
		transaction.setInventoryItem(this.inventoryItem);
		if (this.cbCompany.getSelectedItem() != null) {
			transaction.setCompany(((Company) this.cbCompany.getSelectedItem()));
		} else {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Company!!");
		}
		switch (transType.getInOutEnum()) {
		case IN:
			transaction.setInventoryWarehouseByToWarehouseId((InventoryWarehouse) this.inWareHouse.getSelectedItem());
			transaction.setTotalPrice(Double.valueOf(this.tfTotalPrice.getDouble()));
			transaction.setDiscount(Double.valueOf(this.tfDiscount.getDouble()));
			if (this.cbVat.getSelectedItem() != null) {
				transaction.setVatPaid(((Tax) this.cbVat.getSelectedItem()));
			}
			transaction.setCreditCheck(creditCheck.isSelected());
			if (this.cbVendor.getSelectedItem() != null) {
				transaction.setInventoryVendor((InventoryVendor) this.cbVendor.getSelectedItem());
			} else {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Distributor!!");
			}
			if (this.cbPackSize.getSelectedItem() != null) {
				transaction.setPackSize((PackSize) this.cbPackSize.getSelectedItem());
			} else {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Pack size!!");
			}
			break;
		case OUT:
			transaction.setInventoryWarehouseByFromWarehouseId((InventoryWarehouse) this.outWareHouse.getSelectedItem());
			transaction.setTotalPrice(Double.valueOf(this.tfTotalPrice.getDouble()));
			transaction.setDiscount(Double.valueOf(this.tfDiscount.getDouble()));
			if (this.cbVat.getSelectedItem() != null) {
				transaction.setVatPaid(((Tax) this.cbVat.getSelectedItem()));
			}
			transaction.setCreditCheck(creditCheck.isSelected());
			if (this.cbVendor.getSelectedItem() != null) {
				transaction.setInventoryVendor((InventoryVendor) this.cbVendor.getSelectedItem());
			} else {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Distributor!!");
			}
			if (this.cbPackSize.getSelectedItem() != null) {
				transaction.setPackSize((PackSize) this.cbPackSize.getSelectedItem());
			} else {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Pack size!!");
			}
			break;
		case MOVEMENT:
			transaction.setInventoryWarehouseByToWarehouseId((InventoryWarehouse) this.inWareHouse.getSelectedItem());
			transaction.setInventoryWarehouseByFromWarehouseId((InventoryWarehouse) this.outWareHouse.getSelectedItem());
			break;
		case ADJUSTMENT:
		case WASTAGE:
			transaction.setInventoryWarehouseByFromWarehouseId((InventoryWarehouse) this.outWareHouse.getSelectedItem());
			transaction.setTotalPrice(0.0d);
			transaction.setInventoryVendor(null);
			transaction.setPackSize(null);
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
		InventoryTransactionType type = (InventoryTransactionType) (((DefaultComboBoxModel<?>) ((JXComboBox) arg0.getSource()).getModel()).getSelectedItem());
		this.tfItem.setVisible(true);
		this.itemLabel.setVisible(true);
		this.cbTransactionType.setVisible(true);
		this.transLabel.setVisible(true);
		this.datePicker.setVisible(true);
		this.dateLabel.setVisible(true);
		this.cbCompany.setVisible(true);
		this.companyLabel.setVisible(true);
		this.noteLabel.setVisible(true);
		this.taNote.setVisible(true);
		this.tfUnit.setVisible(true);
		this.itemCountLabel.setVisible(true);
		switch (type.getInOutEnum()) {
		case IN:
			this.cbVendor.setVisible(true);
			this.vendorLabel.setVisible(true);
			this.cbPackSize.setVisible(true);
			this.packLabel.setVisible(true);
			this.priceLabel.setVisible(true);
			this.tfTotalPrice.setVisible(true);
			this.vatLabel.setVisible(true);
			this.cbVat.setVisible(true);
			this.discountLabel.setVisible(true);
			this.tfDiscount.setVisible(true);
			this.creditCheck.setVisible(true);
			this.inWareHouseLabel.setVisible(true);
			this.inWareHouse.setVisible(true);
			this.outWareHouse.setVisible(false);
			this.outWareHouseLabel.setVisible(false);
			this.itemCountLabel.setText("No of packs");
			break;
		case OUT:
			this.cbVendor.setVisible(true);
			this.vendorLabel.setVisible(true);
			this.cbPackSize.setVisible(true);
			this.packLabel.setVisible(true);
			this.priceLabel.setVisible(true);
			this.tfTotalPrice.setVisible(true);
			this.vatLabel.setVisible(true);
			this.cbVat.setVisible(true);
			this.discountLabel.setVisible(true);
			this.tfDiscount.setVisible(true);
			this.creditCheck.setVisible(true);
			this.inWareHouseLabel.setVisible(false);
			this.inWareHouse.setVisible(false);
			this.outWareHouse.setVisible(true);
			this.outWareHouseLabel.setVisible(true);
			this.itemCountLabel.setText("No of packs");
			break;
		case MOVEMENT:
			this.cbVendor.setVisible(true);
			this.vendorLabel.setVisible(true);
			this.cbPackSize.setVisible(true);
			this.packLabel.setVisible(true);
			this.priceLabel.setVisible(false);
			this.tfTotalPrice.setVisible(false);
			this.vatLabel.setVisible(false);
			this.cbVat.setVisible(false);
			this.discountLabel.setVisible(false);
			this.tfDiscount.setVisible(false);
			this.creditCheck.setVisible(false);
			this.inWareHouseLabel.setVisible(true);
			this.inWareHouse.setVisible(true);
			this.outWareHouse.setVisible(true);
			this.outWareHouseLabel.setVisible(true);
			this.itemCountLabel.setText("No of packs");
			break;
		case ADJUSTMENT:
		case WASTAGE:
			this.cbVendor.setVisible(false);
			this.vendorLabel.setVisible(false);
			this.cbPackSize.setVisible(false);
			this.packLabel.setVisible(false);
			this.priceLabel.setVisible(false);
			this.tfTotalPrice.setVisible(false);
			this.vatLabel.setVisible(false);
			this.cbVat.setVisible(false);
			this.discountLabel.setVisible(false);
			this.tfDiscount.setVisible(false);
			this.creditCheck.setVisible(false);
			this.inWareHouseLabel.setVisible(false);
			this.inWareHouse.setVisible(false);
			this.outWareHouse.setVisible(true);
			this.outWareHouseLabel.setVisible(true);
			this.itemCountLabel.setText("Units (" + inventoryItem.getPackagingUnit().getRecepieUnitName() + ")");
			break;
		}

	}

	public void setFieldsEnable(boolean enable) {
		this.tfItem.setEnabled(enable);
		this.tfTotalPrice.setEnabled(enable);
		this.tfDiscount.setEnabled(enable);
		this.cbVat.setEnabled(enable);
		this.cbTransactionType.setEnabled(enable);
		this.cbVendor.setEnabled(enable);
		this.inWareHouse.setEnabled(enable);
		this.outWareHouse.setEnabled(enable);
		this.vendorLabel.setEnabled(enable);
		this.inWareHouseLabel.setEnabled(enable);
		this.outWareHouseLabel.setEnabled(enable);
		this.priceLabel.setEnabled(enable);
		this.itemCountLabel.setEnabled(enable);
		this.datePicker.setEnabled(enable);
		this.noteLabel.setEnabled(enable);
		this.taNote.setEnabled(enable);
		this.tfUnit.setEnabled(enable);
		this.dateLabel.setEnabled(enable);
		this.vatLabel.setEnabled(enable);
		this.itemLabel.setEnabled(enable);
		this.transLabel.setEnabled(enable);
		this.creditCheck.setEnabled(enable);
		this.cbCompany.setEnabled(enable);
		this.packLabel.setEnabled(enable);
		this.discountLabel.setEnabled(enable);
		this.companyLabel.setEnabled(enable);
	}

	public void setFieldsEnableAfterEdit() {
		setFieldsEnable(false);
		this.creditCheck.setEnabled(true);
		this.noteLabel.setEnabled(true);
		this.taNote.setEnabled(true);
	}
}
