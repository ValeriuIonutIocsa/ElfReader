package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info;

import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRegular;
import com.conti.elf_reader.utils.data_types.HexString;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;

import javax.xml.stream.events.StartElement;

public class CallTreeAdditionalInfoRegular extends CallTreeAdditionalInfo {

    private final HexString address;

    public CallTreeAdditionalInfoRegular(HexString address) {

        super(DataInfoCallTreeRegular.getInstance());

        this.address = address;
    }

    public CallTreeAdditionalInfoRegular(XmlReader xmlReader, StartElement startElement) {

        super(DataInfoCallTreeRegular.getInstance());

        address = new HexString(xmlReader.getAttribute(startElement,
                columnInfoArray[0].getColumnTitleName()));
    }

    @Override
    public void writeDataElement(XmlWriter xmlWriter) {

        xmlWriter.writeAttribute(columnInfoArray[0].getColumnTitleName(),
                address != null ? address.toString() : "");
    }

    @Override
    public Object[] getAdditionalTableRowData() {

        return new Object[]{
                address
        };
    }
}
