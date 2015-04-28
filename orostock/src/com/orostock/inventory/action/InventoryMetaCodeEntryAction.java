 package com.orostock.inventory.action;
 
 import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InventoryMetaCode;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.form.InventoryMetacodeEntryForm;
 
 public class InventoryMetaCodeEntryAction extends AbstractAction
 {
   public InventoryMetaCodeEntryAction()
   {
     super("New Inventory Meta code");
   }
 
   public void actionPerformed(ActionEvent e)
   {
     InventoryMetacodeEntryForm form = new InventoryMetacodeEntryForm(new InventoryMetaCode());
     BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
     dialog.pack();
     dialog.open();
   }
 }

/* Location:           C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\plugins\orostock-0.1.jar
 * Qualified Name:     com.orostock.inventory.action.InventoryMetaCodeEntryAction
 * JD-Core Version:    0.6.0
 */