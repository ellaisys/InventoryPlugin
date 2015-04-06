 package com.orostock.inventory.ui;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.model.InventoryMetaCode;
import com.floreantpos.model.dao.InventoryMetaCodeDAO;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSBackofficeDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
 
 public class InventoryMetacodeEntryDialog extends POSBackofficeDialog
 {
   private InventoryMetaCode model;
   private TitlePanel titlePanel;
   private JPanel panel_1;
   private JButton psbtnOk;
   private JButton psbtnCancel;
   private JLabel lblType;
   private JLabel lblName;
   private JLabel lblNo;
   private JLabel lblDescription;
   private POSTextField tfType;
   private POSTextField tfName;
   private IntegerTextField tfNo;
   private POSTextField tfDescription;
 
   public InventoryMetacodeEntryDialog(InventoryMetaCode ig)
   {
     this.model = ig;
 
     createUI();
 
     setDefaultCloseOperation(2);
     updateView();
   }
 
   private void createUI() {
     this.titlePanel = new TitlePanel();
     getContentPane().add(this.titlePanel, "North");
 
     JPanel panel = new JPanel();
     getContentPane().add(panel, "Center");
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
 
     this.panel_1 = new JPanel();
     getContentPane().add(this.panel_1, "South");
 
     this.psbtnOk = new JButton();
     this.psbtnOk.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         InventoryMetacodeEntryDialog.this.saveAndDispose();
       }
     });
     this.psbtnOk.setText("OK");
     this.panel_1.add(this.psbtnOk);
 
     this.psbtnCancel = new JButton();
     this.psbtnCancel.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         InventoryMetacodeEntryDialog.this.setCanceled(true);
         InventoryMetacodeEntryDialog.this.dispose();
       }
     });
     this.psbtnCancel.setText("CANCEL");
     this.panel_1.add(this.psbtnCancel);
   }
 
   protected void saveAndDispose() {
     try {
       updateModel();
 
       InventoryMetaCodeDAO.getInstance().saveOrUpdate(this.model);
       setCanceled(false);
       dispose();
     }
     catch (Exception e) {
       POSMessageDialog.showError(e.getMessage());
     }
   }
 
   private void updateView() {
     if (this.model == null) {
       return;
     }
 
     this.tfType.setText(this.model.getType());
     this.tfName.setText(this.model.getCodeText());
     this.tfNo.setText("" + this.model.getCodeNo());
     this.tfDescription.setText(this.model.getDescription());
   }
 
   private boolean updateModel() {
     if (this.model == null) {
       this.model = new InventoryMetaCode();
     }
 
     this.model.setType(this.tfType.getText());
     this.model.setCodeText(this.tfName.getText());
     this.model.setCodeNo(Integer.valueOf(this.tfNo.getInteger()));
     this.model.setDescription(this.tfDescription.getText());
 
     return true;
   }
 
   public void setTitle(String title) {
     this.titlePanel.setTitle(title);
   }
 
   public InventoryMetaCode getModel() {
     return this.model;
   }
 }

/* Location:           C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\plugins\orostock-0.1.jar
 * Qualified Name:     com.orostock.inventory.ui.InventoryMetacodeEntryDialog
 * JD-Core Version:    0.6.0
 */