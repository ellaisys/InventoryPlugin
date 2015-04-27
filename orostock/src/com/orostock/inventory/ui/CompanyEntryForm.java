package com.orostock.inventory.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import com.floreantpos.model.Person;
import com.floreantpos.model.dao.CompanyDAO;
import com.floreantpos.model.dao.CompanyPersonDAO;
import com.floreantpos.model.dao.PersonDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class CompanyEntryForm extends BeanEditor<Company> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4705036150381956350L;
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

	private void populateComboBoxes() {
		List<Person> pList = PersonDAO.getInstance().findAll();
		this.cbPerson.setModel(new DefaultComboBoxModel(pList.toArray(new Person[0])));
	}

	public void createNew() {
		Company comp = new Company();
		setBean(comp);
	}

	private void createUI() {
		setLayout(new BorderLayout());
		add(this.mainPanel);

		this.mainPanel.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));

		mainPanel.add(nameLabel = new JLabel("Name"));
		this.tfName = new POSTextField();
		mainPanel.add(this.tfName, "grow, wrap");

		mainPanel.add(phoneLabel = new JLabel("Phone"));
		this.tfPhone = new TextField(20);
		mainPanel.add(this.tfPhone, "grow, wrap");

		mainPanel.add(emailLabel = new JLabel("Email"));
		this.tfEmail = new TextField(40);
		mainPanel.add(this.tfEmail, "grow, wrap");

		mainPanel.add(addLabel = new JLabel("Address"));
		this.taAddress = new JTextArea();
		mainPanel.add(new JScrollPane(this.taAddress), "grow, h 100px, wrap");

		JPanel hPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel hPanel1 = new JPanel(new FlowLayout(FlowLayout.LEADING));

		this.cbPerson.setPreferredSize(new Dimension(110, 20));

		persLabel = new JLabel("Person");
		persLabel.setPreferredSize(new Dimension(120, 20));

		// hPanel.add(optionLabel = new JLabel("People :  "),
		// "cell 0 3,alignx trailing");
		hPanel.add(persLabel, "cell 1 3,alignx trailing");

		JLabel j4 = new JLabel("Item Options");
		hPanel1.add(j4, "cell 0 3,alignx trailing");
		j4.setForeground(getBackground());
		hPanel1.add(this.cbPerson, "cell 1 3,alignx");
		this.btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CompanyEntryForm.this.addNewPerson();
			}
		});
		hPanel1.add(this.btnAdd, "cell 4 3,alignx");
		hPanel.setPreferredSize(new Dimension(300, 40));
		hPanel1.setPreferredSize(new Dimension(300, 40));

		this.mainPanel.add(hPanel, "cell 0 15 3 3");
		this.mainPanel.add(hPanel1, "cell 0 18 3 3");

		this.btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CompanyEntryForm.this.deleteSelectedPerson();
			}
		});
		this.table = new JTable(new CompanyDetailModel());
		CompanyDetailModel tableModel = (CompanyDetailModel) this.table.getModel();
		tableModel.setPageSize(70);

		JScrollPane jsp = new JScrollPane(this.table);
		jsp.setPreferredSize(new Dimension(500, 200));
		this.mainPanel.add(jsp, "cell 1 30 3 3");
		this.mainPanel.add(btnDel, "cell 1 35 2 1");

		setFieldsEnable(false);
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
		this.persLabel.setText("");
		this.cbPerson.setSelectedIndex(-1);
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
	}

	public void updateView() {
		Company model = (Company) getBean();
		if (model == null) {
			return;
		}
		this.tfName.setText(model.getName());
		this.tfPhone.setText(model.getPhone());
		this.tfEmail.setText(model.getEmail());
		this.taAddress.setText(model.getAddress());
		loadTableData();
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

	static class CompanyDetailModel extends ListTableModel<CompanyPerson> {
		private static final long serialVersionUID = -5532926699493030221L;

		public CompanyDetailModel() {
			super(new String[] { "Person" });
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			CompanyPerson row = (CompanyPerson) getRowData(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getPerson().getName();
			}
			return null;
		}

	}

	public void setFieldsEnableEdit() {
		this.tfName.setEnabled(true);
		this.tfPhone.setEnabled(true);
		this.tfEmail.setEnabled(true);
		this.taAddress.setEnabled(true);
		this.cbPerson.setEnabled(true);
		this.table.setEnabled(true);
	}

}
