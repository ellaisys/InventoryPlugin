//package com.orostock.inventory.action;
//
//import java.awt.Dimension;
//import java.awt.Toolkit;
//import java.awt.event.ActionEvent;
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.swing.AbstractAction;
//import javax.swing.JFileChooser;
//import javax.swing.filechooser.FileFilter;
//
//import org.apache.commons.io.IOUtils;
//
//import com.floreantpos.bo.ui.BackOfficeWindow;
//import com.floreantpos.model.InventoryItem;
//import com.floreantpos.ui.dialog.POSMessageDialog;
//import com.orostock.inventory.ui.importer.InventoryImporterDialog;
//
//public class InventoryImporterAction extends AbstractAction {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 9044690083130406786L;
//
//	public InventoryImporterAction() {
//		super("Import Inventory items");
//	}
//
//	public void actionPerformed(ActionEvent ee) {
//		JFileChooser fileChooser = new JFileChooser();
//		fileChooser.setFileFilter(new FileFilter() {
//			public String getDescription() {
//				return "CSV file";
//			}
//
//			public boolean accept(File f) {
//				return f.getName().toLowerCase().endsWith(".csv");
//			}
//		});
//		fileChooser.showOpenDialog(BackOfficeWindow.getInstance());
//		File file = fileChooser.getSelectedFile();
//		if (file == null) {
//			return;
//		}
//
//		FileInputStream inputStream = null;
//		try {
//			inputStream = new FileInputStream(file);
//			List<String> lines = IOUtils.readLines(inputStream);
//			List inventoryItems = new ArrayList();
//
//			boolean firstLine = true;
//			for (String line : lines) {
//				if (firstLine) {
//					firstLine = false;
//					continue;
//				}
//
//				InventoryItem item = InventoryItem.fromCSV(line);
//				inventoryItems.add(item);
//			}
//
//			InventoryImporterDialog dialog = new InventoryImporterDialog(inventoryItems);
//			dialog.loadData();
//
//			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//			dialog.setSize(screenSize);
//			dialog.setVisible(true);
//		} catch (RuntimeException e) {
//			POSMessageDialog.showError(BackOfficeWindow.getInstance(), e.getMessage(), e);
//		} catch (Exception e) {
//			POSMessageDialog.showError(BackOfficeWindow.getInstance(), e.getMessage(), e);
//		} finally {
//			IOUtils.closeQuietly(inputStream);
//		}
//	}
//}
//
///*
// * Location:
// * C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\
// * plugins\orostock-0.1.jar Qualified Name:
// * com.orostock.inventory.action.InventoryImporterAction JD-Core Version: 0.6.0
// */