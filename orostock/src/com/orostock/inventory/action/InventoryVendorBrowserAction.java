 package com.orostock.inventory.action;
 
 import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.InventoryVendorBrowser;
 
 public class InventoryVendorBrowserAction extends AbstractAction
 {
   /**
	 * 
	 */
	private static final long serialVersionUID = 8351943608003054308L;

public InventoryVendorBrowserAction()
   {
     super("Vendor Browser");
   }
 
   public void actionPerformed(ActionEvent e)
   {
     BackOfficeWindow window = BackOfficeWindow.getInstance();
     JTabbedPane tabbedPane = window.getTabbedPane();
 
     InventoryVendorBrowser browser = null;
 
     int index = tabbedPane.indexOfTab("Vendor Browser");
     if (index == -1) {
       browser = new InventoryVendorBrowser();
       tabbedPane.addTab("Vendor Browser", browser);
       browser.loadData();
     }
     else {
       browser = (InventoryVendorBrowser)tabbedPane.getComponentAt(index);
     }
 
     tabbedPane.setSelectedComponent(browser);
   }
 }
