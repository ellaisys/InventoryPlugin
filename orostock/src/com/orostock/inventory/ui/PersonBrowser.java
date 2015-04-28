package com.orostock.inventory.ui;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.Person;
import com.floreantpos.model.dao.PersonDAO;
import com.orostock.inventory.ui.form.PersonEntryForm;

public class PersonBrowser extends ModelBrowser<Person> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2457952169222336365L;
	private static PersonEntryForm pf = new PersonEntryForm();

	public PersonBrowser() {
		super(pf);
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		init(new PersonTableModel());
		pf.setFieldsEnable(false);
		hideDeleteBtn();
	}

	public void loadData() {
		List<Person> person = PersonDAO.getInstance().findAll();
		PersonTableModel tableModel = (PersonTableModel) this.browserTable.getModel();
		tableModel.setRows(person);
		tableModel.setPageSize(25);

	}

	public void refreshTable() {
		loadData();
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			pf.setFieldsEnableEdit();
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
	}

	protected void searchPerson() {
	}

	static class PersonTableModel extends ListTableModel<Person> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8008682351957964208L;

		public PersonTableModel() {
			super(new String[] { "NAME", "DESIGNATION", "PHONE", "EMAIL", "REFERENCE" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Person row = (Person) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getName();
			case 1:
				return row.getDesignation();
			case 2:
				return row.getPhone();
			case 3:
				return row.getEmail();
			case 4:
				return row.getRef();
			}
			return null;
		}
	}
}
