package com.utils.xls.style;

import org.apache.poi.ss.usermodel.*;

public class XlsCellStyles {

    public static CellStyle createCellStyleTitle(Workbook workbook) {

        final CellStyle cellStyleTitle = workbook.createCellStyle();

        cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);

        final Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 13);
        font.setBold(true);
        cellStyleTitle.setFont(font);

        return cellStyleTitle;
    }

    public static CellStyle createCellStyleLeft(Workbook workbook) {

        final CellStyle cellStyleLeft = workbook.createCellStyle();
        cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        return cellStyleLeft;
    }

    public static CellStyle createHyperlinkCellStyle(Workbook workbook) {

        final CellStyle cellStyleHyperlink = workbook.createCellStyle();

        final Font fontHyperlink = workbook.createFont();
        fontHyperlink.setUnderline(Font.U_SINGLE);
        fontHyperlink.setColor(IndexedColors.BLUE.getIndex());
        cellStyleHyperlink.setFont(fontHyperlink);

        return cellStyleHyperlink;
    }
}
