package com.conti.elf_reader.data_analyzers.call_tree_analyzer.powerpc_operations;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.OperationCallTree;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationB extends OperationCallTree {

    public OperationB() {
        super(
                "b (branch)",
                DataTypes.intToPaddedBinaryString(18, 6) + DataTypes.start32String.substring(6, 32),
                "AA = 30, LI = [6:29], if AA then NIA <-- sign_ext(LI || 0b00) else NIA <-- CIA + sign_ext(LI || 0b00)");
    }

    @Override
    public long getCalledFunctionAddress(long address, byte[] bytes) {

        String bytesString = DataTypes.bytesToString(bytes);

        char aa = bytesString.charAt(30);

        String bdString = bytesString.substring(6, 30);

        long bd = Integer.parseInt(bdString, 2) | 0x0b00;
        if (aa == '0') {
            bd += address;
        }
        return bd;
    }
}
