package com.orostock.inventory.ui;

import java.awt.BorderLayout;
import java.awt.TextField;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXComboBox;

import com.floreantpos.PosException;
import com.floreantpos.model.Company;
import com.floreantpos.model.Person;
import com.floreantpos.model.dao.CompanyDAO;
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
	private JLabel p1Label;
	private JLabel p2Label;
	private JLabel p3Label;
	private JXComboBox cbP1 = new JXComboBox();
	private JXComboBox cbP2 = new JXComboBox();
	private JXComboBox cbP3 = new JXComboBox();

	JPanel mainPanel = new JPanel();

	public CompanyEntryForm() {
		setLayout(new BorderLayout());
		createUI();
		populateComboBoxes();
	}

	private void populateComboBoxes() {
		List<Person> pList = PersonDAO.getInstance().findAll();
		this.cbP1.setModel(new DefaultComboBoxModel(pList.toArray(new Person[0])));
		this.cbP2.setModel(new DefaultComboBoxModel(pList.toArray(new Person[0])));
		this.cbP3.setModel(new DefaultComboBoxModel(pList.toArray(new Person[0])));
	}

	private void createUI() {
		setLayout(new BorderLayout());
		add(this.mainPanel);

		this.mainPanel.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));

		mainPanel.add(new JLabel("Name"));
		this.tfName = new POSTextField();
		mainPanel.add(this.tfName, "grow, wrap");

		mainPanel.add(new JLabel("Phone"));
		this.tfPhone = new TextField(20);
		mainPanel.add(this.tfPhone, "grow, wrap");

		mainPanel.add(new JLabel("Email"));
		this.tfEmail = new TextField(40);
		mainPanel.add(this.tfEmail, "grow, wrap");

		mainPanel.add(new JLabel("Address"));
		this.taAddress = new JTextArea();
		mainPanel.add(new JScrollPane(this.taAddress), "grow, h 100px, wrap");

		this.mainPanel.add(p1Label = new JLabel("Person 1"), "cell 0 13,alignx trailing");
		this.mainPanel.add(this.cbP1, "cell 1 13");

		this.mainPanel.add(p2Label = new JLabel("Person 2"), "cell 0 13,alignx trailing");
		this.mainPanel.add(this.cbP2, "cell 1 13");

		this.mainPanel.add(p3Label = new JLabel("Person 3"), "cell 0 13,alignx trailing");
		this.mainPanel.add(this.cbP3, "cell 1 13");

		setFieldsEnable(false);
	}

	public void createNew() {
		setBean(new Company());
	}

	public void clearFields() {
		this.tfName.setText("");
		this.tfPhone.setText("");
		this.tfEmail.setText("");
		this.taAddress.setText("");
		this.p1Label.setText("");
		this.p2Label.setText("");
		this.p3Label.setText("");
		this.cbP1.setSelectedItem(null);
		this.cbP2.setSelectedItem(null);
		this.cbP3.setSelectedItem(null);
	}

	public void setFieldsEnable(boolean enable) {
		this.tfName.setEnabled(enable);
		this.tfPhone.setEnabled(enable);
		this.tfEmail.setEnabled(enable);
		this.taAddress.setEnabled(enable);
		this.p1Label.setEnabled(enable);
		this.p2Label.setEnabled(enable);
		this.p3Label.setEnabled(enable);
		this.cbP1.setEnabled(enable);
		this.cbP2.setEnabled(enable);
		this.cbP3.setEnabled(enable);
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
		this.cbP1.setSelectedItem(model.getPersonByP1Id());
		this.cbP2.setSelectedItem(model.getPersonByP2Id());
		this.cbP3.setSelectedItem(model.getPersonByP3Id());
	}

	public boolean updateModel() {
		Company model = (Company) getBean();
		if (model == null) {
			model = new Company();
			setBean(model);
		}
		String nameString = this.tfName.getText();
		if (StringUtils.isEmpty(nameString)) {
			throw new PosException("Name cannot be empty");
		}
		model.setName(nameString);
		model.setPhone(this.tfPhone.getText());
		model.setEmail(this.tfEmail.getText());
		model.setAddress(this.taAddress.getText());
		if (this.cbP1.getSelectedItem() != null) {
			model.setPersonByP1Id((Person) this.cbP1.getSelectedItem());
		} else {
			model.setPersonByP1Id(null);
		}
		if (this.cbP2.getSelectedItem() != null) {
			model.setPersonByP2Id((Person) this.cbP2.getSelectedItem());
		} else {
			model.setPersonByP2Id(null);
		}
		if (this.cbP3.getSelectedItem() != null) {
			model.setPersonByP3Id((Person) this.cbP3.getSelectedItem());
		} else {
			model.setPersonByP3Id(null);
		}
		return true;
	}

	public boolean delete() {
		Company company = (Company) getBean();
		if (company == null) {
			return false;
		}
		CompanyDAO.getInstance().delete(company);
		return true;
	}

	public String getDisplayText() {
		Company company = (Company) getBean();
		if ((company == null) || (company.getId() == null)) {
			return "Add new Company";
		}
		return "Edit Company";
	}

	public boolean save() {
		try {
			if (!updateModel()) {
				return false;
			}
			CompanyDAO dao = CompanyDAO.getInstance();
			dao.saveOrUpdate((Company) getBean());
			return true;
		} catch (Exception e) {
			POSMessageDialog.showError(e.getMessage(), e);
		}
		return false;
	}

	public void setFieldsEnableEdit() {

	}

}
