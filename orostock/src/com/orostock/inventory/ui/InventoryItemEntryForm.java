package com.orostock.inventory.ui;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXComboBox;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InventoryGroup;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.InventoryWarehouseItem;
import com.floreantpos.model.PackagingUnit;
import com.floreantpos.model.dao.InventoryGroupDAO;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.InventoryLocationDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao.InventoryWarehouseItemDAO;
import com.floreantpos.model.dao.PackagingUnitDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class InventoryItemEntryForm extends BeanEditor<InventoryItem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6443314703410893172L;

	JPanel mainPanel = new JPanel();
	JTextField tfName = new FixedLengthTextField(60);
	IntegerTextField tfPackSizeReorderLevel = new IntegerTextField(30);
	IntegerTextField tfPackSizeReplenishLevel = new IntegerTextField(30);
	JTextArea tfDescription = new JTextArea(4, 10);
	private ListComboBoxModel unitModel = new ListComboBoxModel();
	private final JXComboBox cbPackagingUnit = new JXComboBox(this.unitModel);
	private final JXComboBox cbGroup = new JXComboBox();
	private final JXComboBox cbVendor = new JXComboBox();

	public InventoryItemEntryForm() {
		createUI();
		populateComboBoxes();
		setFieldsEnable(false);
	}

	private void populateComboBoxes() {
		List<PackagingUnit> packagingUnits = PackagingUnitDAO.getInstance().findAll();
		this.unitModel.setDataList(packagingUnits);

		List<InventoryGroup> groups = InventoryGroupDAO.getInstance().findAll();
		this.cbGroup.setModel(new DefaultComboBoxModel(groups.toArray(new InventoryGroup[0])));

		List<InventoryVendor> vendors = InventoryVendorDAO.getInstance().findAll();
		this.cbVendor.setModel(new DefaultComboBoxModel(vendors.toArray(new InventoryVendor[0])));
	}

	public void createNew() {
		setBean(new InventoryItem());
	}

	public void clearFields() {
		this.tfName.setText("");
		this.tfPackSizeReorderLevel.setText("");
		this.tfPackSizeReplenishLevel.setText("");
		this.tfDescription.setText("");
		this.cbGroup.setSelectedIndex(-1);
		this.cbVendor.setSelectedIndex(-1);
	}

	public void setFieldsEnable(boolean enable) {
		this.tfName.setEnabled(enable);

		this.cbPackagingUnit.setEnabled(enable);
		this.tfPackSizeReorderLevel.setEnabled(enable);
		this.tfPackSizeReplenishLevel.setEnabled(enable);
		this.tfDescription.setEnabled(enable);
		this.cbGroup.setEnabled(enable);
		this.cbVendor.setEnabled(enable);
	}

	private void createUI() {
		setLayout(new BorderLayout());
		add(this.mainPanel);
		this.mainPanel.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));
		this.mainPanel.add(new JLabel("Name"), "cell 0 0,alignx trailing");
		this.mainPanel.add(this.tfName, "cell 1 0 3 1,growx");
		this.mainPanel.add(new JLabel("Description"), "cell 0 2,alignx trailing");
		this.tfDescription.setTabSize(4);
		JScrollPane scrollPane = new JScrollPane(this.tfDescription);
		this.mainPanel.add(scrollPane, "cell 1 2 3 1,growx");
		this.mainPanel.add(new JLabel("Packaging unit"), "cell 0 3,alignx trailing");
		this.mainPanel.add(this.cbPackagingUnit, "cell 1 3,growx");
		this.mainPanel.add(new JLabel("Reorder level"), "cell 0 6,alignx trailing");
		this.mainPanel.add(this.tfPackSizeReorderLevel, "cell 1 6,growx");
		this.mainPanel.add(new JLabel("Replenish level"), "cell 0 7,alignx trailing");
		this.mainPanel.add(this.tfPackSizeReplenishLevel, "cell 1 7,growx");
		this.mainPanel.add(new JLabel(""), "cell 2 12");
		this.mainPanel.add(new JLabel("Group"), "cell 0 13,alignx trailing");
		this.mainPanel.add(this.cbGroup, "cell 1 13 2 1,growx");
		this.mainPanel.add(new JLabel("Vendor"), "cell 0 15,alignx trailing");
		this.mainPanel.add(this.cbVendor, "cell 1 15 2 1,growx");
	}

	protected void createNewLocation() {
		InventoryLocationEntryForm form = new InventoryLocationEntryForm(new InventoryLocation());
		BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
		dialog.pack();
		dialog.open();
		if (dialog.isCanceled()) {
			return;
		}
	}

	public void updateView() {
		InventoryItem inventoryItem = (InventoryItem) getBean();
		if (inventoryItem == null) {
			return;
		}
		this.tfName.setText(inventoryItem.getName());
		this.cbPackagingUnit.setSelectedItem(inventoryItem.getPackagingUnit());
		this.tfPackSizeReorderLevel.setText(String.valueOf(inventoryItem.getPackageReorderLevel()));
		this.tfPackSizeReplenishLevel.setText(String.valueOf(inventoryItem.getPackageReplenishLevel()));
		this.tfDescription.setText(inventoryItem.getDescription());
		this.cbGroup.setSelectedItem(inventoryItem.getItemGroup());
		this.cbVendor.setSelectedItem(inventoryItem.getItemVendor());
	}

	String formatDouble(double d) {
		NumberFormat f = new DecimalFormat("0.##");
		return f.format(d);
	}

	public boolean updateModel() {
		InventoryItem inventoryItem = (InventoryItem) getBean();

		if (inventoryItem == null) {
			inventoryItem = new InventoryItem();
			setBean(inventoryItem);
		}

		if (StringUtils.isEmpty(this.tfName.getText())) {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Item Name is empty");
			return false;
		}

		inventoryItem.setName(this.tfName.getText());
		inventoryItem.setPackagingUnit((PackagingUnit) this.cbPackagingUnit.getSelectedItem());
		inventoryItem.setPackageReorderLevel(Integer.valueOf(this.tfPackSizeReorderLevel.getInteger()));
		inventoryItem.setPackageReplenishLevel(Integer.valueOf(this.tfPackSizeReplenishLevel.getInteger()));
		inventoryItem.setDescription(this.tfDescription.getText());
		inventoryItem.setCreateTime(new Date());

		// set default values
		inventoryItem.setUnitPerPackage(0d);
		inventoryItem.setSortOrder(0);
		inventoryItem.setTotalPackages(0);
		inventoryItem.setTotalRecepieUnits(0d);
		inventoryItem.setUnitPurchasePrice(0d);
		inventoryItem.setUnitSellingPrice(0d);
		inventoryItem.setItemGroup((InventoryGroup) this.cbGroup.getSelectedItem());
		inventoryItem.setItemLocation(null);
		inventoryItem.setItemVendor(null);
		return true;
	}

	public boolean save() {
		Session session = InventoryItemDAO.getInstance().createNewSession();
		if (session != null) {
			Transaction tx = session.beginTransaction();
			boolean actionPerformed = false;
			try {
				if (updateModel()) {
					InventoryItem item = (InventoryItem) getBean();
					if (item.getPackagingUnit() == null) {
						actionPerformed = false;
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Packaging unit!!");
					} else {
						InventoryItemDAO dao = InventoryItemDAO.getInstance();
						dao.saveOrUpdate(item);
						InventoryWarehouseItemDAO wareItemDao = InventoryWarehouseItemDAO.getInstance();
						InventoryLocationDAO locationDao = InventoryLocationDAO.getInstance();
						List<InventoryLocation> listLocation = locationDao.findAll();
						if (listLocation != null) {
							for (InventoryLocation warehouse : listLocation) {
								InventoryWarehouseItem item1;
								item1 = wareItemDao.findByInventoryItemAndInventoryLocation((InventoryItem) getBean(), warehouse);
								if (item1 == null) {
									item1 = new InventoryWarehouseItem();
									item1.setItemLocation(warehouse);
									item1.setInventoryItem((InventoryItem) getBean());
									item1.setAveragePackagePrice(0d);
									item1.setCreateTime(new Date());
									item1.setTotalRecepieUnits(0d);
									item1.setUnitPurchasePrice(0d);
									wareItemDao.save(item1);
								}
							}
							actionPerformed = true;
						}
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
				session.close();
				POSMessageDialog.showError(e.getMessage(), e);
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean delete() {
		InventoryItem inventoryItem = (InventoryItem) getBean();
		if (inventoryItem == null) {
			return false;
		}
		InventoryItemDAO.getInstance().delete(inventoryItem);
		return true;
	}

	public String getDisplayText() {
		InventoryItem inventoryItem = (InventoryItem) getBean();
		if ((inventoryItem == null) || (inventoryItem.getId() == null)) {
			return "Add new inventory item";
		}
		return "Edit inventory item";
	}
}