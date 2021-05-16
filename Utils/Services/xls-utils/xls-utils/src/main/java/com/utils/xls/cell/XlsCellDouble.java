package com.utils.xls.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class XlsCellDouble extends XlsCell {

    private final Double value;

    public XlsCellDouble(final Double value) {

        super();

        this.value = value;
    }

    public XlsCellDouble(final CellStyle style, final Double value) {

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
