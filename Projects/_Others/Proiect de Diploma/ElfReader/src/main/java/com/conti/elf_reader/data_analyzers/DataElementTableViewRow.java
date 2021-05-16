package com.conti.elf_reader.data_analyzers;

import com.utils.xml.stax.XmlWriter;

public abstract class DataElementTableViewRow {

    public abstract Object[] getRowData();

    public abstract void createDataElement(XmlWriter xmlWriter);
}
