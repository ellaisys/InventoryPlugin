package com.orostock.inventory.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JPanel;

import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.PackagingUnit;
import com.floreantpos.model.dao.PackagingUnitDAO;
import com.orostock.inventory.ui.form.PackagingUnitEntryForm;

public class PackagingUnitBrowser extends ModelBrowser<PackagingUnit> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3106023138023315427L;

	public PackagingUnitBrowser() {
		super(new PackagingUnitEntryForm());
		JPanel buttonPanel = new JPanel();
		this.browserPanel.add(buttonPanel, "South");
		init(new PackagingUnitTableModel(), new Dimension(500, 400), new Dimension(350, 400));
		hideDeleteBtn();
		beanEditor.setFieldsEnable(false);
		refreshTable();
	}

	public void loadData() {
		List<PackagingUnit> pUnits = PackagingUnitDAO.getInstance().findAll();
		PackagingUnitTableModel tableModel = (PackagingUnitTableModel) this.browserTable.getModel();
		tableModel.setRows(pUnits);
	}

	public void refreshTable() {
		loadData();
		super.refreshTable();
	}

	public void refreshUITable() {
		super.refreshTable();
	}

	protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(Command.EDIT.name())) {
			beanEditor.setFieldsEnableEdit();
		}
	}

	protected void searchPackagingUnit() {
	}

	static class PackagingUnitTableModel extends ListTableModel<PackagingUnit> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7406902255742305600L;

		public PackagingUnitTableModel() {
			super(new String[] { "PACKAGING UNIT", "RECEPIE UNIT", "FACTOR" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			PackagingUnit row = (PackagingUnit) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getName();
			case 1:
				return row.getRecepieUnitName();
			case 2:
				return formatDouble(row.getFactor());
			}
			return row.getName();
		}
	}
}
