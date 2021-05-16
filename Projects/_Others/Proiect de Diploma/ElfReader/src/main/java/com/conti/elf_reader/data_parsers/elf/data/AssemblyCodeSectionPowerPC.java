package com.conti.elf_reader.data_parsers.elf.data;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class AssemblyCodeSectionPowerPC extends AssemblyCodeSection {

    public AssemblyCodeSectionPowerPC(
            ByteBuffer sectionByteBuffer, SectionHeaderEntry sectionHeaderEntry,
            Map<Long, String> functionSymbolsAddressNameMap, Map<Long, List<Long>> copyTableSymbolAddressMap) {

        super(sectionByteBuffer, sectionHeaderEntry, functionSymbolsAddressNameMap, copyTableSymbolAddressMap);
    }

    @Override
    byte[] getInstructionBytes(byte[] bytes, int i) {

        byte[] instructionBytes;

        if (i < bytes.length - 3) {
            instructionBytes = new byte[]{bytes[i + 3], bytes[i + 2], bytes[i + 1], bytes[i]};

        } else {
            int remainingBytesCount = bytes.length - i;
            instructionBytes = new byte[remainingBytesCount];
            System.arraycopy(bytes, i, instructionBytes, 0, remainingBytesCount);
        }
        return instructionBytes;
    }

    @Override
    boolean isEndOfFunction(byte[] instructionBytes) {
        return false;
    }
}
