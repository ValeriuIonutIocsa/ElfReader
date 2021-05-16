package com.utils.xls.row;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.utils.xls.cell.XlsCell;

public class XlsRow {

	private final Float height;
	private final List<XlsCell> xlsCells = new ArrayList<>();

	public XlsRow() {

		this(null);
	}

	public XlsRow(final Float height) {

		this.height = height;
	}

	public void write(final Sheet sheet, final int rowIndex) {

		final Row row = this.getOrCreateRow(sheet, rowIndex);
		if (this.height != null) {
			row.setHeightInPoints(this.height);
		}

		final int cellCount = this.xlsCells.size();
		for (int cellIndex = 0; cellIndex < cellCount; cellIndex++) {

			final XlsCell xlsCell = this.xlsCells.get(cellIndex);
			xlsCell.write(row, cellIndex);
		}
	}

	private Row getOrCreateRow(final Sheet sheet, final int rowIndex) {

		final Row row = sheet.getRow(rowIndex);
		if (row != null) {
			return row;
		}

		return sheet.createRow(rowIndex);
	}

	public List<XlsCell> getXlsCells() {
		return this.xlsCells;
	}
}
