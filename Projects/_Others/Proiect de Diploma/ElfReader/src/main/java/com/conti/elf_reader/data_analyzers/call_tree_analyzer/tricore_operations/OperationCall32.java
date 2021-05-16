package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.OperationCallTree;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationCall32 extends OperationCallTree {

    public OperationCall32() {
        super(
                "call (32 bits)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0x6d, 8),
                "PC = PC + sign_ext(disp24 * 2)");
    }

    OperationCall32(String name, String pattern) {
        super(name, pattern, "PC = PC + sign_ext(disp24 * 2)");
    }

    @Override
    public long getCalledFunctionAddress(long address, byte[] bytes) {

        String displacementString = DataTypes.byteToString(bytes[2]) +
                DataTypes.byteToString(bytes[0]) + DataTypes.byteToString(bytes[1]);

        int displacement = Integer.parseInt(displacementString, 2);

        String displacementTimesTwo = Integer.toBinaryString(displacement * 2);

        String signExtendedDisplacement = DataTypes.signExtendTo32(displacementTimesTwo, 24);

        long callAddress = address + Long.parseLong(signExtendedDisplacement, 2);
        callAddress = callAddress & 0xFFFFFFFFL;
        return callAddress;
    }
}
