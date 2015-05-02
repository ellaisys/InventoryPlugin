package com.orostock.inventory.ui.form;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.PosException;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.dao.InventoryLocationDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;

public class InventoryLocationEntryForm extends BeanEditor<InventoryLocation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7721561583730518011L;
	private JCheckBox chkVisible;
	private POSTextField tfName;
	private JLabel lblSortOrder;
	private IntegerTextField tfSortOrder;
	private JLabel lblWarehouse;
	private JComboBox cbWarehouse;
	private JButton btnNewWarehouse;

	public InventoryLocationEntryForm(InventoryLocation ig) {
		setLayout(new BorderLayout());
		setBean(ig, false);
		createUI();
		loadWarehouses();
		updateView();
	}

	private void loadWarehouses() {
		try {
			List<InventoryWarehouse> warehouses = InventoryWarehouseDAO.getInstance().findAll();
			if (warehouses.size() > 0)
				this.cbWarehouse.setModel(new DefaultComboBoxModel(warehouses.toArray(new InventoryWarehouse[0])));
		} catch (Exception e) {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), e.getMessage(), e);
		}
	}

	private void createUI() {
		JPanel panel = new JPanel();
		add(panel, "Center");
		panel.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));

		JLabel lblName = new JLabel("Name");
		panel.add(lblName, "cell 0 0,alignx trailing");

		this.tfName = new POSTextField();
		panel.add(this.tfName, "cell 1 0,growx");

		this.lblSortOrder = new JLabel("Sort order");
		panel.add(this.lblSortOrder, "cell 0 1,alignx trailing");

		this.tfSortOrder = new IntegerTextField();
		panel.add(this.tfSortOrder, "cell 1 1,growx");

		this.lblWarehouse = new JLabel("Warehouse");
		panel.add(this.lblWarehouse, "cell 0 2,alignx trailing");

		this.cbWarehouse = new JComboBox();
		panel.add(this.cbWarehouse, "cell 1 2,growx");

		this.btnNewWarehouse = new JButton("...");
		this.btnNewWarehouse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InventoryLocationEntryForm.this.createNewWarehouse();
			}
		});
		panel.add(this.btnNewWarehouse, "cell 2 2");

		this.chkVisible = new JCheckBox("Visible", true);
		panel.add(this.chkVisible, "cell 1 3");
	}

	protected void createNewWarehouse() {
		InventoryWarehouseEntryForm form = new InventoryWarehouseEntryForm(new InventoryWarehouse());
		BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
		dialog.pack();
		dialog.open();

		if (dialog.isCanceled()) {
			return;
		}

		InventoryWarehouse warehouse = (InventoryWarehouse) form.getBean();
		DefaultComboBoxModel cbModel = (DefaultComboBoxModel) this.cbWarehouse.getModel();
		cbModel.addElement(warehouse);
		this.cbWarehouse.setSelectedItem(warehouse);
	}

	public boolean save() {
		try {
			if (!updateModel()) {
				return false;
			}

			InventoryLocation model = (InventoryLocation) getBean();
			InventoryLocationDAO.getInstance().saveOrUpdate(model);

			return true;
		} catch (Exception e) {
			POSMessageDialog.showError(e.getMessage());
		}

		return false;
	}

	public void updateView() {
		InventoryLocation model = (InventoryLocation) getBean();

		if (model == null) {
			return;
		}

		this.tfName.setText(model.getName());
		this.tfSortOrder.setText(String.valueOf(model.getSortOrder()));
		this.cbWarehouse.setSelectedItem(model.getWarehouse());

		if (model.getId() != null)
			this.chkVisible.setSelected(POSUtil.getBoolean(model.isVisible()));
	}

	public boolean updateModel() {
		InventoryLocation model = (InventoryLocation) getBean();

		if (model == null) {
			model = new InventoryLocation();
		}

		String nameString = this.tfName.getText();
		if (StringUtils.isEmpty(nameString)) {
			throw new PosException("Name cannot be empty");
		}

		model.setName(nameString);
		model.setSortOrder(Integer.valueOf(this.tfSortOrder.getInteger()));
		model.setWarehouse((InventoryWarehouse) this.cbWarehouse.getSelectedItem());
		model.setVisible(Boolean.valueOf(this.chkVisible.isSelected()));

		return true;
	}

	public String getDisplayText() {
		return "Add inventory location";
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.ui.InventoryLocationEntryForm JD-Core Version: 0.6.0
 */