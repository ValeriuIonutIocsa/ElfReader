package com.conti.elf_reader.data_parsers.elf.data;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.DataAnalyzerCallTree;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class AssemblyCodeSection {

    private final String sectionName;
    private final Set<AssemblyCodeInstruction> instructions;

    AssemblyCodeSection(
            ByteBuffer sectionByteBuffer, SectionHeaderEntry sectionHeaderEntry,
            Map<Long, String> functionSymbolsAddressNameMap, Map<Long, List<Long>> copyTableSymbolAddressMap) {

        sectionName = sectionHeaderEntry.getName();
        instructions = new TreeSet<>();

        long address = sectionHeaderEntry.getAddress().getValue();
        byte[] bytes = sectionByteBuffer.array();
        boolean reachedEnd = false;
        for (int i = 0; i < bytes.length; ) {

            long currentAddress = address + i;

            String label = DataAnalyzerCallTree.findSymbolNameByAddress(currentAddress,
                    functionSymbolsAddressNameMap, copyTableSymbolAddressMap);
            if (label != null && !label.isEmpty()) {
                reachedEnd = false;
            }

            byte[] instructionBytes = getInstructionBytes(bytes, i);
            i += instructionBytes.length;

            if (!reachedEnd) {
                instructions.add(new AssemblyCodeInstruction(currentAddress, label, instructionBytes));
            }

            reachedEnd = isEndOfFunction(instructionBytes);
        }
    }

    abstract byte[] getInstructionBytes(byte[] bytes, int i);

    abstract boolean isEndOfFunction(byte[] instructionBytes);

    public String getSectionName() {
        return sectionName;
    }

    public Set<AssemblyCodeInstruction> getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("Dissasembly of section <" + sectionName + ">:\n\n");

        for (AssemblyCodeInstruction instruction : instructions) {
            sb.append(instruction).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
