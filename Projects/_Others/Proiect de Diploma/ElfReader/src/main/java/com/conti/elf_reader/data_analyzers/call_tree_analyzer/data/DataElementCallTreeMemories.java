package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfo;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfoMemories;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeMemories;
import com.utils.xml.stax.XmlReader;

import javax.xml.stream.events.StartElement;
import java.util.List;
import java.util.Set;

public class DataElementCallTreeMemories extends DataElementCallTree {

    public DataElementCallTreeMemories(
            String functionName, CallTreeAdditionalInfo callTreeAdditionalInfo,
            List<String> calls, Set<String> calledBy) {

        super(functionName, DataInfoCallTreeMemories.getInstance(), callTreeAdditionalInfo, calls, calledBy);
    }

    public DataElementCallTreeMemories(XmlReader xmlReader, StartElement startElement, int rowIndex) {

        super(xmlReader, startElement, rowIndex, DataInfoCallTreeMemories.getInstance(),
                new CallTreeAdditionalInfoMemories(xmlReader, startElement));
    }

    public CallTreeAdditionalInfoMemories getAdditionalInfo() {
        return (CallTreeAdditionalInfoMemories) callTreeAdditionalInfo;
    }

    @Override
    public String toString() {

        String result = getFunctionName() + " (";

        String memoryName = getAdditionalInfo().getMemoryName();
        String protection = getAdditionalInfo().getProtection();
        if (memoryName != null && !memoryName.isEmpty()) {
            result += memoryName;
            if (protection != null && !protection.isEmpty()) {
                result += ", " + protection;
            }

        } else if (protection != null && !protection.isEmpty()) {
            result += protection;
        }

        result += ")";
        return result;
    }
}
