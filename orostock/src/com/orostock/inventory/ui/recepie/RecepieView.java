package com.orostock.inventory.ui.recepie;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.hibernate.Session;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.Recepie;
import com.floreantpos.model.RecepieItem;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.RecepieItemDAO;
import com.floreantpos.swing.IUpdatebleView;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class RecepieView extends JPanel implements IUpdatebleView<MenuItem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1197092484112412792L;
	JTable recepieItemTable;
	JButton btnAddItem = new JButton("Add Inventory Item");
	JButton btnDeleteItem = new JButton("Delete Selected Item");
	InventoryItemSelector itemSelector = new InventoryItemSelector();

	boolean inited = false;

	public RecepieView(MenuItem m) {
		recepieItemTable = new JTable(new RecepieItemTableModel());
		RecepieItemTableModel model = (RecepieItemTableModel) this.recepieItemTable.getModel();
		model.setPageSize(100);
		initView(m);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setFieldsEnable(false);
		this.btnAddItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RecepieView.this.addInventoryItem();
			}
		});
		this.btnDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = RecepieView.this.recepieItemTable.getSelectedRow();
				if (selectedRow < 0) {
					return;
				}

				RecepieView.RecepieItemTableModel model = (RecepieView.RecepieItemTableModel) RecepieView.this.recepieItemTable.getModel();
				model.deleteItem(selectedRow);
				recepieItemTable.invalidate();
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.btnAddItem);
		buttonPanel.add(this.btnDeleteItem);
		add(new JScrollPane(recepieItemTable));
		add(buttonPanel, "South");
	}

	protected void addInventoryItem() {
		this.itemSelector.loadData();
		this.itemSelector.open();

		if (this.itemSelector.isCanceled()) {
			return;
		}

		RecepieItem item = new RecepieItem();
		item.setPercentage(Double.valueOf(this.itemSelector.getPercentage()));
		item.setInventoryItem(this.itemSelector.getSelectedItem());

		RecepieItemTableModel model = (RecepieItemTableModel) this.recepieItemTable.getModel();
		List<RecepieItem> items = model.getRows();
		if (items != null && items.size() > 0) {
			for (RecepieItem r : items) {
				if (r.getInventoryItem().equals(item.getInventoryItem())) {
					POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Duplicate entry!");
					return;
				}
			}
		}
		model.addItem(item);
		recepieItemTable.invalidate();

	}

	public void setFieldsEnable(boolean enable) {
		this.recepieItemTable.setEnabled(enable);
		this.btnAddItem.setEnabled(enable);
		this.btnDeleteItem.setEnabled(enable);
		this.recepieItemTable.setEnabled(enable);
	}

	public boolean updateModel(MenuItem e) {
		RecepieItemTableModel model = (RecepieItemTableModel) this.recepieItemTable.getModel();

		Recepie recepie = e.getRecepie();
		if (recepie == null) {
			recepie = new Recepie();
			recepie.setMenuItem(e);
			e.setRecepie(recepie);
		}

		List<RecepieItem> rows = model.getRows();
		List<RecepieItem> toDel = new ArrayList<RecepieItem>();
		List<RecepieItem> finalList = new ArrayList<RecepieItem>();
		if (recepie.getRecepieItems() != null) {
			finalList.addAll(recepie.getRecepieItems());
			for (RecepieItem item : finalList) {
				if (!rows.contains(item)) {
					toDel.add(item);
				}
			}
		}
		finalList.removeAll(toDel);
		if (rows == null) {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "No recipe added!");
		}
		for (RecepieItem recepieItem : rows) {
			if (!finalList.contains(recepieItem)) {
				finalList.add(recepieItem);
			}
		}
		if (recepie.getRecepieItems() != null) {
			recepie.getRecepieItems().clear();
		}

		for (RecepieItem recepieItem : finalList) {
			recepie.addRecepieItem(recepieItem);
		}

		RecepieItemDAO dao = RecepieItemDAO.getInstance();
		for (RecepieItem recepieItem : toDel) {
			dao.delete(recepieItem);
		}
		return true;
	}

	public void initView(MenuItem e) {
		if (this.inited) {
			return;
		}
		// setFieldsEnable(false);
		Recepie recepie = e.getRecepie();
		if (recepie == null)
			return;

		List<RecepieItem> items = new ArrayList<RecepieItem>(recepie.getRecepieItems());
		RecepieItemTableModel model = (RecepieItemTableModel) this.recepieItemTable.getModel();
		model.setRows(items);
		this.inited = true;
	}

	public void updateView(MenuItem e) {
		Recepie recepie = e.getRecepie();
		if (recepie == null)
			return;

		List<RecepieItem> items = new ArrayList<RecepieItem>(recepie.getRecepieItems());
		if (this.recepieItemTable != null && this.recepieItemTable.getModel() != null) {
			RecepieItemTableModel model = (RecepieItemTableModel) this.recepieItemTable.getModel();
			model.setRows(items);
		}
	}

	public void clearTableModel() {
		if (this.recepieItemTable != null && this.recepieItemTable.getModel() != null) {
			RecepieItemTableModel tableModel = (RecepieItemTableModel) this.recepieItemTable.getModel();
			tableModel.setRows(null);
		}
	}

	public static class RecepieItemTableModel extends ListTableModel<RecepieItem> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2216293918891106404L;

		RecepieItemTableModel() {
			super(new String[] { "NAME", "QUANTITY", "UNIT", "COST" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			RecepieItem item = (RecepieItem) getRowData(rowIndex);
			Session session = InventoryItemDAO.getInstance().createNewSession();
			session.refresh(item.getInventoryItem());
			try {
				switch (columnIndex) {
				case 0:
					return item.getInventoryItem().getName();
				case 1:
					return formatDouble(item.getPercentage());
				case 2:
					return item.getInventoryItem().getPackagingUnit().getRecepieUnitName();
				case 3:
					return formatDouble(item.getPercentage() * item.getInventoryItem().getAverageRunitPrice());
				}
			} finally {
				session.close();
			}

			return null;
		}
	}
}
