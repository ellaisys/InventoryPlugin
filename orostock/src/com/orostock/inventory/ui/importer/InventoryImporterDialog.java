 package com.orostock.inventory.ui.importer;
 
 import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.Command;
import com.floreantpos.model.InventoryGroup;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.InventoryGroupDAO;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.InventoryLocationDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.swing.FocusedTextField;
import com.floreantpos.ui.TitlePanel;
 
 public class InventoryImporterDialog extends JDialog
   implements ActionListener
 {
   private JXTable inventoryTable = new JXTable() {
     public boolean isCellEditable(int row, int column) {
       return true;
     } } ;
   private InventoryImporterTableModel tableModel;
 
   public InventoryImporterDialog(List<InventoryItem> inventoryItems) {
     super(BackOfficeWindow.getInstance(), true);
 
     setTitle("Inventory item importer");
 
     TitlePanel titlePanel = new TitlePanel();
     titlePanel.setTitle("Inventory item import preview");
     add(titlePanel, "North");
 
     this.inventoryTable.setModel(this.tableModel = new InventoryImporterTableModel(inventoryItems));
     this.inventoryTable.setDefaultEditor(String.class, new DefaultCellEditor(new FocusedTextField()));
 
     for (int i = this.inventoryTable.getColumnCount() - 1; i > 7; i--) {
       TableColumnExt columnExt = this.inventoryTable.getColumnExt(i);
       columnExt.setVisible(false);
     }
 
     JPanel tablePanel = new JPanel(new BorderLayout());
     tablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
     tablePanel.add(new JScrollPane(this.inventoryTable));
 
     add(tablePanel);
 
     JPanel buttonPanel = new JPanel();
     JButton btnSave = new JButton("Save to Floreant POS Inventory");
     btnSave.setActionCommand(Command.SAVE.name());
     btnSave.addActionListener(this);
 
     JButton btnClose = new JButton("Close");
     btnClose.setActionCommand(Command.CANCEL.name());
     btnClose.addActionListener(this);
 
     buttonPanel.add(btnSave);
     buttonPanel.add(btnClose);
 
     add(buttonPanel, "South");
 
     this.inventoryTable.setColumnControlVisible(true);
 
     setResizable(true);
     setDefaultCloseOperation(2);
   }
 
   public void loadData() {
     JXComboBox cbGroup = new JXComboBox();
     JXComboBox cbLocation = new JXComboBox();
     JXComboBox cbVendor = new JXComboBox();
 
     cbGroup.setModel(new DefaultComboBoxModel(InventoryGroupDAO.getInstance().findAll().toArray(new InventoryGroup[0])));
     cbLocation.setModel(new DefaultComboBoxModel(InventoryLocationDAO.getInstance().findAll().toArray(new InventoryLocation[0])));
     cbVendor.setModel(new DefaultComboBoxModel(InventoryVendorDAO.getInstance().findAll().toArray(new InventoryVendor[0])));
 
     this.inventoryTable.getColumnExt(2).setCellEditor(new DefaultCellEditor(cbGroup));
     this.inventoryTable.getColumnExt(6).setCellEditor(new DefaultCellEditor(cbVendor));
     this.inventoryTable.getColumnExt(7).setCellEditor(new DefaultCellEditor(cbLocation));
   }
 
   public void actionPerformed(ActionEvent e)
   {
     Command command = Command.fromString(e.getActionCommand());
 
     switch (command.ordinal()) {
     case 1:
       List<InventoryItem> rows = this.tableModel.getRows();
 
       InventoryItemDAO dao = InventoryItemDAO.getInstance();
       Session session = dao.createNewSession();
       Transaction transaction = session.beginTransaction();
 
       for (InventoryItem inventoryItem : rows) {
         try
         {
           if (!dao.hasInventoryItemByName(inventoryItem.getName())) {
             dao.saveOrUpdate(inventoryItem, session);
           }
           else {
             inventoryItem.setName(inventoryItem.getName() + RandomUtils.nextInt());
             dao.saveOrUpdate(inventoryItem, session);
           }
         } catch (Exception e2) {
           e2.printStackTrace();
         }
       }
 
       transaction.commit();
       session.close();
 
       JOptionPane.showMessageDialog(this, "Saved");
       break;
     case 2:
       dispose();
       break;
     }
   }
 }

/* Location:           C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\plugins\orostock-0.1.jar
 * Qualified Name:     com.orostock.inventory.ui.importer.InventoryImporterDialog
 * JD-Core Version:    0.6.0
 */