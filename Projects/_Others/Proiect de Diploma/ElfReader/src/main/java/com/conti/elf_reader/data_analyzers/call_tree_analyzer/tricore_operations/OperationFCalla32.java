package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationFCalla32 extends OperationCalla32 {

    public OperationFCalla32() {
        super(
                "fcalla (32 bits)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0xe1, 8));
    }
}
