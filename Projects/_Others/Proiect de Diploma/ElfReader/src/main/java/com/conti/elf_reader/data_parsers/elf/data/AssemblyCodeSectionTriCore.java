package com.conti.elf_reader.data_parsers.elf.data;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class AssemblyCodeSectionTriCore extends AssemblyCodeSection {

    public AssemblyCodeSectionTriCore(
            ByteBuffer sectionByteBuffer, SectionHeaderEntry sectionHeaderEntry,
            Map<Long, String> functionSymbolsAddressNameMap, Map<Long, List<Long>> copyTableSymbolAddressMap) {

        super(sectionByteBuffer, sectionHeaderEntry, functionSymbolsAddressNameMap, copyTableSymbolAddressMap);
    }

    @Override
    byte[] getInstructionBytes(byte[] bytes, int i) {

        byte[] instructionBytes;
        int firstBit = bytes[i] & 0b00000001;
        if (firstBit == 0 && i < bytes.length - 1) {

            instructionBytes = new byte[]{bytes[i + 1], bytes[i]};

        } else if (firstBit == 1 && i < bytes.length - 3) {

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
        return String.format("%02x", instructionBytes[0]).equals("90") && (int) instructionBytes[1] == 0;
    }
}
