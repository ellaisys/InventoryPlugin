package com.orostock.inventory.ui.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXComboBox;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.Company;
import com.floreantpos.model.CompanyPerson;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.ItemCompVendPack;
import com.floreantpos.model.Person;
import com.floreantpos.model.dao.CompanyDAO;
import com.floreantpos.model.dao.CompanyPersonDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao.ItemCompVendPackDAO;
import com.floreantpos.model.dao.PersonDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class CompanyEntryForm extends BeanEditor<Company> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4705036150381956350L;
	private javax.swing.JTabbedPane tabbedPane;
	private javax.swing.JPanel tabCompany;
	private javax.swing.JPanel tabDistributors;
	protected JTable distTable;
	private javax.swing.JScrollPane jScrollPane;
	private POSTextField tfName;
	private TextField tfPhone;
	private TextField tfEmail;
	private JTextArea taAddress;
	private JLabel nameLabel;
	private JLabel phoneLabel;
	private JLabel emailLabel;
	private JLabel addLabel;
	private JXComboBox cbPerson = new JXComboBox();
	protected JTable table;
	private JLabel persLabel;
	private HashSet<CompanyPerson> tbd;
	JPanel mainPanel = new JPanel();
	private final JButton btnAdd = new JButton("Add");
	private final JButton btnDel = new JButton("Delete");

	public CompanyEntryForm() {
		createUI();
		tbd = new HashSet<CompanyPerson>();
		populateComboBoxes();
		setFieldsEnable(false);
	}

	public void clearTableModel() {
		if (this.table != null && this.table.getModel() != null) {
			CompanyDetailModel tableModel = (CompanyDetailModel) this.table.getModel();
			tableModel.setRows(null);
		}
	}

	private void populateComboBoxes() {
		List<Person> pList = PersonDAO.getInstance().findAll();
		this.cbPerson.setModel(new DefaultComboBoxModel(pList.toArray(new Person[0])));
		this.cbPerson.setSelectedIndex(-1);
	}

	public void createNew() {
		Company comp = new Company();
		setBean(comp);
	}

	private void createUI() {
		tabbedPane = new JTabbedPane();
		tabCompany = new JPanel();
		tabDistributors = new JPanel();
		tabCompany.setPreferredSize(new Dimension(500, 400));
		tabDistributors.setPreferredSize(new Dimension(500, 400));
		tabbedPane.setPreferredSize(new Dimension(500, 400));

		tabbedPane.addTab(com.floreantpos.POSConstants.GENERAL, tabCompany);
		tabbedPane.addTab("Distributors", tabDistributors);
		tabCompany.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));
		setLayout(new BorderLayout());

		tabCompany.add(nameLabel = new JLabel("Name"), "cell 0 2,alignx trailing");
		this.tfName = new POSTextField();
		tabCompany.add(this.tfName, "grow, wrap");

		tabCompany.add(phoneLabel = new JLabel("Phone"), "cell 0 5,alignx trailing");
		this.tfPhone = new TextField(20);
		tabCompany.add(this.tfPhone, "grow, wrap");

		tabCompany.add(emailLabel = new JLabel("Email"), "cell 0 8,alignx trailing");
		this.tfEmail = new TextField(40);
		tabCompany.add(this.tfEmail, "grow, wrap");

		tabCompany.add(addLabel = new JLabel("Address"), "cell 0 11,alignx trailing");
		this.taAddress = new JTextArea(4, 10);
		this.taAddress.setTabSize(4);
		JScrollPane scrollPane = new JScrollPane(this.taAddress);
		tabCompany.add(scrollPane, "cell 1 11");

		tabCompany.add(persLabel = new JLabel("People"), "cell 0 15,alignx trailing");
		tabCompany.add(this.cbPerson, "cell 1 15,alignx");
		this.btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CompanyEntryForm.this.addNewPerson();
			}
		});
		tabCompany.add(this.btnAdd, "cell 2 15");

		this.table = new JTable(new CompanyDetailModel());
		CompanyDetailModel tableModel = (CompanyDetailModel) this.table.getModel();
		tableModel.setPageSize(30);
		JScrollPane jsp = new JScrollPane(this.table);
		jsp.setPreferredSize(new Dimension(500, 200));
		tabCompany.add(jsp, "cell 1 20");

		this.btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CompanyEntryForm.this.deleteSelectedPerson();
			}
		});
		tabCompany.add(btnDel, "cell 2 33");
		tabDistributors.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));
		this.distTable = new JTable(new DistributorTableModel());
		this.distTable.setPreferredSize(new Dimension(300, 400));
		jScrollPane = new JScrollPane(this.distTable);

		jScrollPane.setPreferredSize(new Dimension(500, 600));
		DistributorTableModel distModel = (DistributorTableModel) this.distTable.getModel();
		distModel.setPageSize(100);
		tabDistributors.add(jScrollPane, "cell 1 30 3 3");
		add(tabbedPane);
	}

	protected void deleteSelectedPerson() {
		if (this.table.getSelectedRow() >= 0) {
			CompanyDetailModel tableModel = (CompanyDetailModel) this.table.getModel();
			tbd.add(tableModel.getRowData(this.table.getSelectedRow()));
			tableModel.deleteItem(this.table.getSelectedRow());
		}
	}

	protected void addNewPerson() {
		CompanyPerson cp = new CompanyPerson();
		cp.setCompany(getBean());
		boolean allSet = true;
		if (this.cbPerson.getSelectedItem() != null) {
			cp.setPerson((Person) this.cbPerson.getSelectedItem());
		} else {
			allSet = false;
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Person Name can not be empty!");
		}
		if (allSet) {
			CompanyDetailModel tableModel = (CompanyDetailModel) this.table.getModel();
			boolean flag = false;
			if (tableModel.getRows() != null) {
				for (CompanyPerson cp1 : tableModel.getRows()) {
					if (cp1.equals(cp)) {
						flag = true;
						break;
					}
				}
			}
			if (!flag) {
				if (tbd.contains(cp)) {
					tbd.remove(cp);
				}
				tableModel.addItem(cp);
			} else {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Duplicate entry!");
			}
		}
	}

	public void clearFields() {
		this.tfName.setText("");
		this.tfPhone.setText("");
		this.tfEmail.setText("");
		this.taAddress.setText("");
		this.cbPerson.setSelectedIndex(-1);
		clearDistributorTableModel();
	}

	public void setFieldsEnable(boolean enable) {
		this.nameLabel.setEnabled(enable);
		this.phoneLabel.setEnabled(enable);
		this.emailLabel.setEnabled(enable);
		this.addLabel.setEnabled(enable);
		this.btnAdd.setEnabled(enable);
		this.btnDel.setEnabled(enable);
		this.tfName.setEnabled(enable);
		this.tfPhone.setEnabled(enable);
		this.tfEmail.setEnabled(enable);
		this.taAddress.setEnabled(enable);
		this.table.setEnabled(enable);
		this.persLabel.setEnabled(enable);
		this.cbPerson.setEnabled(enable);
		this.distTable.setEnabled(enable);
	}

	public void setFieldsEnableEdit() {
		this.tfName.setEnabled(true);
		this.tfPhone.setEnabled(true);
		this.tfEmail.setEnabled(true);
		this.taAddress.setEnabled(true);
		this.cbPerson.setEnabled(true);
		this.persLabel.setEnabled(true);
		this.table.setEnabled(true);
	}

	public void updateView() {
		populateComboBoxes();
		Company comp = (Company) getBean();
		if (comp == null) {
			return;
		}
		this.tfName.setText(comp.getName());
		this.tfPhone.setText(comp.getPhone());
		this.tfEmail.setText(comp.getEmail());
		this.taAddress.setText(comp.getAddress());
		this.cbPerson.setSelectedIndex(-1);
		tbd.clear();
		loadTableData();
		DistributorTableModel distTableModel = (DistributorTableModel) this.distTable.getModel();
		if (tabDistributors != null && comp.getId() != null) {
			distTableModel.setRows(getDistributor(comp));
		}
	}

	private List<InventoryVendor> getDistributor(Company comp) {
		List<ItemCompVendPack> icvps = ItemCompVendPackDAO.getInstance().findAllByCompany(comp);
		Set<InventoryVendor> distributors = new HashSet<InventoryVendor>();
		if (icvps != null) {
			for (ItemCompVendPack i : icvps) {
				distributors.add(i.getInventoryVendor());
			}
		}
		if (distributors == null) {
			return null;
		} else {
			List<InventoryVendor> disList = new ArrayList<InventoryVendor>(distributors);
			return disList;
		}
	}

	public boolean updateModel() {
		Company comp = (Company) getBean();
		if (comp == null) {
			createNew();
		}
		if (StringUtils.isEmpty(this.tfName.getText())) {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Company Name is empty");
			return false;
		}
		comp.setName(this.tfName.getText());
		comp.setPhone(this.tfPhone.getText());
		comp.setEmail(this.tfEmail.getText());
		comp.setAddress(this.taAddress.getText());
		return true;
	}

	// public boolean delete() {
	// Company company = (Company) getBean();
	// if (company == null) {
	// return false;
	// }
	// CompanyDAO.getInstance().delete(company);
	// return true;
	// }

	public boolean save() {
		Session session = CompanyDAO.getInstance().createNewSession();
		if (session != null) {
			Transaction tx = session.beginTransaction();
			boolean actionPerformed = false;
			try {
				if (updateModel()) {
					Company comp = (Company) getBean();
					if (comp.getName() == null) {
						actionPerformed = false;
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Company name!!");
					} else {
						CompanyDAO dao = CompanyDAO.getInstance();
						dao.saveOrUpdate(comp);
						actionPerformed = true;
						CompanyPersonDAO cpDao = CompanyPersonDAO.getInstance();
						CompanyDetailModel tableModel = (CompanyDetailModel) this.table.getModel();
						if (tableModel.getRows() != null) {
							for (CompanyPerson ic : tableModel.getRows()) {
								cpDao.saveOrUpdate(ic);
							}
						}
						for (CompanyPerson ic : tbd) {
							if (ic.getId() != null) {
								cpDao.refresh(ic);
								cpDao.delete(ic);
							}
						}
					}
				}
				if (actionPerformed) {
					tx.commit();
				} else {
					tx.rollback();
					return false;
				}
			} catch (Exception e) {
				if (tx != null) {
					tx.rollback();
				}
				session.close();
				POSMessageDialog.showError(e.getMessage(), e);
				return false;
			}
			return true;
		}
		return false;
	}

	public String getDisplayText() {
		Company company = (Company) getBean();
		if ((company == null) || (company.getId() == null)) {
			return "Add new Company";
		}
		return "Edit Company";
	}

	public void loadTableData() {
		Company comp = (Company) getBean();
		CompanyDetailModel tableModel = (CompanyDetailModel) this.table.getModel();
		if (comp.getId() != null) {
			List<CompanyPerson> tuple = CompanyPersonDAO.getInstance().findAllByCompany(comp);
			tableModel.setRows(tuple);
		}
	}

	public void clearDistributorTableModel() {
		if (this.distTable != null && this.distTable.getModel() != null) {
			DistributorTableModel tableModel = (DistributorTableModel) this.distTable.getModel();
			tableModel.setRows(null);
		}
	}

	static class CompanyDetailModel extends ListTableModel<CompanyPerson> {

		private static final long serialVersionUID = -819923946552684372L;

		public CompanyDetailModel() {
			super(new String[] { "Person", "Designation", "Phone" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			CompanyPerson row = (CompanyPerson) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getPerson().getName();
			case 1:
				return row.getPerson().getDesignation();
			case 3:
				return row.getPerson().getPhone();
			}
			return null;
		}

	}

	static class DistributorTableModel extends ListTableModel<InventoryVendor> {

		private static final long serialVersionUID = 2219488651760125616L;

		public DistributorTableModel() {
			super(new String[] { "Distributor" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryVendor row = (InventoryVendor) getRowData(rowIndex);
			Session session = InventoryVendorDAO.getInstance().createNewSession();
			if (row.getId() != null)
				session.refresh(row);
			try {
				switch (columnIndex) {
				case 0:
					return row.getName();
				}
				return "";
			} finally {
				session.close();
			}
		}
	}

}
