package com.orostock.inventory.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.PackagingUnit;
import com.floreantpos.model.dao.PackagingUnitDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class PackagingUnitEntryForm extends BeanEditor<PackagingUnit> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4705036150381956350L;
	private POSTextField tfPName;
	private POSTextField tfRName;
	private DoubleTextField tfFactor;
	private JLabel fLabel;
	JPanel mainPanel = new JPanel();

	public PackagingUnitEntryForm() {
		setLayout(new BorderLayout());
		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout());
		add(this.mainPanel);

		this.mainPanel.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));

		mainPanel.add(new JLabel("Packaging Unit"));
		this.tfPName = new POSTextField();
		mainPanel.add(this.tfPName, "grow, wrap");

		mainPanel.add(new JLabel("Recipe Unit"));
		this.tfRName = new POSTextField();
		mainPanel.add(this.tfRName, "grow, wrap");

		mainPanel.add(fLabel = new JLabel("Factor"));
		this.tfFactor = new DoubleTextField(20);
		this.tfFactor.setText("");
		mainPanel.add(this.tfFactor, "grow, wrap");
		setFieldsEnable(false);
	}

	public void createNew() {
		setBean(new PackagingUnit());
		this.tfFactor.setText("");
	}

	public void clearFields() {
		this.tfPName.setText("");
		this.tfRName.setText("");
		this.tfFactor.setText("");
	}

	public void setFieldsEnable(boolean enable) {
		this.tfPName.setEnabled(enable);
		this.tfRName.setEnabled(enable);
		this.tfFactor.setEnabled(enable);
	}

	public void setFieldsEnableEdit() {
		this.tfPName.setEnabled(true);
		this.tfRName.setEnabled(true);
		this.tfFactor.setEnabled(true);
		this.fLabel.setEnabled(true);
	}

	public void updateView() {
		PackagingUnit model = (PackagingUnit) getBean();
		if (model == null) {
			return;
		}
		this.tfPName.setText(model.getName());
		this.tfRName.setText(model.getRecepieUnitName());
		this.tfFactor.setText(model.getFactor().toString());
		this.tfFactor.setEnabled(false);
	}

	public boolean updateModel() {
		PackagingUnit model = (PackagingUnit) getBean();
		if (model == null) {
			model = new PackagingUnit();
			setBean(model);
		}
		String pNameString = this.tfPName.getText();
		String rNameString = this.tfRName.getText();
		if (StringUtils.isEmpty(pNameString)) {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Packaging Unit!!");
		} else if (StringUtils.isEmpty(rNameString)) {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Recipe Unit!!");
		}
		model.setName(pNameString);
		model.setRecepieUnitName(rNameString);
		model.setFactor(Double.valueOf(this.tfFactor.getDouble()));
		return true;
	}

	public boolean delete() {
		PackagingUnit pUnit = (PackagingUnit) getBean();
		if (pUnit == null) {
			return false;
		}
		PackagingUnitDAO.getInstance().delete(pUnit);
		return true;
	}

	public String getDisplayText() {
		PackagingUnit pUnit = (PackagingUnit) getBean();
		if ((pUnit == null) || (pUnit.getId() == null)) {
			return "Add new Packaging Unit";
		}
		return "Edit Packaging Unit";
	}

	public boolean save() {
		try {
			if (!updateModel()) {
				return false;
			}
			PackagingUnit pUnit = (PackagingUnit) getBean();
			if (pUnit.getFactor().isNaN() || pUnit.getFactor() == 0) {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Factor!!");
				return false;
			} else {
				PackagingUnitDAO dao = PackagingUnitDAO.getInstance();
				dao.saveOrUpdate((PackagingUnit) getBean());
				return true;
			}
		} catch (Exception e) {
			POSMessageDialog.showError(e.getMessage(), e);
			return false;
		}
	}

}
