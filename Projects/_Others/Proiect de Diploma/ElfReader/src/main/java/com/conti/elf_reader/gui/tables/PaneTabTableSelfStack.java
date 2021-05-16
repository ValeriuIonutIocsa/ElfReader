package com.conti.elf_reader.gui.tables;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.data.DataElementSelfStack;
import com.conti.elf_reader.data_info.tables.DataInfoSelfStack;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.settings.Settings;
import com.utils.xml.stax.XmlReader;

import javax.xml.stream.events.StartElement;

public class PaneTabTableSelfStack extends PaneTabTable {

    public PaneTabTableSelfStack(Settings settings) {
        super(settings, DataInfoSelfStack.getInstance());
    }

    @Override
    protected DataElementTableViewRow createTableViewItem(XmlReader xmlReader, StartElement startElement, int rowIndex) {
        return new DataElementSelfStack(xmlReader, startElement, rowIndex);
    }
}
