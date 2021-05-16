package com.conti.elf_reader.data_info.tables;

public class TableViewColumnInfo {

    private final String columnTitleName;
    private final double widthPercentage;

    public TableViewColumnInfo(String columnTitleName, double widthPercentage) {

        this.columnTitleName = columnTitleName;
        this.widthPercentage = widthPercentage;
    }

    public String getColumnTitleName() {
        return columnTitleName;
    }

    public double getWidthPercentage() {
        return widthPercentage;
    }

    @Override
    public String toString() {
        return columnTitleName;
    }
}
