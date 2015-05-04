package com.orostock.inventory.ui.recepie;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXComboBox;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;

public class InventoryItemSelector extends BeanEditorDialog {
	JXComboBox comboBox = new JXComboBox();
	DoubleTextField tfQty = new DoubleTextField(5);
	JLabel quantityLabel = new JLabel("Quantity in");
	InventoryItem selectedItem;
	double quantity;
	boolean dataLoaded;

	public InventoryItemSelector() {
		super(BackOfficeWindow.getInstance(), true);
		setBeanEditor(new InventoryItemView());
	}

	public InventoryItem getSelectedItem() {
		return this.selectedItem;
	}

	public double getPercentage() {
		return this.quantity;
	}

	public void loadData() {
		if (this.dataLoaded)
			return;

		List<InventoryItem> list = InventoryItemDAO.getInstance().findAll();
		this.comboBox.setModel(new DefaultComboBoxModel(list.toArray(new InventoryItem[0])));
		if (list != null && list.size() > 0) {
			quantityLabel.setText("Quantity in " + list.get(0).getPackagingUnit().getRecepieUnitName());
		}
		this.dataLoaded = true;
	}

	public void dispose() {
		BeanEditor editor = getBeanEditor();

		super.dispose();

		setBeanEditor(editor);
	}

	class InventoryItemView extends BeanEditor<InventoryItem> implements ActionListener {
		public InventoryItemView() {
			setLayout(new BorderLayout());

			JPanel panel = new JPanel(new MigLayout());
			panel.add(new JLabel("Inventory item"));
			panel.add(InventoryItemSelector.this.comboBox, "w 250px,wrap");

			InventoryItemSelector.this.comboBox.addActionListener(this);
			panel.add(quantityLabel);
			panel.add(InventoryItemSelector.this.tfQty);
			add(panel);
		}

		public boolean save() {
			InventoryItemSelector.this.selectedItem = ((InventoryItem) InventoryItemSelector.this.comboBox.getSelectedItem());
			InventoryItemSelector.this.quantity = (float) InventoryItemSelector.this.tfQty.getDouble();

			return true;
		}

		protected void updateView() {
		}

		protected boolean updateModel() throws IllegalModelStateException {
			return true;
		}

		public String getDisplayText() {
			return "Select inventory item";
		}

		public void actionPerformed(ActionEvent arg0) {
			InventoryItem item = (InventoryItem) ((JXComboBox) arg0.getSource()).getSelectedItem();
			quantityLabel.setText("Quantity in " + item.getPackagingUnit().getRecepieUnitName());
		}
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.ui.recepie.InventoryItemSelector JD-Core Version:
 * 0.6.0
 */