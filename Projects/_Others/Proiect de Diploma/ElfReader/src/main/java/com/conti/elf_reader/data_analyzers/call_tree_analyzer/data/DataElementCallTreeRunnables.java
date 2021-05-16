package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfo;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfoRunnables;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRunnables;
import com.utils.xml.stax.XmlReader;

import javax.xml.stream.events.StartElement;
import java.util.List;
import java.util.Set;

public class DataElementCallTreeRunnables extends DataElementCallTree {

    public DataElementCallTreeRunnables(
            String functionName, CallTreeAdditionalInfo callTreeAdditionalInfo,
            List<String> calls, Set<String> calledBy) {

        super(functionName, DataInfoCallTreeRunnables.getInstance(), callTreeAdditionalInfo, calls, calledBy);
    }

    public DataElementCallTreeRunnables(XmlReader xmlReader, StartElement startElement, int rowIndex) {

        super(xmlReader, startElement, rowIndex, DataInfoCallTreeRunnables.getInstance(),
                new CallTreeAdditionalInfoRunnables(xmlReader, startElement));
    }

    @Override
    public String toString() {

        String functionName = getFunctionName();
        int selfStack = getAdditionalInfo().getSelfStack();
        int reachable = getAdditionalInfo().getReachable();
        return functionName + " (self stack: " + selfStack + ", reachable: " + reachable + ")";
    }

    public CallTreeAdditionalInfoRunnables getAdditionalInfo() {
        return (CallTreeAdditionalInfoRunnables) callTreeAdditionalInfo;
    }
}
