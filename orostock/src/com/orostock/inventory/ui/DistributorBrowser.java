package com.orostock.inventory.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.orostock.inventory.ui.form.DistributorEntryForm;

public class DistributorBrowser extends ModelBrowser<InventoryVendor> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3216688728242415755L;
	private static DistributorEntryForm df = new DistributorEntryForm();

	public DistributorBrowser() {
		super(df);
		df.clearTableModel();
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		init(new DistributorTableModel(), new Dimension(300, 400), new Dimension(650, 400));
		df.setFieldsEnable(false);
		hideDeleteBtn();
		refreshTable();
	}

	public void loadData() {
		List<InventoryVendor> inventoryVendors = InventoryVendorDAO.getInstance().findAllExpenseVendors(false);
		DistributorTableModel tableModel = (DistributorTableModel) this.browserTable.getModel();
		tableModel.setRows(inventoryVendors);
	}

	public void refreshTable() {
		loadData();
		super.refreshTable();
	}

	public void refreshUITable() {
		super.refreshTable();
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			df.setFieldsEnableEdit();
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		df.setFieldsEnable(false);
	}

	protected void searchInventoryVendor() {
	}

	static class DistributorTableModel extends ListTableModel<InventoryVendor> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8008682351957964208L;

		public DistributorTableModel() {
			super(new String[] { "DISTRIBUTOR NAME", "PHONE", "ADDRESS" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryVendor row = (InventoryVendor) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getName();
			case 1:
				return row.getPhone();
			case 2:
				return row.getAddress();
			}
			return row.getName();
		}
	}

	public void clearTableModel() {
		DistributorTableModel tableModel = (DistributorTableModel) this.browserTable.getModel();
		tableModel.setRows(null);
	}
}
