package com.conti.elf_reader.data_writers.writers;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.utils.xml.stax.XmlWriter;

import java.nio.file.Path;
import java.util.Collection;

public class DataFileWriterXml extends DataFileWriter {

    private final Collection<? extends DataElementTableViewRow> tableViewItems;

    public DataFileWriterXml(
            Path outputPath, DataInfoTable dataInfoTable, Collection<? extends DataElementTableViewRow> tableViewItems) {

        super(outputPath, dataInfoTable);
        this.tableViewItems = tableViewItems;
    }

    @Override
    void writeData() {

        new XmlWriter(outputPath) {

            @Override
            protected void write() {

                writeStartDocument();
                writeStartElement(dataInfoTable.getRootElementTagName());

                for (DataElementTableViewRow dataElementTableViewRow : tableViewItems) {
                    dataElementTableViewRow.createDataElement(this);
                }

                writeEndElement(dataInfoTable.getRootElementTagName());
                writeEndDocument();
            }

        }.writeXml();
    }
}
