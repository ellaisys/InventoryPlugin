package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

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

public class InventoryItemBrowser extends ModelBrowser<InventoryItem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7358720735670997427L;
	private JButton btnNewTransaction = new JButton("NEW TRANSACTION");

	public InventoryItemBrowser() {
		super(new InventoryItemEntryForm());

		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		this.btnNewTransaction.setActionCommand(Command.NEW_TRANSACTION.name());
		this.btnNewTransaction.setEnabled(false);
		init(new InventoryItemTableModel());
		hideDeleteBtn();
		refreshTable();
	}

	public void loadData() {
		List<InventoryItem> inventoryItems = InventoryItemDAO.getInstance().findAll();
		InventoryItemTableModel tableModel = (InventoryItemTableModel) this.browserTable.getModel();
		tableModel.setRows(inventoryItems);
		tableModel.setPageSize(25);
	}

	public void refreshTable() {
		loadData();
		super.refreshTable();
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
		} else if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			this.btnNewTransaction.setEnabled(false);
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

		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryItem row = (InventoryItem) getRowData(rowIndex);
			InventoryWarehouseItemDAO dao = InventoryWarehouseItemDAO.getInstance();
			List<InventoryWarehouseItem> listItems = dao.findByInventoryItem(row);
			Double cafeRcpQty = 0.0d;
			Double godownRcpQty = 0.0d;
			if (listItems != null && listItems.size() == 2) {

				cafeRcpQty = listItems.get(0).getTotalRecepieUnits();
				godownRcpQty = listItems.get(1).getTotalRecepieUnits();
			}
			switch (columnIndex) {
			case 0:
				return row.getName();
			case 1:
				return this.f.format(cafeRcpQty) + " " + row.getPackagingUnit().getShortName();
			case 2:
				return this.f.format(godownRcpQty) + " " + row.getPackagingUnit().getShortName();
			}
			return row.getName();
		}
	}
}
