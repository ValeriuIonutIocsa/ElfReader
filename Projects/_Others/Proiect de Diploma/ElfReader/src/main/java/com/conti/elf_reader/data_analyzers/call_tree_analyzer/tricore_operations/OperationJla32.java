package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationJla32 extends OperationCalla32{

    public OperationJla32() {
        super(
                "jla (32 bits)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0xdd, 8));
    }
}
