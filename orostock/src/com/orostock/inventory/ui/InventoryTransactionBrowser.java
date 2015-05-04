package com.orostock.inventory.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.InOutEnum;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.dao.InventoryTransactionDAO;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.form.InventoryTransactionEntryForm;

public class InventoryTransactionBrowser extends ModelBrowser<InventoryTransaction> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4133361713025286200L;
	private static InventoryTransactionEntryForm itForm = new InventoryTransactionEntryForm();
	private JButton btnNewTrans = new JButton("NEW TRANSACTION");

	public InventoryTransactionBrowser() {
		super(itForm);
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		this.btnNewTrans.setActionCommand(Command.NEW_TRANSACTION.name());
		this.btnNewTrans.setEnabled(true);
		init(new InventoryTransactionTableModel(), new Dimension(450, 400), new Dimension(500, 400));
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

	protected JButton getAdditionalButton() {
		return this.btnNewTrans;
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
			this.btnNewTrans.setEnabled(true);
			itForm.setNewTransaction(false);
			itForm.setInventoryItem(bean.getInventoryItem());
		} else
			this.btnNewTrans.setEnabled(false);
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		InventoryTransaction bean = (InventoryTransaction) this.beanEditor.getBean();
		if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			itForm.setFieldsEnableAfterEdit();
		} else if (e.getActionCommand().equalsIgnoreCase(Command.NEW_TRANSACTION.name())) {
			InventoryTransactionEntryForm form = new InventoryTransactionEntryForm();
			form.setBean(new InventoryTransaction());
			BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
			dialog.pack();
			dialog.open();
			refreshTable();
		} else if (e.getActionCommand().equalsIgnoreCase(Command.CANCEL.name())) {
			this.btnNewTrans.setEnabled(true);
		} else {
			if ((bean != null) && (bean.getId() != null)) {
				this.btnNewTrans.setEnabled(true);
			} else
				this.btnNewTrans.setEnabled(false);
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
				} else if (inOutEnum == InOutEnum.SELF_CONSUMPTION) {
					return "SELF";
				} else {
					return "";
				}
			case 2:
				if (row.getInventoryItem() != null && row.getInventoryItem().getPackagingUnit() != null) {
					if (inOutEnum == InOutEnum.IN || inOutEnum == InOutEnum.OUT || inOutEnum == InOutEnum.MOVEMENT) {
						return formatDouble(row.getQuantity()) + " " + row.getInventoryItem().getPackagingUnit().getName();
					} else {
						return formatDouble(row.getQuantity()) + " " + row.getInventoryItem().getPackagingUnit().getRecepieUnitName();
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
					return "₹" + formatDouble(row.getTotalPrice());
				} else {
					return "-";
				}
			case 5:
				if (inOutEnum == InOutEnum.IN || inOutEnum == InOutEnum.OUT) {
					return formatDouble(row.getVatPaid().getRate()) + " %";
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
				} else if (inOutEnum == InOutEnum.OUT || inOutEnum == InOutEnum.ADJUSTMENT || inOutEnum == InOutEnum.WASTAGE || inOutEnum == InOutEnum.SELF_CONSUMPTION) {
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
