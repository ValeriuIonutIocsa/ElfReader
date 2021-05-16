package com.utils.xls.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class XlsCellLong extends XlsCell {

    private final Long value;

    public XlsCellLong(final Long value) {

        super();

        this.value = value;
    }

    public XlsCellLong(final CellStyle style, final Long value) {

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
