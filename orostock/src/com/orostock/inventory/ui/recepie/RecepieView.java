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

import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.Recepie;
import com.floreantpos.model.RecepieItem;
import com.floreantpos.model.dao.RecepieItemDAO;
import com.floreantpos.swing.IUpdatebleView;

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
		initView(m);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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
		model.addItem(item);
		recepieItemTable.invalidate();

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
		if (this.inited)
			return;

		Recepie recepie = e.getRecepie();
		if (recepie == null)
			return;

		List<RecepieItem> items = new ArrayList<RecepieItem>(recepie.getRecepieItems());
		RecepieItemTableModel model = (RecepieItemTableModel) this.recepieItemTable.getModel();
		model.setRows(items);

		this.inited = true;
	}

	public static class RecepieItemTableModel extends ListTableModel<RecepieItem> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2216293918891106404L;

		RecepieItemTableModel() {
			super(new String[] { "NAME", "QUANTITY", "UNIT" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			RecepieItem item = (RecepieItem) getRowData(rowIndex);

			switch (columnIndex) {
			case 0:
				return item.getInventoryItem().getName();
			case 1:
				return item.getPercentage();
			case 2:
				return item.getInventoryItem().getPackagingUnit().getShortName();
			}

			return null;
		}
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.ui.recepie.RecepieView JD-Core Version: 0.6.0
 */