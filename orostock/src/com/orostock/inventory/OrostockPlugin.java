package com.orostock.inventory;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import com.floreantpos.extension.InventoryPlugin;
import com.floreantpos.model.MenuItem;
import com.orostock.inventory.action.InventoryImporterAction;
import com.orostock.inventory.action.InventoryItemBrowserAction;
import com.orostock.inventory.action.InventoryVendorBrowserAction;
import com.orostock.inventory.ui.recepie.RecepieView;

@PluginImplementation
public class OrostockPlugin implements InventoryPlugin {
	public AbstractAction[] getActions() {
		return new AbstractAction[] { new InventoryItemBrowserAction(), new InventoryVendorBrowserAction() };
	}

	// {
	// return new AbstractAction[] { new InventoryImporterAction(), new
	// InventoryItemBrowserAction() };
	// }

	public void addRecepieView(JTabbedPane tabbedPane, MenuItem m) {
		tabbedPane.addTab("Recipe", new RecepieView(m));
	}
}

/*
 * Location:
 * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
 * plugins\orostock-0.1.jar Qualified Name:
 * com.orostock.inventory.OrostockPlugin JD-Core Version: 0.6.0
 */