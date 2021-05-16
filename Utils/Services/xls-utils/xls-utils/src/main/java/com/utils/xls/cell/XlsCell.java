package com.utils.xls.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public abstract class XlsCell {

    private final CellStyle style;

    XlsCell(){

        this(null);
    }

    XlsCell(CellStyle style){

        this.style = style;
    }

    public Cell write(Row row, int cellIndex) {

        Cell cell = getOrCreateCell(row, cellIndex);
        if(style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }

    private Cell getOrCreateCell(Row row, int cellIndex) {

        final Cell cell = row.getCell(cellIndex);
        if (cell != null)
            return cell;

        return row.createCell(cellIndex);
    }
}
