package com.orostock.inventory.ui;

import java.awt.BorderLayout;
import java.awt.TextField;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.PosException;
import com.floreantpos.model.Person;
import com.floreantpos.model.dao.PersonDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class PersonEntryForm extends BeanEditor<Person> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4705036150381956350L;
	private POSTextField tfName;
	private TextField tfPhone;
	private TextField tfDesignation;
	private TextField tfEmail;
	private JTextArea taAddress;
	JPanel mainPanel = new JPanel();

	public PersonEntryForm() {
		setLayout(new BorderLayout());
		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout());
		add(this.mainPanel);

		this.mainPanel.setLayout(new MigLayout("fillx", "[][grow,fill][grow,fill][]", "[][][][][][][][][][][][][][][][][]"));

		mainPanel.add(new JLabel("Name"));
		this.tfName = new POSTextField();
		mainPanel.add(this.tfName, "grow, wrap");

		mainPanel.add(new JLabel("Designation"));
		this.tfDesignation = new TextField();
		mainPanel.add(this.tfDesignation, "grow, wrap");

		mainPanel.add(new JLabel("Phone"));
		this.tfPhone = new TextField(20);
		mainPanel.add(this.tfPhone, "grow, wrap");

		mainPanel.add(new JLabel("Email"));
		this.tfEmail = new TextField(40);
		mainPanel.add(this.tfEmail, "grow, wrap");

		mainPanel.add(new JLabel("Address"));
		this.taAddress = new JTextArea();
		mainPanel.add(new JScrollPane(this.taAddress), "grow, h 100px, wrap");
		setFieldsEnable(false);
	}

	public void createNew() {
		setBean(new Person());
	}

	public void clearFields() {
		this.tfName.setText("");
		this.tfDesignation.setText("");
		this.tfPhone.setText("");
		this.tfEmail.setText("");
		this.taAddress.setText("");
	}

	public void setFieldsEnable(boolean enable) {
		this.tfName.setEnabled(enable);
		this.tfPhone.setEnabled(enable);
		this.tfDesignation.setEnabled(enable);
		this.tfEmail.setEnabled(enable);
		this.taAddress.setEnabled(enable);
	}

	public void updateView() {
		Person model = (Person) getBean();
		if (model == null) {
			return;
		}
		this.tfName.setText(model.getName());
		this.tfPhone.setText(model.getPhone());
		this.tfDesignation.setText(model.getDesignation());
		this.tfEmail.setText(model.getEmail());
		this.taAddress.setText(model.getAddress());
	}

	public boolean updateModel() {
		Person model = (Person) getBean();
		if (model == null) {
			model = new Person();
			setBean(model);
		}
		String nameString = this.tfName.getText();
		if (StringUtils.isEmpty(nameString)) {
			throw new PosException("Name cannot be empty");
		}
		model.setName(nameString);
		model.setDesignation(this.tfDesignation.getText());
		model.setPhone(this.tfPhone.getText());
		model.setEmail(this.tfEmail.getText());
		model.setAddress(this.taAddress.getText());
		return true;
	}

	public boolean delete() {
		Person person = (Person) getBean();
		if (person == null) {
			return false;
		}
		PersonDAO.getInstance().delete(person);
		return true;
	}

	public String getDisplayText() {
		Person person = (Person) getBean();
		if ((person == null) || (person.getId() == null)) {
			return "Add new Person";
		}
		return "Edit Person";
	}

	public boolean save() {
		try {
			if (!updateModel()) {
				return false;
			}
			PersonDAO dao = PersonDAO.getInstance();
			dao.saveOrUpdate((Person) getBean());
			return true;
		} catch (Exception e) {
			POSMessageDialog.showError(e.getMessage(), e);
		}
		return false;
	}

	public void setFieldsEnableEdit() {

	}

}
