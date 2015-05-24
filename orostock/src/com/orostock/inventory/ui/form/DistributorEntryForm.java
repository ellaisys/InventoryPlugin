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
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.ItemCompVendPack;
import com.floreantpos.model.Person;
import com.floreantpos.model.VendorPerson;
import com.floreantpos.model.dao.CompanyDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao.ItemCompVendPackDAO;
import com.floreantpos.model.dao.PersonDAO;
import com.floreantpos.model.dao.VendorPersonDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class DistributorEntryForm extends BeanEditor<InventoryVendor> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4705036150381956350L;
	private javax.swing.JTabbedPane tabbedPane;
	private javax.swing.JPanel tabDistributor;
	private javax.swing.JPanel tabCompanies;
	protected JTable companyTable;
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
	private HashSet<VendorPerson> tbd;
	JPanel mainPanel = new JPanel();
	private final JButton btnAdd = new JButton("Add");
	private final JButton btnDel = new JButton("Delete");

	public DistributorEntryForm() {
		createUI();
		tbd = new HashSet<VendorPerson>();
		populateComboBoxes();
		setFieldsEnable(false);
	}

	private void populateComboBoxes() {
		List<Person> pList = PersonDAO.getInstance().findAll();
		this.cbPerson.setModel(new DefaultComboBoxModel(pList.toArray(new Person[0])));
		this.cbPerson.setSelectedIndex(-1);
	}

	public void createNew() {
		InventoryVendor inV = new InventoryVendor();
		inV.setExpenseTypeVendor(false);
		setBean(inV);
		clearTableModel();
	}

	private void createUI() {
		tabbedPane = new JTabbedPane();
		tabDistributor = new JPanel();
		tabCompanies = new JPanel();
		tabDistributor.setPreferredSize(new Dimension(500, 400));
		tabCompanies.setPreferredSize(new Dimension(500, 400));
		tabbedPane.setPreferredSize(new Dimension(500, 400));

		tabbedPane.addTab(com.floreantpos.POSConstants.GENERAL, tabDistributor);
		tabbedPane.addTab("Companies", tabCompanies);
		tabDistributor.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));
		setLayout(new BorderLayout());

		tabDistributor.add(nameLabel = new JLabel("Name"), "cell 0 2,alignx trailing");
		this.tfName = new POSTextField();
		tabDistributor.add(this.tfName, "grow, wrap");

		tabDistributor.add(phoneLabel = new JLabel("Phone"), "cell 0 5,alignx trailing");
		this.tfPhone = new TextField(20);
		tabDistributor.add(this.tfPhone, "grow, wrap");

		tabDistributor.add(emailLabel = new JLabel("Email"), "cell 0 8,alignx trailing");
		this.tfEmail = new TextField(40);
		tabDistributor.add(this.tfEmail, "grow, wrap");

		tabDistributor.add(addLabel = new JLabel("Address"), "cell 0 11,alignx trailing");
		this.taAddress = new JTextArea(4, 10);
		this.taAddress.setTabSize(4);
		JScrollPane scrollPane = new JScrollPane(this.taAddress);
		tabDistributor.add(scrollPane, "cell 1 11");

		tabDistributor.add(persLabel = new JLabel("People"), "cell 0 15,alignx trailing");
		tabDistributor.add(this.cbPerson, "cell 1 15,alignx");
		this.btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DistributorEntryForm.this.addNewPerson();
			}
		});
		tabDistributor.add(this.btnAdd, "cell 2 15");

		this.table = new JTable(new DistributorDetailModel());
		DistributorDetailModel tableModel = (DistributorDetailModel) this.table.getModel();
		tableModel.setPageSize(30);
		JScrollPane jsp = new JScrollPane(this.table);
		jsp.setPreferredSize(new Dimension(500, 200));
		tabDistributor.add(jsp, "cell 1 20");

		this.btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DistributorEntryForm.this.deleteSelectedPerson();
			}
		});
		tabDistributor.add(btnDel, "cell 2 33");

		tabCompanies.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));
		this.companyTable = new JTable(new CompanyTableModel());
		this.companyTable.setPreferredSize(new Dimension(300, 400));
		jScrollPane = new JScrollPane(this.companyTable);

		jScrollPane.setPreferredSize(new Dimension(500, 600));
		CompanyTableModel compTableModel = (CompanyTableModel) this.companyTable.getModel();
		compTableModel.setPageSize(100);
		tabCompanies.add(jScrollPane, "cell 1 30 3 3");
		add(tabbedPane);
	}

	protected void deleteSelectedPerson() {
		if (this.table.getSelectedRow() >= 0) {
			DistributorDetailModel tableModel = (DistributorDetailModel) this.table.getModel();
			tbd.add(tableModel.getRowData(this.table.getSelectedRow()));
			tableModel.deleteItem(this.table.getSelectedRow());
		}
	}

	protected void addNewPerson() {
		VendorPerson vp = new VendorPerson();
		vp.setVendor(getBean());
		boolean allSet = true;
		if (this.cbPerson.getSelectedItem() != null) {
			vp.setPerson((Person) this.cbPerson.getSelectedItem());
		} else {
			allSet = false;
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Person Name can not be empty!");
		}
		if (allSet) {
			DistributorDetailModel tableModel = (DistributorDetailModel) this.table.getModel();
			boolean flag = false;
			if (tableModel.getRows() != null) {
				for (VendorPerson vp1 : tableModel.getRows()) {
					if (vp1.equals(vp)) {
						flag = true;
						break;
					}
				}
			}
			if (!flag) {
				if (tbd.contains(vp)) {
					tbd.remove(vp);
				}
				tableModel.addItem(vp);
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
		clearTableModel();
		clearCompanyTableModel();
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
		this.companyTable.setEnabled(enable);
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
		InventoryVendor vend = (InventoryVendor) getBean();
		if (vend == null) {
			return;
		}
		this.tfName.setText(vend.getName());
		this.tfPhone.setText(vend.getPhone());
		this.tfEmail.setText(vend.getEmail());
		this.taAddress.setText(vend.getAddress());
		this.cbPerson.setSelectedIndex(-1);
		tbd.clear();
		loadTableData();
		CompanyTableModel compTableModel = (CompanyTableModel) this.companyTable.getModel();
		if (tabCompanies != null && vend.getId() != null) {
			compTableModel.setRows(getCompany(vend));
		}
	}

	private List<Company> getCompany(InventoryVendor vend) {
		List<ItemCompVendPack> icvps = ItemCompVendPackDAO.getInstance().findAllByVendor(vend);
		Set<Company> companies = new HashSet<Company>();
		if (icvps != null) {
			for (ItemCompVendPack i : icvps) {
				companies.add(i.getCompany());
			}
		}
		if (companies == null) {
			return null;
		} else {
			List<Company> compList = new ArrayList<Company>(companies);
			return compList;
		}
	}

	public boolean updateModel() {
		InventoryVendor vend = (InventoryVendor) getBean();
		if (vend == null) {
			createNew();
		}
		if (StringUtils.isEmpty(this.tfName.getText())) {
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Vendor Name is empty");
			return false;
		}
		vend.setName(this.tfName.getText());
		vend.setPhone(this.tfPhone.getText());
		vend.setEmail(this.tfEmail.getText());
		vend.setAddress(this.taAddress.getText());
		return true;
	}

	public boolean delete() {
		InventoryVendor inventoryVendor = (InventoryVendor) getBean();
		if (inventoryVendor == null) {
			return false;
		}
		InventoryVendorDAO.getInstance().delete(inventoryVendor);
		return true;
	}

	public boolean save() {
		Session session = InventoryVendorDAO.getInstance().createNewSession();
		if (session != null) {
			Transaction tx = session.beginTransaction();
			boolean actionPerformed = false;
			try {
				if (updateModel()) {
					InventoryVendor vend = (InventoryVendor) getBean();
					if (vend.getName() == null) {
						actionPerformed = false;
						POSMessageDialog.showError(BackOfficeWindow.getInstance(), "Please add a valid Vendor name!!");
					} else {
						InventoryVendorDAO dao = InventoryVendorDAO.getInstance();
						dao.saveOrUpdate(vend);
						actionPerformed = true;
						VendorPersonDAO vpDao = VendorPersonDAO.getInstance();
						DistributorDetailModel tableModel = (DistributorDetailModel) this.table.getModel();
						if (tableModel.getRows() != null) {
							for (VendorPerson ic : tableModel.getRows()) {
								vpDao.saveOrUpdate(ic);
							}
						}
						for (VendorPerson ic : tbd) {
							if (ic.getId() != null) {
								vpDao.refresh(ic);
								vpDao.delete(ic);
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
		InventoryVendor inventoryVendor = (InventoryVendor) getBean();
		if ((inventoryVendor == null) || (inventoryVendor.getId() == null)) {
			return "Add new Vendor";
		}
		return "Edit Vendor";
	}

	public void loadTableData() {
		InventoryVendor comp = (InventoryVendor) getBean();
		DistributorDetailModel tableModel = (DistributorDetailModel) this.table.getModel();
		if (comp.getId() != null) {
			List<VendorPerson> tuple = VendorPersonDAO.getInstance().findAllByVendor(comp);
			tableModel.setRows(tuple);
		}
	}

	public void clearTableModel() {
		if (this.table != null && this.table.getModel() != null) {
			DistributorDetailModel tableModel = (DistributorDetailModel) this.table.getModel();
			tableModel.setRows(null);
		}
	}

	public void clearCompanyTableModel() {
		if (this.companyTable != null && this.companyTable.getModel() != null) {
			CompanyTableModel tableModel = (CompanyTableModel) this.companyTable.getModel();
			tableModel.setRows(null);
		}
	}

	static class DistributorDetailModel extends ListTableModel<VendorPerson> {

		private static final long serialVersionUID = 7259163496906110603L;

		public DistributorDetailModel() {
			super(new String[] { "Person", "Designation", "Phone" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			VendorPerson row = (VendorPerson) getRowData(rowIndex);
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

	static class CompanyTableModel extends ListTableModel<Company> {

		private static final long serialVersionUID = 2219488651760125616L;

		public CompanyTableModel() {
			super(new String[] { "Company" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Company row = (Company) getRowData(rowIndex);
			Session session = CompanyDAO.getInstance().createNewSession();
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
