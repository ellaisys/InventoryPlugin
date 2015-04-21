/*    */package com.orostock.inventory.action;

/*    */
/*    *//*    */import java.awt.event.ActionEvent;

/*    */
import javax.swing.AbstractAction;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.InventoryGroup;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.orostock.inventory.ui.InventoryGroupEntryForm;

public class InventoryGroupEntryAction extends AbstractAction {
	public InventoryGroupEntryAction() {
		super("New Inventory Group");
	}

	public void actionPerformed(ActionEvent e) {
		InventoryGroupEntryForm form = new InventoryGroupEntryForm(new InventoryGroup());
		BeanEditorDialog dialog = new BeanEditorDialog(form, BackOfficeWindow.getInstance(), true);
		dialog.pack();
		dialog.open();
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.action.InventoryGroupEntryAction JD-Core Version:
 * 0.6.0
 */