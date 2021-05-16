package com.conti.elf_reader.gui.tables;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoSymbols;
import com.conti.elf_reader.data_parsers.elf.data.SymbolTableEntryRow;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.settings.Settings;
import com.utils.xml.stax.XmlReader;

import javax.xml.stream.events.StartElement;

public class PaneTabTableSymbols extends PaneTabTable {

    public PaneTabTableSymbols(Settings settings) {
        super(settings, DataInfoSymbols.getInstance());
    }

    @Override
    protected DataElementTableViewRow createTableViewItem(XmlReader xmlReader, StartElement startElement, int rowIndex) {
        return new SymbolTableEntryRow(xmlReader, startElement, rowIndex);
    }
}