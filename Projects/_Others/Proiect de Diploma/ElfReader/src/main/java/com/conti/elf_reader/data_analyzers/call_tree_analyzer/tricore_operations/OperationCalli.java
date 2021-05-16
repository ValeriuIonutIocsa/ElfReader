package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.OperationCallTree;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationCalli extends OperationCallTree {

    public OperationCalli() {
        super(
                "calli (32 bits)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0x2d, 8),
                "indirect call (impossible to compute)");
    }

    @Override
    public long getCalledFunctionAddress(long address, byte[] bytes) {
        return 0;
    }
}
