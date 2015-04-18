package com.orostock.inventory.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class ColorColumnRenderer extends DefaultTableCellRenderer {
	Color bkgndColor, fgndColor;
	int rowI, columnI;

	public ColorColumnRenderer() {
		super();
	}

	public ColorColumnRenderer(Color bkgnd, Color foregnd) {
		super();
		bkgndColor = bkgnd;
		fgndColor = foregnd;
	}

	public ColorColumnRenderer(Color bkgnd, Color foregnd, int row, int col) {
		super();
		bkgndColor = bkgnd;
		fgndColor = foregnd;
		rowI = row;
		columnI = col;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (rowI == row && columnI == column) {
			cell.setBackground(bkgndColor);
			cell.setForeground(fgndColor);
		}
		return cell;
	}
}
