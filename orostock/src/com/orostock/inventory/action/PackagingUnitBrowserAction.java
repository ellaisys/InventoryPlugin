 package com.orostock.inventory.action;
 
 import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.orostock.inventory.ui.PackagingUnitBrowser;
 
 public class PackagingUnitBrowserAction extends AbstractAction
 {
   /**
	 * 
	 */
	private static final long serialVersionUID = 8351943608003054308L;

public PackagingUnitBrowserAction()
   {
     super("Packaging Unit Browser");
   }
 
   public void actionPerformed(ActionEvent e)
   {
     BackOfficeWindow window = BackOfficeWindow.getInstance();
     JTabbedPane tabbedPane = window.getTabbedPane();
 
     PackagingUnitBrowser browser = null;
 
     int index = tabbedPane.indexOfTab("Packaging Unit Browser");
     if (index == -1) {
       browser = new PackagingUnitBrowser();
       tabbedPane.addTab("Packaging Unit Browser", browser);
       browser.loadData();
     }
     else {
       browser = (PackagingUnitBrowser)tabbedPane.getComponentAt(index);
     }
 
     tabbedPane.setSelectedComponent(browser);
   }
 }
