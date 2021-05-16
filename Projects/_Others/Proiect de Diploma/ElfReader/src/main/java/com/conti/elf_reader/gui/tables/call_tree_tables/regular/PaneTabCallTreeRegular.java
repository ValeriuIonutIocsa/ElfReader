package com.conti.elf_reader.gui.tables.call_tree_tables.regular;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeRegular;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRegular;
import com.conti.elf_reader.gui.tables.call_tree_tables.PaneTabCallTree;
import com.conti.elf_reader.settings.Settings;
import com.utils.xml.stax.XmlReader;

import javax.xml.stream.events.StartElement;

public class PaneTabCallTreeRegular extends PaneTabCallTree {

    public PaneTabCallTreeRegular(Settings settings) {
        super(settings, DataInfoCallTreeRegular.getInstance());
    }

    @Override
    protected DataElementTableViewRow createTableViewItem(
            XmlReader xmlReader, StartElement startElement, int rowIndex) {
        return new DataElementCallTreeRegular(xmlReader, startElement, rowIndex);
    }
}
