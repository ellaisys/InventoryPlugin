 package com.orostock.inventory.ui.form;
 
 import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.model.InventoryMetaCode;
import com.floreantpos.model.dao.InventoryMetaCodeDAO;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
 
 public class InventoryMetacodeEntryForm extends BeanEditor<InventoryMetaCode>
 {
   private JLabel lblType;
   private JLabel lblName;
   private JLabel lblNo;
   private JLabel lblDescription;
   private POSTextField tfType;
   private POSTextField tfName;
   private IntegerTextField tfNo;
   private POSTextField tfDescription;
 
   public InventoryMetacodeEntryForm(InventoryMetaCode ig)
   {
     setLayout(new BorderLayout());
 
     createUI();
 
     setBean(ig);
   }
 
   private void createUI()
   {
     JPanel panel = new JPanel();
     add(panel, "Center");
     panel.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
 
     this.lblType = new JLabel("Type");
     panel.add(this.lblType, "cell 0 0,alignx trailing");
 
     this.tfType = new POSTextField();
     panel.add(this.tfType, "cell 1 0,growx");
 
     this.lblName = new JLabel("Name");
     panel.add(this.lblName, "cell 0 1,alignx trailing");
 
     this.tfName = new POSTextField();
     panel.add(this.tfName, "cell 1 1,growx");
 
     this.lblNo = new JLabel("No");
     panel.add(this.lblNo, "cell 0 2,alignx trailing");
 
     this.tfNo = new IntegerTextField();
     panel.add(this.tfNo, "cell 1 2,growx");
 
     this.lblDescription = new JLabel("Description");
     panel.add(this.lblDescription, "cell 0 3,alignx trailing");
 
     this.tfDescription = new POSTextField();
     panel.add(this.tfDescription, "cell 1 3,growx");
   }
 
   public void updateView()
   {
     InventoryMetaCode model = (InventoryMetaCode)getBean();
 
     if (model == null) {
       return;
     }
 
     this.tfType.setText(model.getType());
     this.tfName.setText(model.getCodeText());
     this.tfNo.setText("" + model.getCodeNo());
     this.tfDescription.setText(model.getDescription());
   }
 
   public boolean updateModel() {
     InventoryMetaCode model = (InventoryMetaCode)getBean();
 
     if (model == null) {
       model = new InventoryMetaCode();
     }
 
     model.setType(this.tfType.getText());
     model.setCodeText(this.tfName.getText());
     model.setCodeNo(Integer.valueOf(this.tfNo.getInteger()));
     model.setDescription(this.tfDescription.getText());
 
     return true;
   }
 
   public String getDisplayText()
   {
     return "Add inventory MetaData";
   }
 
   public boolean save()
   {
     try {
       if (!updateModel()) {
         return false;
       }
 
       InventoryMetaCode model = (InventoryMetaCode)getBean();
       InventoryMetaCodeDAO.getInstance().saveOrUpdate(model);
 
       return true;
     } catch (Exception e) {
       POSMessageDialog.showError(e.getMessage());
     }
 
     return false;
   }
 }

/* Location:           C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\plugins\orostock-0.1.jar
 * Qualified Name:     com.orostock.inventory.ui.InventoryMetacodeEntryForm
 * JD-Core Version:    0.6.0
 */