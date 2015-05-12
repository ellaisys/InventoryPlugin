package com.orostock.inventory.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXComboBox;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.Company;
import com.floreantpos.model.InventoryGroup;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.InventoryWarehouseItem;
import com.floreantpos.model.ItemCompVendPack;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.PackSize;
import com.floreantpos.model.PackagingUnit;
import com.floreantpos.model.RecepieItem;
import com.floreantpos.model.dao.CompanyDAO;
import com.floreantpos.model.dao.InventoryGroupDAO;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.InventoryLocationDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao.InventoryWarehouseItemDAO;
import com.floreantpos.model.dao.ItemCompVendPackDAO;
import com.floreantpos.model.dao.PackSizeDAO;
import com.floreantpos.model.dao.PackagingUnitDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IUpdatebleView;
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

	private javax.swing.JTabbedPane tabbedPane;
	private javax.swing.JPanel tabMenuIems;
	private javax.swing.JPanel tabInvItem;
	protected JTable menuTable;
	private javax.swing.JScrollPane jScrollPane;
	JPanel mainPanel = new JPanel();
	JTextField tfName = new FixedLengthTextField(60);
	IntegerTextField tfPackSizeReorderLevel = new IntegerTextField(30);
	IntegerTextField tfPackSizeReplenishLevel = new IntegerTextField(30);
	JTextArea tfDescription = new JTextArea(4, 10);
	private ListComboBoxModel unitModel = new ListComboBoxModel();
	private final JXComboBox cbPackagingUnit = new JXComboBox(this.unitModel);
	private final JXComboBox cbGroup = new JXComboBox();
	private final JXComboBox cbCompany = new JXComboBox();
	private final JXComboBox cbVendor = new JXComboBox();
	private final JXComboBox cbPackSize = new JXComboBox();
	private final JButton btnAdd = new JButton("Add");
	private final JButton btnDel = new JButton("Delete");
	protected JTable table;
	private JLabel compLabel;
	private JLabel nameLabel;
	private JLabel descLabel;
	private JLabel packLabel;
	private JLabel reorderLabel;
	private JLabel relenishLabel;
	private JLabel groupLabel;
	private JLabel distLabel;
	private JLabel packSizeLabel;
	private JLabel optionLabel;
	private HashSet<ItemCompVendPack> tbd;
	private final JButton btnNewGroup = new JButton("New Group");
	MenuItemDetailModel menuTableModel;

	public InventoryItemEntryForm() {
		createUI();
		tbd = new HashSet<ItemCompVendPack>();
		populateComboBoxes();
		setFieldsEnable(false);
	}

	private void populateComboBoxes() {
		List<PackagingUnit> packagingUnits = PackagingUnitDAO.getInstance().findAll();
		this.unitModel.setDataList(packagingUnits);

		List<InventoryGroup> groups = InventoryGroupDAO.getInstance().findAll();
		this.cbGroup.setModel(new DefaultComboBoxModel(groups.toArray(new InventoryGroup[0])));
		this.cbGroup.setSelectedIndex(-1);

		List<Company> companies = CompanyDAO.getInstance().findAll();
		this.cbCompany.setModel(new DefaultComboBoxModel(companies.toArray(new Company[0])));
		this.cbCompany.setSelectedIndex(-1);

		List<InventoryVendor> vendors = InventoryVendorDAO.getInstance().findAllExpenseVendors(false);
		this.cbVendor.setModel(new DefaultComboBoxModel(vendors.toArray(new InventoryVendor[0])));
		this.cbVendor.setSelectedIndex(-1);

		List<PackSize> packSize = PackSizeDAO.getInstance().findAll();
		this.cbPackSize.setModel(new DefaultComboBoxModel(packSize.toArray(new PackSize[0])));
		this.cbPackSize.setSelectedIndex(-1);

	}

	public void createNew() {
		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setCreateTime(new Date());
		inventoryItem.setAverageRunitPrice(0.0d);
		inventoryItem.setVisible(true);
		inventoryItem.setSortOrder(0);
		inventoryItem.setPackageBarcode(null);
		setBean(inventoryItem);
		clearTableModel();
		clearMenuTableModel();
	}

	public void clearFields() {
		this.tfName.setText("");
		this.tfPackSizeReorderLevel.setText("");
		this.tfPackSizeReplenishLevel.setText("");
		this.tfDescription.setText("");
		this.cbGroup.setSelectedIndex(-1);
		this.cbVendor.setSelectedIndex(-1);
		this.cbCompany.setSelectedIndex(-1);
		this.cbPackSize.setSelectedIndex(-1);
		this.cbPackagingUnit.setSelectedIndex(-1);
		clearTableModel();
		clearMenuTableModel();
	}

	public void setFieldsEnableEdit() {
		// this.cbPackagingUnit.setEnabled(false);
	}

	public void setFieldsEnable(boolean enable) {
		this.tfName.setEnabled(enable);
		this.cbPackagingUnit.setEnabled(enable);
		this.tfPackSizeReorderLevel.setEnabled(enable);
		this.tfPackSizeReplenishLevel.setEnabled(enable);
		this.tfDescription.setEnabled(enable);
		this.cbGroup.setEnabled(enable);
		this.cbVendor.setEnabled(enable);
		this.cbPackSize.setEnabled(enable);
		this.cbCompany.setEnabled(enable);
		this.compLabel.setEnabled(enable);
		this.nameLabel.setEnabled(enable);
		this.descLabel.setEnabled(enable);
		this.packLabel.setEnabled(enable);
		this.reorderLabel.setEnabled(enable);
		this.relenishLabel.setEnabled(enable);
		this.groupLabel.setEnabled(enable);
		this.distLabel.setEnabled(enable);
		this.packSizeLabel.setEnabled(enable);
		this.optionLabel.setEnabled(enable);
		this.btnAdd.setEnabled(enable);
		this.btnDel.setEnabled(enable);
		this.table.setEnabled(enable);
		this.btnNewGroup.setEnabled(enable);
	}

	private void createUI() {
		tabbedPane = new javax.swing.JTabbedPane();
		tabInvItem = new javax.swing.JPanel();
		tabMenuIems = new javax.swing.JPanel();
		tabbedPane.addTab(com.floreantpos.POSConstants.GENERAL, tabInvItem);
		tabbedPane.addTab("Menu Items", tabMenuIems);
		tabInvItem.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));
		setLayout(new BorderLayout());

		tabInvItem.add(nameLabel = new JLabel("Name"), "cell 0 0,alignx trailing");
		tabInvItem.add(this.tfName, "cell 1 0");
		tabInvItem.add(descLabel = new JLabel("Description"), "cell 0 2,alignx trailing");
		this.tfDescription.setTabSize(4);
		JScrollPane scrollPane = new JScrollPane(this.tfDescription);
		tabInvItem.add(scrollPane, "cell 1 2");
		tabInvItem.add(packLabel = new JLabel("Packaging unit"), "cell 0 3,alignx trailing");
		tabInvItem.add(this.cbPackagingUnit, "cell 1 3");
		tabInvItem.add(reorderLabel = new JLabel("Reorder level"), "cell 0 6,alignx trailing");
		tabInvItem.add(this.tfPackSizeReorderLevel, "cell 1 6");
		tabInvItem.add(relenishLabel = new JLabel("Replenish level"), "cell 0 7,alignx trailing");
		tabInvItem.add(this.tfPackSizeReplenishLevel, "cell 1 7");
		tabInvItem.add(groupLabel = new JLabel("Group"), "cell 0 13,alignx trailing");
		tabInvItem.add(this.cbGroup, "cell 1 13");
		this.btnNewGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InventoryItemEntryForm.this.createNewGroup();
			}
		});
		tabInvItem.add(this.btnNewGroup, "cell 3 13,growx");

		JPanel hPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel hPanel1 = new JPanel(new FlowLayout(FlowLayout.LEADING));

		this.cbCompany.setPreferredSize(new Dimension(110, 20));
		this.cbVendor.setPreferredSize(new Dimension(110, 20));
		this.cbPackSize.setPreferredSize(new Dimension(110, 20));
		this.cbPackSize.setEditable(true);
		this.cbPackSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PackSizeDAO packSizeDao = PackSizeDAO.getInstance();
				if (e.getActionCommand().equals("comboBoxEdited")) {
					JXComboBox combo = ((JXComboBox) e.getSource());
					String o = combo.getSelectedItem().toString();
					if (o != null) {
						int size = 0;
						try {
							size = Integer.parseInt(o);
							PackSize ps = null;
							if ((ps = packSizeDao.findPackSize(size)) == null) {
								ps = new PackSize(size);
								packSizeDao.save(ps);
								combo.addItem(ps);
								combo.setSelectedItem(ps);
							} else {
								combo.setSelectedItem(ps);
							}
						} catch (Exception exp) {
							exp.printStackTrace();
							POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please enter a valid Pack Size!");
						}
					}
				}
			}
		});

		compLabel = new JLabel("Company");
		distLabel = new JLabel("Distributor");
		packSizeLabel = new JLabel("Pack Size");
		compLabel.setPreferredSize(new Dimension(120, 20));
		distLabel.setPreferredSize(new Dimension(120, 20));
		packSizeLabel.setPreferredSize(new Dimension(120, 20));

		hPanel.add(optionLabel = new JLabel("Options :  "), "cell 0 3,alignx trailing");
		hPanel.add(compLabel, "cell 1 3,alignx trailing");
		hPanel.add(distLabel, "cell 2 3,alignx trailing");
		hPanel.add(packSizeLabel, "cell 3 3,alignx trailing");

		JLabel j4 = new JLabel("Item Options");
		hPanel1.add(j4, "cell 0 3,alignx trailing");
		j4.setForeground(getBackground());
		hPanel1.add(this.cbCompany, "cell 1 3,alignx");
		hPanel1.add(this.cbVendor, "cell 2 3,alignx");
		hPanel1.add(this.cbPackSize, "cell 3 3,alignx");
		this.btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InventoryItemEntryForm.this.addNewTuple();
			}
		});
		hPanel1.add(this.btnAdd, "cell 4 3,alignx");
		hPanel.setPreferredSize(new Dimension(500, 40));
		hPanel1.setPreferredSize(new Dimension(500, 40));
		tabInvItem.add(hPanel, "cell 0 15 3 3");
		tabInvItem.add(hPanel1, "cell 0 18 3 3");

		this.btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InventoryItemEntryForm.this.deleteSelectedTuple();
			}
		});
		this.table = new JTable(new InventoryItemDetailModel());
		InventoryItemDetailModel tableModel = (InventoryItemDetailModel) this.table.getModel();
		tableModel.setPageSize(100);

		JScrollPane jsp = new JScrollPane(this.table);
		jsp.setPreferredSize(new Dimension(500, 200));
		tabInvItem.add(jsp, "cell 1 30 3 3");
		tabInvItem.add(btnDel, "cell 1 35 2 1");

		jScrollPane = new javax.swing.JScrollPane();
		tabMenuIems.setLayout(new BorderLayout());
		this.menuTable = new JTable(new MenuItemDetailModel());
		jScrollPane.setPreferredSize(new Dimension(500, 200));
		menuTableModel = (MenuItemDetailModel) this.menuTable.getModel();
		menuTableModel.setPageSize(100);
		tabMenuIems.add(jScrollPane);

		add(tabbedPane);
	}

	protected void createNewGroup() {
		InventoryGroupEntryForm form = new InventoryGroupEntryForm(new InventoryGroup());
		BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
		dialog.pack();
		dialog.open();
		if (dialog.isCanceled()) {
			return;
		}

		InventoryGroup inventoryGroup = (InventoryGroup) form.getBean();
		DefaultComboBoxModel<InventoryGroup> cbModel = (DefaultComboBoxModel) this.cbGroup.getModel();
		cbModel.addElement(inventoryGroup);
		cbModel.setSelectedItem(inventoryGroup);
	}

	protected void deleteSelectedTuple() {
		if (this.table.getSelectedRow() >= 0) {
			InventoryItemDetailModel tableModel = (InventoryItemDetailModel) this.table.getModel();
			tbd.add(tableModel.getRowData(this.table.getSelectedRow()));
			tableModel.deleteItem(this.table.getSelectedRow());
		}
	}

	protected void addNewTuple() {
		ItemCompVendPack icvp = new ItemCompVendPack();
		icvp.setInventoryItem(getBean());
		if (getBean().getPackagingUnit() == null) {
			if (cbPackagingUnit.getSelectedIndex() >= 0) {
				getBean().setPackagingUnit((PackagingUnit) cbPackagingUnit.getSelectedItem());
			} else {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please select a packaging unit first!");
				return;
			}
		}
		boolean allSet = true;
		if (this.cbCompany.getSelectedItem() != null) {
			icvp.setCompany((Company) this.cbCompany.getSelectedItem());
		} else {
			allSet = false;
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Company Name can not be empty!");
		}
		if (this.cbVendor.getSelectedItem() != null) {
			icvp.setInventoryVendor((InventoryVendor) this.cbVendor.getSelectedItem());
		} else {
			allSet = false;
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Distributor Name can not be empty!");
		}
		if (this.cbPackSize.getSelectedItem() != null) {
			icvp.setPackSize((PackSize) this.cbPackSize.getSelectedItem());
		} else {
			allSet = false;
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Pack Size can not be empty!");
		}
		if (allSet) {
			InventoryItemDetailModel tableModel = (InventoryItemDetailModel) this.table.getModel();
			boolean flag = false;
			if (tableModel.getRows() != null) {
				for (ItemCompVendPack icvp1 : tableModel.getRows()) {
					if (icvp1.equals(icvp)) {
						flag = true;
						break;
					}
				}
			}
			if (!flag) {
				if (tbd.contains(icvp)) {
					tbd.remove(icvp);
				}
				tableModel.addItem(icvp);
			} else {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Duplicate entry!");
			}
		}
	}

	public void updateView() {
		populateComboBoxes();
		InventoryItem inventoryItem = (InventoryItem) getBean();
		if (inventoryItem == null) {
			return;
		}
		this.tfName.setText(inventoryItem.getName());
		this.cbPackagingUnit.setSelectedItem(inventoryItem.getPackagingUnit());
		this.tfPackSizeReorderLevel.setText(String.valueOf(inventoryItem.getPackageReorderLevel()));
		this.tfPackSizeReplenishLevel.setText(String.valueOf(inventoryItem.getPackageReplenishLevel()));
		this.tfDescription.setText(inventoryItem.getDescription());
		this.cbGroup.setSelectedItem(inventoryItem.getInventoryGroup());
		this.cbGroup.setSelectedIndex(-1);
		this.cbCompany.setSelectedIndex(-1);
		this.cbVendor.setSelectedIndex(-1);
		this.cbPackSize.setSelectedIndex(-1);
		tbd.clear();
		loadTableData();
		if (tabMenuIems != null) {
			menuTableModel.setRows(getMenu(inventoryItem));
		}
	}

	public void clearMenuTableModel() {
		if (this.menuTable != null && this.menuTable.getModel() != null) {
			MenuItemDetailModel tableModel = (MenuItemDetailModel) this.menuTable.getModel();
			tableModel.setRows(null);
		}
	}

	public List<Pair<MenuItem, RecepieItem>> getMenu(InventoryItem item) {
		Set<RecepieItem> recItems = item.getRecepieItems();
		List<Pair<MenuItem, RecepieItem>> mrList = new ArrayList<Pair<MenuItem, RecepieItem>>();
		for (RecepieItem r : recItems) {
			Pair<MenuItem, RecepieItem> mr = Pair.of(r.getRecepie().getMenuItem(), r);
			mrList.add(mr);
		}
		return mrList;
	}

	String formatDouble(double d) {
		NumberFormat f = new DecimalFormat("0.##");
		return f.format(d);
	}

	public boolean updateModel() {
		InventoryItem inventoryItem = (InventoryItem) getBean();
		if (inventoryItem == null) {
			createNew();
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
		inventoryItem.setLastUpdateDate(new Date());
		if (this.cbGroup.getSelectedItem() != null) {
			inventoryItem.setInventoryGroup((InventoryGroup) this.cbGroup.getSelectedItem());
		} else {
			inventoryItem.setInventoryGroup(null);
		}
		menuTableModel.setRows(getMenu(inventoryItem));

		int tabCount = tabbedPane.getTabCount();
		for (int i = 0; i < tabCount; i++) {
			Component componentAt = tabbedPane.getComponent(i);
			if (componentAt instanceof IUpdatebleView) {
				IUpdatebleView view = (IUpdatebleView) componentAt;
				if (!view.updateModel(inventoryItem)) {
					return false;
				}
			}
		}
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
					} else if (item.getPackageReorderLevel() != -100 && item.getPackageReorderLevel() < 0) {
						actionPerformed = false;
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Package Reorder Level!!");
					} else if (item.getPackageReplenishLevel() != -100 && item.getPackageReplenishLevel() < 0) {
						actionPerformed = false;
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Package Replenish Level!!");
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
						ItemCompVendPackDAO icvpDao = ItemCompVendPackDAO.getInstance();
						InventoryItemDetailModel tableModel = (InventoryItemDetailModel) this.table.getModel();
						if (tableModel.getRows() != null) {
							for (ItemCompVendPack ic : tableModel.getRows()) {
								icvpDao.saveOrUpdate(ic);
							}
						}
						for (ItemCompVendPack ic : tbd) {
							if (ic.getId() != null) {
								icvpDao.refresh(ic);
								icvpDao.delete(ic);
							}
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

	// public boolean delete() {
	// InventoryItem inventoryItem = (InventoryItem) getBean();
	// if (inventoryItem == null) {
	// return false;
	// }
	// InventoryItemDAO.getInstance().delete(inventoryItem);
	// return true;
	// }

	public String getDisplayText() {
		InventoryItem inventoryItem = (InventoryItem) getBean();
		if ((inventoryItem == null) || (inventoryItem.getId() == null)) {
			return "Add new inventory item";
		}
		return "Edit inventory item";
	}

	public void loadTableData() {
		InventoryItem inventoryItem = (InventoryItem) getBean();
		InventoryItemDetailModel tableModel = (InventoryItemDetailModel) this.table.getModel();
		if (inventoryItem.getId() != null) {
			List<ItemCompVendPack> tuple = ItemCompVendPackDAO.getInstance().findAllByInventoryItem(inventoryItem);
			tableModel.setRows(tuple);
		}
	}

	public void clearTableModel() {
		if (this.table != null && this.table.getModel() != null) {
			InventoryItemDetailModel tableModel = (InventoryItemDetailModel) this.table.getModel();
			tableModel.setRows(null);
		}
	}

	static class InventoryItemDetailModel extends ListTableModel<ItemCompVendPack> {
		private static final long serialVersionUID = -5532926699493030221L;

		public InventoryItemDetailModel() {
			super(new String[] { "Company", "Distributor", "PackSize" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			ItemCompVendPack row = (ItemCompVendPack) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getCompany().getName();
			case 1:
				return row.getInventoryVendor().getName();
			case 2:
				return row.getPackSize().getSize() + " " + row.getInventoryItem().getPackagingUnit().getName();
			}
			return null;
		}

	}

	static class MenuItemDetailModel extends ListTableModel<Pair<MenuItem, RecepieItem>> {
		private static final long serialVersionUID = -5532926699493030221L;

		public MenuItemDetailModel() {
			super(new String[] { "Menu Item", "Quantity", "Unit" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Pair<MenuItem, RecepieItem> row = (Pair<MenuItem, RecepieItem>) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getLeft().getName();
			case 1:
				return formatDouble(row.getRight().getPercentage());
			case 2:
				if (row.getRight().getInventoryItem() != null && row.getRight().getInventoryItem().getPackagingUnit() != null) {
					return row.getRight().getInventoryItem().getPackagingUnit().getRecepieUnitName();
				} else {
					return "";
				}
			}
			return "";
		}
	}
}