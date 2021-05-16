package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.OperationCallTree;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationCalla32 extends OperationCallTree {

    public OperationCalla32() {
        super(
                "calla (32 bits)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0xed, 8),
                "PC = {disp24[23:20], 7'b0000000, disp24[19:0], 1'b0};");
    }

    OperationCalla32(String name, String pattern){
        super(name, pattern, "PC = {disp24[23:20], 7'b0000000, disp24[19:0], 1'b0};");
    }

    @Override
    public long getCalledFunctionAddress(long address, byte[] bytes) {

        final String disp_16_23 = DataTypes.byteToString(bytes[2]);

        String callAddressString = disp_16_23.substring(0, 4) + "0000000" +
                disp_16_23.substring(4, 8) + DataTypes.byteToString(bytes[0]) + DataTypes.byteToString(bytes[1]) + "0";

        long callAddress = Long.parseLong(callAddressString, 2);
        callAddress = callAddress & 0xFFFFFFFFL;
        return callAddress;
    }
}
