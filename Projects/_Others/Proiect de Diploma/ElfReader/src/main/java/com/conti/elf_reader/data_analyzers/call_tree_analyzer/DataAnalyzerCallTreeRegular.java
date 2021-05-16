package com.conti.elf_reader.data_analyzers.call_tree_analyzer;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeRegular;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfo;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfoRegular;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_parsers.elf.data.SymbolTableEntryRow;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.utils.data_types.HexString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataAnalyzerCallTreeRegular extends DataAnalyzerCallTree {

    public DataAnalyzerCallTreeRegular(Settings settings, ElfFile elfFile) {
        super(settings, elfFile);
    }

    @Override
    protected void fillAdditionalInfoMap(
            Map<String, List<String>> callsMap, Map<String, Set<String>> calledByMap,
            Map<String, CallTreeAdditionalInfo> additionalInfoMap) {

        List<SymbolTableEntryRow> symbolTableEntries = elfFile.getSymbolTableEntries();
        Map<String, SymbolTableEntryRow> symbolTableEntriesByNameMap = new HashMap<>();
        for (SymbolTableEntryRow symbolTableEntry : symbolTableEntries) {
            symbolTableEntriesByNameMap.put(symbolTableEntry.getName(), symbolTableEntry);
        }

        for (String functionName : callsMap.keySet()) {

            SymbolTableEntryRow symbolTableEntry = symbolTableEntriesByNameMap
                    .getOrDefault(functionName, null);
            HexString address = symbolTableEntry != null ? symbolTableEntry.getAddress() : null;
            additionalInfoMap.put(functionName, new CallTreeAdditionalInfoRegular(address));
        }
    }

    @Override
    protected DataElementCallTree createDataElementCallTree(
            String functionName, CallTreeAdditionalInfo callTreeAdditionalInfo,
            List<String> calls, Set<String> calledBy) {

        return new DataElementCallTreeRegular(functionName, callTreeAdditionalInfo, calls, calledBy);
    }
}
