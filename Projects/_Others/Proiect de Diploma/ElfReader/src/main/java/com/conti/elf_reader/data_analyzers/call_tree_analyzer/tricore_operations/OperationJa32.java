package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationJa32 extends OperationCalla32 {

    public OperationJa32() {
        super(
                "ja (32 bits)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0x9d, 8));
    }
}
