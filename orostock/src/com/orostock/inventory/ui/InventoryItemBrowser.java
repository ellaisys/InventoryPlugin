package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.InventoryWarehouseItem;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.InventoryWarehouseItemDAO;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class InventoryItemBrowser extends ModelBrowser<InventoryItem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7358720735670997427L;
	private JButton btnNewTransaction = new JButton("NEW TRANSACTION");

	public InventoryItemBrowser() {
		super(new InventoryItemEntryForm());

		JPanel buttonPanel = new JPanel();
		// JButton btnExport = new JButton("Export Items");
		// btnExport.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// InventoryItemBrowser.this.exportInventoryItems();
		// }
		// });
		// buttonPanel.add(btnExport);

		this.browserPanel.add(buttonPanel, "South");

		this.btnNewTransaction.setActionCommand(Command.NEW_TRANSACTION.name());
		this.btnNewTransaction.setEnabled(false);

		init(new InventoryItemTableModel());
	}

	protected void exportInventoryItems() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "CSV file";
			}

			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".csv");
			}
		});
		fileChooser.setFileSelectionMode(0);
		int option = fileChooser.showSaveDialog(this);
		if (option != 0) {
			return;
		}

		File selectedFile = fileChooser.getSelectedFile();

		FileWriter out = null;
		try {
			out = new FileWriter(selectedFile);
			List<InventoryItem> inventoryItems = InventoryItemDAO.getInstance().findAll();

			String[] header = { "NAME", "UNIT_PER_PACKAGE", "TOTAL_PACKAGES", "AVERAGE_RUNIT_PRICE", "TOTAL_RECEPIE_UNITS", "UNIT_PURCHASE_PRICE",
					"PACKAGE_BARCODE", "UNIT_BARCODE", "PACKAGE_DESC", "SORT_ORDER", "PACKAGE_REORDER_LEVEL", "PACKAGE_REPLENISH_LEVEL",
					"DESCRIPTION", "UNIT_SELLING_PRICE" };

			String line = "";
			for (String string : header) {
				line = line + string + ",";
			}
			out.write(line);
			out.write("\n");

			for (Iterator<InventoryItem> i = inventoryItems.iterator(); i.hasNext();) {
				InventoryItem inventoryItem = i.next();
				line = "";

				line = line + inventoryItem.getName() + ",";
				line = line + inventoryItem.getUnitPerPackage() + ",";
				line = line + inventoryItem.getTotalPackages() + ",";
				line = line + inventoryItem.getAverageRUnitPrice() + ",";
				line = line + inventoryItem.getTotalRecepieUnits() + ",";
				line = line + inventoryItem.getUnitPurchasePrice() + ",";
				line = line + inventoryItem.getPackageBarcode() + ",";
				line = line + inventoryItem.getUnitBarcode() + ",";
				line = line + inventoryItem.getPackagingUnit() + ",";
				line = line + inventoryItem.getSortOrder() + ",";
				line = line + inventoryItem.getPackageReorderLevel() + ",";
				line = line + inventoryItem.getPackageReplenishLevel() + ",";
				line = line + inventoryItem.getDescription() + ",";
				line = line + inventoryItem.getUnitSellingPrice() + ",";

				out.write(line);
				out.write("\n");
			}

			JOptionPane.showMessageDialog(this, "Exported");
		} catch (Exception e) {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), e.getMessage());
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public void loadData() {
		List<InventoryItem> inventoryItems = InventoryItemDAO.getInstance().findAll();
		InventoryItemTableModel tableModel = (InventoryItemTableModel) this.browserTable.getModel();
		tableModel.setRows(inventoryItems);
	}

	public void refreshTable() {
		loadData();
	}

	protected JButton getAdditionalButton() {
		return this.btnNewTransaction;
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(Command.NEW_TRANSACTION.name())) {
			InventoryItem bean = (InventoryItem) this.beanEditor.getBean();
			InventoryTransactionEntryForm form = new InventoryTransactionEntryForm();
			form.setBean(new InventoryTransaction());
			form.setInventoryItem(bean);
			BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
			dialog.pack();
			dialog.open();
			refreshTable();
		} else {
			InventoryItem bean = (InventoryItem) this.beanEditor.getBean();
			if ((bean != null) && (bean.getId() != null)) {
				this.btnNewTransaction.setEnabled(true);
			} else
				this.btnNewTransaction.setEnabled(false);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);

		InventoryItem bean = (InventoryItem) this.beanEditor.getBean();
		if ((bean != null) && (bean.getId() != null)) {
			this.btnNewTransaction.setEnabled(true);
		} else
			this.btnNewTransaction.setEnabled(false);
	}

	protected void searchInventoryItem() {
	}

	static class InventoryItemTableModel extends ListTableModel<InventoryItem> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3011793405270921001L;
		NumberFormat f = new DecimalFormat("0.##");

		public InventoryItemTableModel() {
			super(new String[] { "ITEM NAME", "CAFE QUANTITY", "GODOWN QUANTITY" });
		}

		// public InventoryItemTableModel(List<InventoryItem> items) {
		// super(items);
		// }

		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryItem row = (InventoryItem) getRowData(rowIndex);
			InventoryWarehouseItemDAO dao = InventoryWarehouseItemDAO.getInstance();
			List<InventoryWarehouseItem> listItems = dao.findByInventoryItem(row);
			Double cafePcgQty = 0.0d;
			Double godownPcgQty = 0.0d;
			Double cafeRcpQty = 0.0d;
			Double godownRcpQty = 0.0d;
			double factor = row.getPackagingUnit().getFactor();
			if (listItems != null && listItems.size() == 2) {
				cafePcgQty = Math.floor(new Double((listItems.get(0).getTotalRecepieUnits() / factor)));
				godownPcgQty = Math.floor(new Double((listItems.get(1).getTotalRecepieUnits() / factor)));
				cafeRcpQty = listItems.get(0).getTotalRecepieUnits() - (cafePcgQty * factor);
				godownRcpQty = listItems.get(1).getTotalRecepieUnits() - (godownPcgQty * factor);
			}
			switch (columnIndex) {
			case 0:
				return row.getName();
			case 1:
				if (cafeRcpQty <= 0.0) {
					return this.f.format(cafePcgQty) + " " + row.getPackagingUnit().getName();
				} else {
					return this.f.format(cafePcgQty) + " " + row.getPackagingUnit().getName() + ", " + this.f.format(cafeRcpQty) + " "
							+ row.getPackagingUnit().getShortName();
				}
			case 2:
				if (godownRcpQty <= 0.0) {
					return this.f.format(godownPcgQty) + " " + row.getPackagingUnit().getName();
				} else {
					return this.f.format(godownPcgQty) + " " + row.getPackagingUnit().getName() + ", " + this.f.format(godownRcpQty) + " "
							+ row.getPackagingUnit().getShortName();
				}
			}
			return row.getName();
		}
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.ui.InventoryItemBrowser JD-Core Version: 0.6.0
 */