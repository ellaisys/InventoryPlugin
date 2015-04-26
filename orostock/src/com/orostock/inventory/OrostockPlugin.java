package com.orostock.inventory;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import com.floreantpos.extension.InventoryPlugin;
import com.floreantpos.model.MenuItem;
import com.orostock.inventory.action.CompanyBrowserAction;
import com.orostock.inventory.action.InventoryItemBrowserAction;
import com.orostock.inventory.action.InventoryVendorBrowserAction;
import com.orostock.inventory.action.PackagingUnitBrowserAction;
import com.orostock.inventory.action.PersonBrowserAction;
import com.orostock.inventory.ui.ExpenseTransactionBrowserAction;
import com.orostock.inventory.ui.InventoryTransactionBrowserAction;
import com.orostock.inventory.ui.recepie.RecepieView;

@PluginImplementation
public class OrostockPlugin implements InventoryPlugin {
	public AbstractAction[] getActions() {
		return new AbstractAction[] { 
				new InventoryItemBrowserAction(), 
				new InventoryVendorBrowserAction(), 
				new PackagingUnitBrowserAction(), 
				new ExpenseTransactionBrowserAction(),
				new InventoryTransactionBrowserAction(), 
				new PersonBrowserAction(),
				new CompanyBrowserAction()};
	}

	public void addRecepieView(JTabbedPane tabbedPane, MenuItem m) {
		tabbedPane.addTab("Recipe", new RecepieView(m));
	}
}

