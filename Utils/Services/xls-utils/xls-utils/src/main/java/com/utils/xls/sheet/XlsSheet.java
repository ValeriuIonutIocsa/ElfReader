package com.utils.xls.sheet;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.utils.xls.row.XlsRow;

public class XlsSheet {

	private final String name;
	private final double[] columnWidthPercentages;

	private final List<XlsRow> xlsRows = new ArrayList<>();

	public XlsSheet(final String name) {

		this(name, null);
	}

	public XlsSheet(final String name, final double[] columnWidthPercentages) {

		this.name = name;
		this.columnWidthPercentages = columnWidthPercentages;
	}

	public void write(final Workbook workbook) {

		final Sheet sheet = this.getOrCreateSheet(workbook, this.name);

		if (this.columnWidthPercentages != null) {
			this.sizeColumns(sheet, this.columnWidthPercentages);
		}

		int maxCellCount = 0;
		final int rowCount = this.xlsRows.size();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {

			final XlsRow xlsRow = this.xlsRows.get(rowIndex);
			xlsRow.write(sheet, rowIndex);

			final int cellCount = xlsRow.getXlsCells().size();
			maxCellCount = Math.max(maxCellCount, cellCount);
		}
	}

	private Sheet getOrCreateSheet(final Workbook workbook, final String name) {

		final Sheet sheet = workbook.getSheet(name);
		if (sheet != null) {
			return sheet;
		}

		return workbook.createSheet(name);
	}

	private void sizeColumns(final Sheet sheet, final double[] columnWidthPercentages) {

		for (int columnIndex = 0; columnIndex < columnWidthPercentages.length; columnIndex++) {

			final double widthPercentage = columnWidthPercentages[columnIndex];
			sheet.setColumnWidth(columnIndex, (int) Math.floor(54000 * widthPercentage));
		}
	}

	public List<XlsRow> getXlsRows() {
		return this.xlsRows;
	}
}
