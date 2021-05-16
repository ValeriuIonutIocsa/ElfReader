package com.conti.elf_reader.data_analyzers.call_tree_analyzer;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeMemories;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfo;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfoMemories;
import com.conti.elf_reader.data_analyzers.size_analyzer.DataAnalyzerSize;
import com.conti.elf_reader.data_analyzers.size_analyzer.data.DataElementSize;
import com.conti.elf_reader.data_info.tables.DataInfoSize;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeMemories;
import com.conti.elf_reader.data_parsers.core_architecture.ParserCoreArchitectureFile;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.WindowMain;
import com.conti.elf_reader.gui.tables.call_tree_tables.memories.PaneTabCallTreeMemories;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.utils.data_types.HexString;
import com.utils.log.Logger;

import java.util.*;

public class DataAnalyzerCallTreeMemories extends DataAnalyzerCallTree {

    private class SizeInfo {

        private String sourceFileName;
        private HexString address;
        private final String protection;

        private SizeInfo(String sourceFileName, HexString address, String protection) {

            this.sourceFileName = sourceFileName;
            this.address = address;
            this.protection = protection;
        }
    }

    public DataAnalyzerCallTreeMemories(Settings settings, ElfFile elfFile) {
        super(settings, elfFile);
    }

    @Override
    protected void fillAdditionalInfoMap(
            Map<String, List<String>> callsMap, Map<String, Set<String>> calledByMap,
            Map<String, CallTreeAdditionalInfo> additionalInfoMap) {

        Map<String, SizeInfo> functionNameToSizeInfoMap = new HashMap<>();
        fillFunctionNameToSizeInfoMap(functionNameToSizeInfoMap);

        if (settings.isGui()) {
            PaneTabCallTreeMemories paneTabCallTreeMemories = (PaneTabCallTreeMemories) WindowMain
                    .getPaneTabTableByName(DataInfoCallTreeMemories.getInstance().getTabName());
            ParserCoreArchitectureFile.parse(paneTabCallTreeMemories.getCoreArchitectureFilePath());
        }

        for (String functionName : callsMap.keySet()) {

            SizeInfo sizeInfo = functionNameToSizeInfoMap.getOrDefault(functionName, null);
            String sourceFileName = sizeInfo != null ? sizeInfo.sourceFileName : "";
            HexString address = sizeInfo != null ? sizeInfo.address : null;
            String memoryName = ParserCoreArchitectureFile.computeMemoryName(address);
            String protection = sizeInfo != null ? sizeInfo.protection : null;
            additionalInfoMap.putIfAbsent(functionName, new CallTreeAdditionalInfoMemories(
                    sourceFileName, address, memoryName, protection));
        }
    }

    private void fillFunctionNameToSizeInfoMap(Map<String, SizeInfo> functionNameToSizeInfoMap) {

        try {
            elfFile.readFile(DataInfoSize.getInstance());
            DataAnalyzerSize dataAnalyzerSelfStack = new DataAnalyzerSize(settings, elfFile);
            dataAnalyzerSelfStack.analyze(false);
            Collection<? extends DataElementTableViewRow> dataElementTableViewRowList =
                    dataAnalyzerSelfStack.getDataElementTableViewRowList();

            for (DataElementTableViewRow dataElementTableViewRow : dataElementTableViewRowList) {

                DataElementSize dataElementSize = (DataElementSize) dataElementTableViewRow;
                String name = dataElementSize.getSymbolName();
                HexString address = dataElementSize.getSymbolAddress();
                String sourceFileName = dataElementSize.getSymbolFile();
                String protection = computeProtection(dataElementSize.getSectionName());
                functionNameToSizeInfoMap.putIfAbsent(name, new SizeInfo(sourceFileName, address, protection));
            }

        } catch (Exception ignored) {
            Logger.printException(ignored);
        }
    }

    private String computeProtection(String sectionName) {

        String[] sectionNameSplit = sectionName.split("\\.", 0);
        if (sectionNameSplit.length < 2)
            return "";

        return sectionNameSplit[sectionNameSplit.length - 2] +
                "." + sectionNameSplit[sectionNameSplit.length - 1];
    }


    @Override
    protected DataElementCallTree createDataElementCallTree(
            String functionName, CallTreeAdditionalInfo callTreeAdditionalInfo,
            List<String> calls, Set<String> calledBy) {

        return new DataElementCallTreeMemories(functionName, callTreeAdditionalInfo, calls, calledBy);
    }
}
