package com.orostock.inventory.ui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.PosException;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;

public class InventoryVendorEntryForm extends BeanEditor<InventoryVendor>
{
  private JCheckBox chkVisible;
  private POSTextField tfName;

  public InventoryVendorEntryForm(InventoryVendor ig)
  {
    setLayout(new BorderLayout());

    createUI();

    setBean(ig);
  }

  private void createUI()
  {
    JPanel panel = new JPanel();
    add(panel, "Center");
    panel.setLayout(new MigLayout("", "[][grow]", "[][]"));

    JLabel lblName = new JLabel("Name");
    panel.add(lblName, "cell 0 0,alignx trailing");

    this.tfName = new POSTextField();
    panel.add(this.tfName, "cell 1 0,growx");

    this.chkVisible = new JCheckBox("Visible", true);
    panel.add(this.chkVisible, "cell 1 1");
  }

  public void updateView()
  {
    InventoryVendor model = (InventoryVendor)getBean();

    if (model == null) {
      return;
    }

    this.tfName.setText(model.getName());

    if (model.getId() != null)
      this.chkVisible.setSelected(POSUtil.getBoolean(model.isVisible()));
  }

  public boolean updateModel() {
    InventoryVendor model = (InventoryVendor)getBean();

    if (model == null) {
      model = new InventoryVendor();
    }

    String nameString = this.tfName.getText();
    if (StringUtils.isEmpty(nameString)) {
      throw new PosException("Name cannot be empty");
    }

    model.setName(nameString);
    model.setVisible(Boolean.valueOf(this.chkVisible.isSelected()));

    return true;
  }

  public String getDisplayText()
  {
    return "Add inventory Entry";
  }

  public boolean save()
  {
    try {
      if (!updateModel()) {
        return false;
      }

      InventoryVendor model = (InventoryVendor)getBean();
      InventoryVendorDAO.getInstance().saveOrUpdate(model);

      return true;
    } catch (Exception e) {
      POSMessageDialog.showError(e.getMessage());
    }

    return false;
  }
}

/* Location:           C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\plugins\orostock-0.1.jar
 * Qualified Name:     com.orostock.inventory.ui.InventoryVendorEntryForm
 * JD-Core Version:    0.6.0
 */