package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info;

import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.utils.xml.stax.XmlWriter;

import java.util.Arrays;

public abstract class CallTreeAdditionalInfo {

    final TableViewColumnInfo[] columnInfoArray;

    CallTreeAdditionalInfo(DataInfoTable dataInfoTable){

        TableViewColumnInfo[] columnTitleNames = dataInfoTable.getColumnInfoArray();
        columnInfoArray = Arrays.copyOfRange(columnTitleNames, 2, columnTitleNames.length - 2);
    }

    public abstract void writeDataElement(XmlWriter xmlWriter);

    public abstract Object[] getAdditionalTableRowData();
}
