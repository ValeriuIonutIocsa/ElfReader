package com.conti.elf_reader.data_analyzers.call_tree_analyzer;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeRunnables;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfo;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfoRunnables;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.DataAnalyzerSelfStack;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.data.DataElementSelfStack;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.settings.Settings;

import java.util.*;

public class DataAnalyzerCallTreeRunnables extends DataAnalyzerCallTree {

    public DataAnalyzerCallTreeRunnables(Settings settings, ElfFile elfFile) {
        super(settings, elfFile);
    }

    @Override
    protected void fillAdditionalInfoMap(
            Map<String, List<String>> callsMap, Map<String, Set<String>> calledByMap,
            Map<String, CallTreeAdditionalInfo> additionalInfoMap) {

        Map<String, Integer> selfStackMap = new HashMap<>();
        fillFunctionNameToSelfStackMap(selfStackMap);

        Map<String, List<String>> recursiveMap = new HashMap<>();
        for (String functionName : callsMap.keySet()) {

            if (!additionalInfoMap.containsKey(functionName)) {
                fillRecursiveInfo(":" + functionName + ":",
                        functionName, callsMap, selfStackMap, recursiveMap, additionalInfoMap);
            }
        }
    }

    private void fillFunctionNameToSelfStackMap(Map<String, Integer> functionNameToSelfStackMap) {

        DataAnalyzerSelfStack dataAnalyzerSelfStack = new DataAnalyzerSelfStack(settings, elfFile);
        dataAnalyzerSelfStack.analyze(false);
        Collection<? extends DataElementTableViewRow> dataElementTableViewRowList =
                dataAnalyzerSelfStack.getDataElementTableViewRowList();


        for (DataElementTableViewRow dataElementTableViewRow : dataElementTableViewRowList) {

            DataElementSelfStack dataElementSelfStack = (DataElementSelfStack) dataElementTableViewRow;
            String name = dataElementSelfStack.getName();
            int selfStack = dataElementSelfStack.getSelfStack();
            functionNameToSelfStackMap.putIfAbsent(name, selfStack);
        }
    }

    private int fillRecursiveInfo(
            String pathInTree, String functionName,
            Map<String, List<String>> callsMap, Map<String, Integer> selfStackMap,
            Map<String, List<String>> recursiveMap, Map<String, CallTreeAdditionalInfo> additionalInfoMap) {

        List<String> calls = callsMap.getOrDefault(functionName, null);

        int reachable = 0;
        if (calls != null) {
            for (String calledFunctionName : calls) {

                String pathInTreeIncrement = ":" + calledFunctionName + ":";
                int recursionIndex = pathInTree.indexOf(pathInTreeIncrement);
                if (recursionIndex >= 0) {
                    String recursivePathString = pathInTree.substring(recursionIndex + 1) + ":" + calledFunctionName;
                    List<String> recursivePaths = recursiveMap.getOrDefault(calledFunctionName, null);
                    if (recursivePaths == null) {
                        recursivePaths = new ArrayList<>();
                        recursiveMap.put(calledFunctionName, recursivePaths);
                    }
                    recursivePaths.add(recursivePathString);
                    continue;
                }

                int reachableOfDescendants;
                CallTreeAdditionalInfo additionalInfo = additionalInfoMap.getOrDefault(calledFunctionName, null);
                if (additionalInfo != null) {
                    reachableOfDescendants = ((CallTreeAdditionalInfoRunnables) additionalInfo).getReachable();
                } else {
                    reachableOfDescendants = fillRecursiveInfo(pathInTree + pathInTreeIncrement,
                            calledFunctionName, callsMap, selfStackMap, recursiveMap, additionalInfoMap);
                }
                if (reachableOfDescendants > reachable) {
                    reachable = reachableOfDescendants;
                }
            }
        }

        int selfStack = selfStackMap.getOrDefault(functionName, 0);
        reachable = selfStack + reachable;
        List<String> recursivePaths = recursiveMap.getOrDefault(functionName, null);
        additionalInfoMap.put(functionName, new CallTreeAdditionalInfoRunnables(selfStack, reachable, recursivePaths));
        return reachable;
    }

    @Override
    protected DataElementCallTree createDataElementCallTree(
            String functionName, CallTreeAdditionalInfo callTreeAdditionalInfo,
            List<String> calls, Set<String> calledBy) {

        return new DataElementCallTreeRunnables(functionName, callTreeAdditionalInfo, calls, calledBy);
    }
}
