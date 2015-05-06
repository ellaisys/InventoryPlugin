package com.orostock.inventory.ui.form;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.PosException;
import com.floreantpos.model.PackSize;
import com.floreantpos.model.dao.PackSizeDAO;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class InventoryPackSizeEntryForm extends BeanEditor<PackSize> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4371798664714860336L;
	private JCheckBox chkVisible;
	private IntegerTextField tfName;

	public InventoryPackSizeEntryForm() {
		createUI();
	}

	public InventoryPackSizeEntryForm(PackSize packSize) {
		createUI();

		setBean(packSize);
	}

	private void createUI() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new MigLayout("", "[][grow]", "[][]"));

		JLabel lblName = new JLabel("Name");
		panel.add(lblName, "cell 0 0,alignx trailing");

		this.tfName = new IntegerTextField();
		panel.add(this.tfName, "cell 1 0,growx");

		this.chkVisible = new JCheckBox("Visible", true);
		panel.add(this.chkVisible, "cell 1 1");
	}

	public void clearView() {
		this.tfName.setText("");
		this.chkVisible.setSelected(true);
	}

	public void updateView() {
		PackSize packSize = (PackSize) getBean();
		if (packSize == null) {
			clearView();
			return;
		}
		this.tfName.setText(String.valueOf(packSize.getSize()));
		this.chkVisible.setSelected(packSize.getVisible().booleanValue());
	}

	public boolean updateModel() {
		PackSize packSize = (PackSize) getBean();
		if (packSize == null) {
			packSize = new PackSize();
		}
		if (Integer.valueOf(this.tfName.getInteger()).intValue() <= 0) {
			throw new PosException("Size can't be 0");
		} else {
			packSize.setSize(tfName.getInteger());
		}
		packSize.setVisible(Boolean.valueOf(this.chkVisible.isSelected()));

		return true;
	}

	public String getDisplayText() {
		return "Add Pack Size";
	}

	public boolean save() {
		try {
			if (!updateModel()) {
				return false;
			}
			PackSize model = (PackSize) getBean();
			PackSizeDAO.getInstance().saveOrUpdate(model);

			return true;
		} catch (Exception e) {
			POSMessageDialog.showError(e.getMessage());
		}

		return false;
	}

	@Override
	public void clearTableModel() {
		// TODO Auto-generated method stub

	}

}
