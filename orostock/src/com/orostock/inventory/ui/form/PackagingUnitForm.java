package com.orostock.inventory.ui.form;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.model.PackagingUnit;
import com.floreantpos.model.dao.PackagingUnitDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class PackagingUnitForm extends BeanEditor<PackagingUnit> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4982566356163849129L;
	private FixedLengthTextField tfName = new FixedLengthTextField(30);
	private FixedLengthTextField tfShortName = new FixedLengthTextField(10);
	private DoubleTextField tfFactor = new DoubleTextField(10);

	public PackagingUnitForm() {
		this(null);
	}

	public PackagingUnitForm(PackagingUnit pu) {
		createUI();

		setBean(pu);
	}

	private void createUI() {
		setLayout(new MigLayout("fill"));

		add(new JLabel("Packing Unit Name"));
		add(this.tfName, "grow, wrap");

		add(new JLabel("Reciepe Unit name"));
		add(this.tfShortName, "grow, wrap");

		add(new JLabel("Multiplication Factor"));
		add(this.tfFactor, "wrap");
		this.tfFactor.setToolTipText("Total number of reciepe units in a package");

	}

	public boolean save() {
		try {
			if (!updateModel()) {
				return false;
			}
			PackagingUnitDAO.getInstance().save((PackagingUnit) getBean());

			return true;
		} catch (IllegalModelStateException e) {
			POSMessageDialog.showError(this, e.getMessage());
		}

		return false;
	}

	protected void updateView() {
		PackagingUnit packagingUnit = (PackagingUnit) getBean();
		if (packagingUnit == null) {
			return;
		}

		this.tfName.setText(packagingUnit.getName());
		this.tfShortName.setText(packagingUnit.getRecepieUnitName());
		this.tfFactor.setText(packagingUnit.getFactor() + "");
	}

	protected boolean updateModel() throws IllegalModelStateException {
		String name = this.tfName.getText();
		String shortName = this.tfShortName.getText();
		double factor = this.tfFactor.getDouble();

		if (StringUtils.isEmpty(name)) {
			throw new IllegalModelStateException("Please enter unit name");
		}

		if (PackagingUnitDAO.getInstance().nameExists(name)) {
			throw new IllegalModelStateException("A packaging unit with that name already exists");
		}

		if ((Double.isNaN(factor)) || (factor <= 0.0D)) {
			throw new IllegalModelStateException("Please make sure that factor is >= 1");
		}

		PackagingUnit packagingUnit = (PackagingUnit) getBean();
		if (packagingUnit == null) {
			packagingUnit = new PackagingUnit();
			setBean(packagingUnit, false);
		}

		packagingUnit.setName(name);
		packagingUnit.setRecepieUnitName(shortName);
		packagingUnit.setFactor(Double.valueOf(factor));

		return true;
	}

	public String getDisplayText() {
		return "Add/Edit packaing unit";
	}
}
