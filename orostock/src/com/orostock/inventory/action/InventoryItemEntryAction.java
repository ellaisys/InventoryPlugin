 package com.orostock.inventory.action;
 
 import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.form.InventoryItemEntryForm;
 
 public class InventoryItemEntryAction extends AbstractAction
 {
   /**
	 * 
	 */
	private static final long serialVersionUID = 1025959183208948925L;

public InventoryItemEntryAction()
   {
     super("New Inventory Item");
   }
 
   public void actionPerformed(ActionEvent e)
   {
     InventoryItemEntryForm form = new InventoryItemEntryForm();
     form.setBean(new InventoryItem());
     BeanEditorDialog dialog = new BeanEditorDialog(BackOfficeWindow.getInstance(), true);
     dialog.setBeanEditor(form);
     dialog.pack();
     dialog.setLocationRelativeTo(BackOfficeWindow.getInstance());
     dialog.setVisible(true);
   }
 }

/* Location:           C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\plugins\orostock-0.1.jar
 * Qualified Name:     com.orostock.inventory.action.InventoryItemEntryAction
 * JD-Core Version:    0.6.0
 */