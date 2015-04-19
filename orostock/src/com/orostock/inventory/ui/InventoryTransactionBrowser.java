package com.orostock.inventory.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXTable;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.ExplorerButtonPanel;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.dao.InventoryTransactionDAO;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;

public class InventoryTransactionBrowser extends TransparentPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4133361713025286200L;

	private List<InventoryTransaction> groupList;

	private JXTable table;
	private GroupExplorerTableModel tableModel;

	public InventoryTransactionBrowser() {
		InventoryTransactionDAO dao = new InventoryTransactionDAO();
		groupList = dao.findAll();

		tableModel = new GroupExplorerTableModel();
		table = new JXTable(tableModel);
		table.setDefaultRenderer(Object.class, new PosTableRenderer());

		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table));

		ExplorerButtonPanel explorerButton = new ExplorerButtonPanel();
		JButton editButton = explorerButton.getEditButton();

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					index = table.convertRowIndexToModel(index);

					InventoryTransaction trans = groupList.get(index);

					InventoryTransactionEntryForm editor = new InventoryTransactionEntryForm();
					editor.setBean(trans);
					editor.setInventoryItem(trans.getInventoryItem());
					BeanEditorDialog dialog = new BeanEditorDialog(editor, BackOfficeWindow.getInstance(), true);
					dialog.open();
					if (dialog.isCanceled())
						return;
					table.repaint();
				} catch (Exception x) {
					BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
				}
			}

		});

		TransparentPanel panel = new TransparentPanel();
		panel.add(editButton);
		add(panel, BorderLayout.SOUTH);
	}

	class GroupExplorerTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2312553573667836103L;

		String[] columnNames = { "TRANSACTION TYPE", "TRANSACTION DATE", "VENDOR", "AMOUNT", "VAT", "CREDIT", "REMARKS" };

		public int getRowCount() {
			if (groupList == null) {
				return 0;
			}
			return groupList.size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (groupList == null)
				return ""; //$NON-NLS-1$

			InventoryTransaction group = groupList.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return group.getTransactionType().getName();

			case 1:
				return group.getTransactionDate().toString();

			case 2:
				return group.getVendor().getName();

			case 3:
				return group.getUnitPrice();

			case 4:
				return group.getVatPaid();

			case 5:
				return group.getCreditCheck().toString();

			case 6:
				return group.getRemark();
			}
			return null;
		}

	}
}
