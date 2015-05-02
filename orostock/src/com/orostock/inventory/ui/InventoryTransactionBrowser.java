package com.orostock.inventory.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.InOutEnum;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.dao.InventoryTransactionDAO;
import com.orostock.inventory.ui.form.InventoryTransactionEntryForm;

public class InventoryTransactionBrowser extends ModelBrowser<InventoryTransaction> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4133361713025286200L;
	private static InventoryTransactionEntryForm itForm = new InventoryTransactionEntryForm();

	public InventoryTransactionBrowser() {
		super(itForm);
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		init(new InventoryTransactionTableModel(), new Dimension(450, 400), new Dimension(500, 400));
		Dimension tableSize = new Dimension(450, 400);
		browserTable.getColumn("DATE").setPreferredWidth(80);
		browserTable.getColumn("TYPE").setPreferredWidth(20);
		browserTable.getColumn("QTY").setPreferredWidth(50);
		browserTable.getColumn("ITEM").setPreferredWidth(120);
		browserTable.getColumn("AMOUNT").setPreferredWidth(40);
		browserTable.getColumn("VAT").setPreferredWidth(20);
		browserTable.getColumn("CREDIT").setPreferredWidth(10);
		browserTable.getColumn("WAREHOUSE").setPreferredWidth(80);
		hideDeleteBtn();
		hideNewBtn();
		itForm.setFieldsEnable(false);
		refreshTable();
	}

	public void loadData() {
		List<InventoryTransaction> expense = InventoryTransactionDAO.getInstance().findByCurrentMonth();
		InventoryTransactionTableModel tableModel = (InventoryTransactionTableModel) this.browserTable.getModel();
		tableModel.setRows(expense);
	}

	public void refreshTable() {
		loadData();
		super.refreshTable();
	}

	public void refreshUITable() {
		super.refreshTable();
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		itForm.setFieldsEnable(false);
		InventoryTransaction bean = (InventoryTransaction) this.beanEditor.getBean();
		if ((bean != null) && (bean.getInventoryItem() != null)) {
			itForm.setNewTransaction(false);
			itForm.setInventoryItem(bean.getInventoryItem());
		}
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			itForm.setFieldsEnableAfterEdit();
		}
	}

	protected void searchPackagingUnit() {
	}

	static class InventoryTransactionTableModel extends ListTableModel<InventoryTransaction> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6168307011011117975L;

		public InventoryTransactionTableModel() {
			super(new String[] { "DATE", "TYPE", "QTY", "ITEM", "AMOUNT", "VAT", "CREDIT", "WAREHOUSE" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {

			InventoryTransaction row = (InventoryTransaction) getRowData(rowIndex);
			InOutEnum inOutEnum = InOutEnum.fromInt(row.getInventoryTransactionType().getInOrOut().intValue());
			switch (columnIndex) {
			case 0:
				if (row.getTransactionDate() != null) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
					return simpleDateFormat.format(row.getTransactionDate());
				}
			case 1:
				if (inOutEnum == InOutEnum.IN) {
					return "IN";
				} else if (inOutEnum == InOutEnum.OUT) {
					return "OUT";
				} else if (inOutEnum == InOutEnum.MOVEMENT) {
					return "MOV";
				} else if (inOutEnum == InOutEnum.ADJUSTMENT) {
					return "ADJ";
				} else if (inOutEnum == InOutEnum.WASTAGE) {
					return "WST";
				} else {
					return "";
				}
			case 2:
				if (row.getInventoryItem() != null && row.getInventoryItem().getPackagingUnit() != null) {
					if (inOutEnum == InOutEnum.IN || inOutEnum == InOutEnum.OUT || inOutEnum == InOutEnum.MOVEMENT) {
						return row.getQuantity() + " " + row.getInventoryItem().getPackagingUnit().getName();
					} else {
						return row.getQuantity() + " " + row.getInventoryItem().getPackagingUnit().getRecepieUnitName();
					}
				} else {
					return "";
				}
			case 3:
				if (row.getInventoryItem() != null) {
					return row.getInventoryItem().getName();
				} else {
					return "";
				}
			case 4:
				if (inOutEnum == InOutEnum.IN || inOutEnum == InOutEnum.OUT) {
					return "₹" + row.getTotalPrice();
				} else {
					return "-";
				}
			case 5:
				if (inOutEnum == InOutEnum.IN || inOutEnum == InOutEnum.OUT) {
					return row.getVatPaid().getRate() + " %";
				} else {
					return "-";
				}
			case 6:
				if (inOutEnum == InOutEnum.IN || inOutEnum == InOutEnum.OUT) {
					if (row.isCreditCheck()) {
						return "T";
					} else {
						return "F";
					}
				} else {
					return "-";
				}
			case 7:
				if (inOutEnum == InOutEnum.IN) {
					return row.getInventoryWarehouseByToWarehouseId().getName();
				} else if (inOutEnum == InOutEnum.OUT || inOutEnum == InOutEnum.ADJUSTMENT || inOutEnum == InOutEnum.WASTAGE) {
					return row.getInventoryWarehouseByFromWarehouseId().getName();
				} else if (inOutEnum == InOutEnum.MOVEMENT) {
					return row.getInventoryWarehouseByFromWarehouseId().getName() + " -> " + row.getInventoryWarehouseByToWarehouseId().getName();
				} else {
					return "";
				}
			}
			return null;
		}

	}
}
