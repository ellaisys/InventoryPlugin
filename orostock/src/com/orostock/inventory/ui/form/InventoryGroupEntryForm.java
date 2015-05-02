package com.orostock.inventory.ui.form;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.PosException;
import com.floreantpos.model.InventoryGroup;
import com.floreantpos.model.dao.InventoryGroupDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class InventoryGroupEntryForm extends BeanEditor<InventoryGroup> {
	private JCheckBox chkVisible;
	private POSTextField tfName;

	public InventoryGroupEntryForm() {
		createUI();
	}

	public InventoryGroupEntryForm(InventoryGroup inventoryGroup) {
		createUI();

		setBean(inventoryGroup);
	}

	private void createUI() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new MigLayout("", "[][grow]", "[][]"));

		JLabel lblName = new JLabel("Name");
		panel.add(lblName, "cell 0 0,alignx trailing");

		this.tfName = new POSTextField();
		panel.add(this.tfName, "cell 1 0,growx");

		this.chkVisible = new JCheckBox("Visible", true);
		panel.add(this.chkVisible, "cell 1 1");
	}

	public void clearView() {
		this.tfName.setText("");
		this.chkVisible.setSelected(true);
	}

	public void updateView() {
		InventoryGroup inventoryGroup = (InventoryGroup) getBean();

		if (inventoryGroup == null) {
			clearView();
			return;
		}

		this.tfName.setText(inventoryGroup.getName());
		this.chkVisible.setSelected(inventoryGroup.isVisible().booleanValue());
	}

	public boolean updateModel() {
		InventoryGroup inventoryGroup = (InventoryGroup) getBean();

		if (inventoryGroup == null) {
			inventoryGroup = new InventoryGroup();
		}

		String nameString = this.tfName.getText();
		if (StringUtils.isEmpty(nameString)) {
			throw new PosException("Name cannot be empty");
		}

		inventoryGroup.setName(nameString);
		inventoryGroup.setVisible(Boolean.valueOf(this.chkVisible.isSelected()));

		return true;
	}

	public String getDisplayText() {
		return "Add inventory Group";
	}

	public boolean save() {
		try {
			if (!updateModel()) {
				return false;
			}

			InventoryGroup model = (InventoryGroup) getBean();
			InventoryGroupDAO.getInstance().saveOrUpdate(model);

			return true;
		} catch (Exception e) {
			POSMessageDialog.showError(e.getMessage());
		}

		return false;
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.ui.InventoryGroupEntryForm JD-Core Version: 0.6.0
 */