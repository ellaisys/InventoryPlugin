package com.orostock.inventory.ui.form;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.PosException;
import com.floreantpos.model.ExpenseHead;
import com.floreantpos.model.dao.ExpenseHeadDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class ExpenseHeadEntryForm extends BeanEditor<ExpenseHead> {

	private static final long serialVersionUID = -5299431242215702793L;
	private JCheckBox chkVisible;
	private POSTextField tfName;

	public ExpenseHeadEntryForm() {
		createUI();
	}

	public ExpenseHeadEntryForm(ExpenseHead exHead) {
		createUI();
		setBean(exHead);
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
		ExpenseHead exHead = (ExpenseHead) getBean();
		if (exHead == null) {
			clearView();
			return;
		}
		this.tfName.setText(exHead.getName());
		this.chkVisible.setSelected(exHead.getVisible());
	}

	public boolean updateModel() {
		ExpenseHead exHead = (ExpenseHead) getBean();
		if (exHead == null) {
			exHead = new ExpenseHead();
		}
		String nameString = this.tfName.getText();
		if (StringUtils.isEmpty(nameString)) {
			throw new PosException("Name cannot be empty");
		}
		exHead.setName(nameString);
		exHead.setVisible(Boolean.valueOf(this.chkVisible.isSelected()));
		return true;
	}

	public String getDisplayText() {
		return "Add expense head";
	}

	public boolean save() {
		try {
			if (!updateModel()) {
				return false;
			}
			ExpenseHead model = (ExpenseHead) getBean();
			ExpenseHeadDAO.getInstance().saveOrUpdate(model);

			return true;
		} catch (Exception e) {
			POSMessageDialog.showError(e.getMessage());
		}
		return false;
	}
}
