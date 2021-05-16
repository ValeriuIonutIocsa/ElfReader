package com.conti.elf_reader.gui.tables;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoSections;
import com.conti.elf_reader.data_parsers.elf.data.SectionHeaderEntry;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.settings.Settings;
import com.utils.xml.stax.XmlReader;

import javax.xml.stream.events.StartElement;

public class PaneTabTableSections extends PaneTabTable {

    public PaneTabTableSections(Settings settings) {
        super(settings, DataInfoSections.getInstance());
    }

    @Override
    protected DataElementTableViewRow createTableViewItem(XmlReader xmlReader, StartElement startElement, int rowIndex) {
        return new SectionHeaderEntry(xmlReader, startElement, rowIndex);
    }
}
