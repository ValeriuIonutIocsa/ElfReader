package com.conti.elf_reader.cli;

import com.conti.elf_reader.data_info.DataInfo;
import com.conti.elf_reader.data_info.DataInfoDumpAll;
import com.conti.elf_reader.data_info.DataInfoSymbolsToSectionsMap;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeMemories;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRegular;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRunnables;
import com.conti.elf_reader.data_info.tables.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class Options {

    private final Map<String, DataInfo> optionToDataInfoMap = new LinkedHashMap<>();

    public Options() {

        addOption(optionToDataInfoMap, DataInfoCallTreeRegular.getInstance());
        addOption(optionToDataInfoMap, DataInfoCallTreeMemories.getInstance());
        addOption(optionToDataInfoMap, DataInfoCallTreeRunnables.getInstance());

        addOption(optionToDataInfoMap, DataInfoSelfStack.getInstance());
        addOption(optionToDataInfoMap, DataInfoSize.getInstance());
        addOption(optionToDataInfoMap, DataInfoSections.getInstance());
        addOption(optionToDataInfoMap, DataInfoSymbols.getInstance());

        addOption(optionToDataInfoMap, DataInfoDumpAll.getInstance());
        addOption(optionToDataInfoMap, DataInfoSymbolsToSectionsMap.getInstance());
    }

    private void addOption(Map<String, DataInfo> optionToDataInfoMap, DataInfo dataInfo) {
        optionToDataInfoMap.putIfAbsent(dataInfo.getOption(), dataInfo);
    }

    public String supportedOptionsToString() {

        StringBuilder stringBuilder = new StringBuilder("Supported options:" + System.lineSeparator());
        for (DataInfo dataInfo : optionToDataInfoMap.values()) {
            stringBuilder.append("    ").append(dataInfo.getOption()).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public Map<String, DataInfo> getOptionToDataInfoMap() {
        return optionToDataInfoMap;
    }
}
