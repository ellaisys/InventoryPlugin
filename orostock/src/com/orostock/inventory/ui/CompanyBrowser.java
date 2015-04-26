package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.Company;
import com.floreantpos.model.dao.CompanyDAO;

public class CompanyBrowser extends ModelBrowser<Company> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2457952169222336365L;
	private static CompanyEntryForm cf = new CompanyEntryForm();

	public CompanyBrowser() {
		super(cf);
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		init(new CompanyTableModel());
		cf.setFieldsEnable(false);
		hideDeleteBtn();
	}

	public void loadData() {
		List<Company> company = CompanyDAO.getInstance().findAll();
		CompanyTableModel tableModel = (CompanyTableModel) this.browserTable.getModel();
		tableModel.setRows(company);
		tableModel.setPageSize(25);
	}

	public void refreshTable() {
		loadData();
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			cf.setFieldsEnableEdit();
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
	}

	protected void searchCompany() {
	}

	static class CompanyTableModel extends ListTableModel<Company> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8008682351957964208L;

		public CompanyTableModel() {
			super(new String[] { "NAME", "PHONE", "EMAIL", "ADDRESS", "PERSON1", "PERSON2", "PERSON3" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Company row = (Company) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getName();
			case 1:
				return row.getPhone();
			case 2:
				return row.getEmail();
			case 3:
				return row.getAddress();
			case 4:
				if (row.getPersonByP1Id() != null) {
					return row.getPersonByP1Id().getName();
				} else {
					return null;
				}
			case 5:
				if (row.getPersonByP2Id() != null) {
					return row.getPersonByP2Id().getName();
				} else {
					return null;
				}
			case 6:
				if (row.getPersonByP3Id() != null) {
					return row.getPersonByP3Id().getName();
				} else {
					return null;
				}
			}
			return row.getName();
		}
	}
}
