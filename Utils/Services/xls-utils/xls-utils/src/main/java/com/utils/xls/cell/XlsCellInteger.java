package com.utils.xls.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class XlsCellInteger extends XlsCell {

    private final Integer value;

    public XlsCellInteger(final Integer value) {

        super();

        this.value = value;
    }

    public XlsCellInteger(final CellStyle style, final Integer value) {

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
