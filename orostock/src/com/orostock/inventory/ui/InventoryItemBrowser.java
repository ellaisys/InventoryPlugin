package com.orostock.inventory.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellRenderer;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.InventoryWarehouseItem;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.InventoryWarehouseItemDAO;
import com.floreantpos.ui.InventoryLevel;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.form.InventoryItemEntryForm;
import com.orostock.inventory.ui.form.InventoryTransactionEntryForm;

public class InventoryItemBrowser extends ModelBrowser<InventoryItem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7358720735670997427L;
	private JButton btnNewTransaction = new JButton("NEW TRANSACTION");
	private static InventoryItemEntryForm iif = new InventoryItemEntryForm();

	public InventoryItemBrowser() {
		super(iif);
		iif.clearTableModel();
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		this.btnNewTransaction.setActionCommand(Command.NEW_TRANSACTION.name());
		this.btnNewTransaction.setEnabled(false);
		init(new InventoryItemTableModel(), new Dimension(300, 400), new Dimension(650, 400));
		this.browserTable.getColumn(1).setCellRenderer(new InventoryLevelRenderer());
		this.browserTable.getColumn(2).setCellRenderer(new InventoryLevelRenderer());
		hideDeleteBtn();
	}

	public void loadData() {
		List<InventoryItem> inventoryItems = InventoryItemDAO.getInstance().findAll();
		InventoryItemTableModel tableModel = (InventoryItemTableModel) this.browserTable.getModel();
		tableModel.setRows(inventoryItems);
	}

	public void refreshTable() {
		loadData();
		super.refreshTable();
	}

	public void refreshUITable() {
		super.refreshTable();
	}

	protected JButton getAdditionalButton() {
		return this.btnNewTransaction;
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(Command.NEW_TRANSACTION.name())) {
			InventoryItem bean = (InventoryItem) this.beanEditor.getBean();
			InventoryTransactionEntryForm form = new InventoryTransactionEntryForm();
			form.setNewTransaction(true);
			form.setBean(new InventoryTransaction());
			form.setInventoryItem(bean);
			BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
			dialog.pack();
			dialog.open();
			refreshTable();
		} else if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			this.btnNewTransaction.setEnabled(false);
			iif.setFieldsEnableEdit(false);
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
		iif.setFieldsEnable(false);
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
				if (row.getPackageReplenishLevel() == -100) {
					return new InventoryLevel("NA", Color.GRAY);
				} else {
					if (cafeRcpQty <= row.getPackageReplenishLevel()) {
						return new InventoryLevel(formatDouble(cafeRcpQty) + " " + row.getPackagingUnit().getRecepieUnitName(), Color.YELLOW);
					} else {
						return new InventoryLevel(formatDouble(cafeRcpQty) + " " + row.getPackagingUnit().getRecepieUnitName(), Color.WHITE);
					}
				}
			case 2:
				if (row.getPackageReorderLevel() == -100) {
					return new InventoryLevel("NA", Color.GRAY);
				} else {
					if (godownRcpQty <= row.getPackageReorderLevel()) {
						return new InventoryLevel(formatDouble(godownRcpQty) + " " + row.getPackagingUnit().getRecepieUnitName(), Color.PINK);
					} else {
						return new InventoryLevel(formatDouble(godownRcpQty) + " " + row.getPackagingUnit().getRecepieUnitName(), Color.WHITE);
					}
				}

			}
			return row.getName();
		}
	}
}

class InventoryLevelRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	private Map<String, JLabel> labelMap = new HashMap<String, JLabel>();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

		// Cells are by default rendered as a JLabel.
		// JLabel l = (JLabel) super.getTableCellRendererComponent(table, value,
		// isSelected, hasFocus, row, col);
		String loc = row + "_" + col;

		JLabel l;
		if (labelMap.containsKey(loc)) {
			l = labelMap.get(loc);
		} else {
			l = new JLabel();
			l.setOpaque(true);
			labelMap.put(loc, l);
		}

		// Get the status for the current row.
		InventoryLevel level = (InventoryLevel) value;
		l.setBackground(level.getC());
		l.setText(level.getLevel());
		if (isSelected) {
			l.setBackground(table.getSelectionBackground());
			l.setForeground(table.getSelectionForeground());
		} else {
			l.setForeground(table.getForeground());

		}
		// Return the JLabel which renders the cell.
		return l;
	}
}
