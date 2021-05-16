package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.OperationCallTree;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationCall16 extends OperationCallTree {

    public OperationCall16() {
        super(
                "call (16 bits)",
                DataTypes.start32String.substring(0, 8) + DataTypes.intToPaddedBinaryString(0x5c, 8),
                "PC = PC + sign_ext(2 * disp8)");
    }

    OperationCall16(String name, String pattern, String formula){
        super(name, pattern, formula);
    }

    @Override
    public long getCalledFunctionAddress(long address, byte[] bytes) {

        String displacementString = DataTypes.byteToString(bytes[0]);

        int displacement = Integer.parseInt(displacementString, 2);

        String displacementTimesTwo = Integer.toBinaryString(displacement * 2);

        String signExtendedDisplacement = DataTypes.signExtendTo32(displacementTimesTwo, 8);

        long callAddress = address + Long.parseLong(signExtendedDisplacement, 2);
        callAddress = callAddress & 0xFFFFFFFFL;
        return callAddress;
    }
}
