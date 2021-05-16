package com.conti.elf_reader.data_writers.writers;

import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Collection;

public class DataFileWriterXlsx extends DataFileWriter {

    private final Collection<? extends DataElementTableViewRow> tableViewItems;

    public DataFileWriterXlsx(
            Path outputPath, DataInfoTable dataInfoTable, Collection<? extends DataElementTableViewRow> tableViewItems) {

        super(outputPath, dataInfoTable);
        this.tableViewItems = tableViewItems;
    }

    @Override
    void writeData() throws Exception {

        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(), 100);
        SXSSFSheet sheet = workbook.createSheet();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 13);
        font.setBold(true);
        headerStyle.setFont(font);

        CellStyle regularStyle = workbook.createCellStyle();
        regularStyle.setAlignment(HorizontalAlignment.LEFT);

        TableViewColumnInfo[] columnInfoArray = dataInfoTable.getColumnInfoArray();
        createRow(sheet, columnInfoArray, -1, (short) 640, headerStyle);

        int rowIndex = 0;
        for (DataElementTableViewRow dataElementTableViewRow : tableViewItems) {
            createRow(sheet, dataElementTableViewRow.getRowData(), rowIndex++, (short) -1, regularStyle);
        }

        int tableColumnsLength = columnInfoArray.length;
        for (int i = 0; i < tableColumnsLength; i++) {

            double widthPercentage = columnInfoArray[i].getWidthPercentage();
            sheet.setColumnWidth(i, (int) Math.floor(54000 * widthPercentage));
        }

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                outputPath.toAbsolutePath().toString()));
        workbook.write(bufferedOutputStream);
        workbook.close();
        bufferedOutputStream.close();
    }

    private void createRow(SXSSFSheet sheet, Object[] rowData,
                           int rowIndex, short rowHeight, CellStyle style) {

        SXSSFRow row = sheet.createRow(rowIndex + 1);
        if (rowHeight >= 0) {
            row.setHeight(rowHeight);
        }

        for (int i = 0, rowDataLength = rowData.length; i < rowDataLength; i++) {

            SXSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);

            Object data = rowData[i];
            if (data == null)
                continue;

            if (data instanceof Integer) {
                cell.setCellValue((int) data);

            } else if (data instanceof Long) {
                cell.setCellValue((long) data);

            } else {
                cell.setCellValue(data.toString());
            }
        }
    }
}
