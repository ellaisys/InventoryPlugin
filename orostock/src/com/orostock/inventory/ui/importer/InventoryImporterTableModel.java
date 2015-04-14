 package com.orostock.inventory.ui.importer;
 
 import java.util.List;

import com.floreantpos.bo.ui.explorer.ListTableModel;
import com.floreantpos.model.InventoryItem;
 
 public class InventoryImporterTableModel extends ListTableModel<InventoryItem>
 {
   public static String[] columnNames = { "NAME", "UNIT_PER_PACKAGE", "TOTAL_PACKAGES", "AVERAGE_PACKAGE_PRICE", "TOTAL_RECEPIE_UNITS", "UNIT_PURCHASE_PRICE", "PACKAGE_BARCODE", "UNIT_BARCODE", "PACKAGE_DESC", "SORT_ORDER", "PACKAGE_REORDER_LEVEL", "PACKAGE_REPLENISH_LEVEL", "DESCRIPTION", "UNIT_SELLING_PRICE" };
 
   public InventoryImporterTableModel()
   {
     super(columnNames);
   }
 
   public InventoryImporterTableModel(List<InventoryItem> rows) {
     super(columnNames, rows);
   }
 
   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
   {
     InventoryItem inventoryItem = (InventoryItem)getRowData(rowIndex);
 
     switch (columnIndex) {
     case 0:
       inventoryItem.setName((String)aValue);
       break;
     case 1:
       inventoryItem.setUnitPerPackage((Double)aValue);
       break;
     case 2:
       inventoryItem.setTotalPackages((Integer)aValue);
       break;
     case 3:
       inventoryItem.setAverageRUnitPrice((Double)aValue);
       break;
     case 4:
       inventoryItem.setTotalRecepieUnits((Double)aValue);
       break;
     case 5:
       inventoryItem.setUnitPurchasePrice((Double)aValue);
       break;
     case 6:
       inventoryItem.setPackageBarcode((String)aValue);
       break;
     case 7:
       inventoryItem.setUnitBarcode((String)aValue);
       break;
     case 8:
       break;
     case 9:
       inventoryItem.setSortOrder((Integer)aValue);
       break;
     case 10:
       inventoryItem.setPackageReorderLevel((Integer)aValue);
       break;
     case 11:
       inventoryItem.setPackageReplenishLevel((Integer)aValue);
       break;
     case 12:
       inventoryItem.setDescription((String)aValue);
       break;
     case 13:
       inventoryItem.setUnitSellingPrice((Double)aValue);
       break;
     }
   }
 
   public Object getValueAt(int rowIndex, int columnIndex)
   {
     InventoryItem inventoryItem = (InventoryItem)getRowData(rowIndex);
 
     switch (columnIndex) {
     case 0:
       return inventoryItem.getName();
     case 1:
       return inventoryItem.getUnitPerPackage();
     case 2:
       return inventoryItem.getTotalPackages();
     case 3:
       return inventoryItem.getAverageRUnitPrice();
     case 4:
       return inventoryItem.getTotalRecepieUnits();
     case 5:
       return inventoryItem.getUnitPurchasePrice();
     case 6:
       return inventoryItem.getPackageBarcode();
     case 7:
       return inventoryItem.getUnitBarcode();
     case 8:
       return inventoryItem.getPackagingUnit();
     case 9:
       return inventoryItem.getSortOrder();
     case 10:
       return inventoryItem.getPackageReorderLevel();
     case 11:
       return inventoryItem.getPackageReplenishLevel();
     case 12:
       return inventoryItem.getDescription();
     case 13:
       return inventoryItem.getUnitSellingPrice();
     }
 
     return null;
   }
 }

/* Location:           C:\Users\SOMYA\Downloads\floreantpos_14452\floreantpos-1.4-build556\plugins\orostock-0.1.jar
 * Qualified Name:     com.orostock.inventory.ui.importer.InventoryImportarTableModel
 * JD-Core Version:    0.6.0
 */