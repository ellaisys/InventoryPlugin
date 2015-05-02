package com.orostock.inventory;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import com.floreantpos.extension.InventoryPlugin;
import com.floreantpos.model.MenuItem;
import com.orostock.inventory.action.CompanyBrowserAction;
import com.orostock.inventory.action.DistributorBrowserAction;
import com.orostock.inventory.action.ExpenseTransactionBrowserAction;
import com.orostock.inventory.action.ExpenseVendorBrowserAction;
import com.orostock.inventory.action.InventoryItemBrowserAction;
import com.orostock.inventory.action.InventoryTransactionBrowserAction;
import com.orostock.inventory.action.PackagingUnitBrowserAction;
import com.orostock.inventory.action.PersonBrowserAction;
import com.orostock.inventory.ui.recepie.RecepieView;

@PluginImplementation
public class OrostockPlugin implements InventoryPlugin {
	public AbstractAction[] getInventoryActions() {
		return new AbstractAction[] { 
				new PackagingUnitBrowserAction(), 
				new InventoryItemBrowserAction(), 
				new InventoryTransactionBrowserAction() };
	}
	
	public AbstractAction[] getExpenseActions() {
		return new AbstractAction[] { 
				new ExpenseVendorBrowserAction(), 
				new ExpenseTransactionBrowserAction()};
	}

	public AbstractAction[] getEntityActions() {
		return new AbstractAction[] { 
				new PersonBrowserAction(), 
				new DistributorBrowserAction(), 
				new CompanyBrowserAction() };
	}

	public void addRecepieView(JTabbedPane tabbedPane, MenuItem m) {
		tabbedPane.addTab("Recipe", new RecepieView(m));
	}
}
