package com.utils.xls.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class XlsCellString extends XlsCell {

	private final String value;

	public XlsCellString(final String value) {

		super();

		this.value = value;
	}

	public XlsCellString(final CellStyle style, final String value) {

		super(style);

		this.value = value;
	}

	@Override
	public Cell write(final Row row, final int cellIndex) {

		final Cell cell = super.write(row, cellIndex);

		cell.setCellValue(this.value);

		return cell;
	}
}
