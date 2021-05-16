package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfo;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfoRegular;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRegular;
import com.utils.xml.stax.XmlReader;

import javax.xml.stream.events.StartElement;
import java.util.List;
import java.util.Set;

public class DataElementCallTreeRegular extends DataElementCallTree {

    public DataElementCallTreeRegular(String functionName, CallTreeAdditionalInfo callTreeAdditionalInfo,
                                      List<String> calls, Set<String> calledBy) {

        super(functionName, DataInfoCallTreeRegular.getInstance(), callTreeAdditionalInfo, calls, calledBy);
    }

    public DataElementCallTreeRegular(XmlReader xmlReader, StartElement startElement, int rowIndex) {

        super(xmlReader, startElement, rowIndex, DataInfoCallTreeRegular.getInstance(),
                new CallTreeAdditionalInfoRegular(xmlReader, startElement));
    }
}
