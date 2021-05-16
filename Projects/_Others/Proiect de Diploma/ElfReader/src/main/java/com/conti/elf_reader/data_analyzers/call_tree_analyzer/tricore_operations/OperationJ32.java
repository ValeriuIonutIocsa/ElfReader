package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationJ32 extends OperationCall32 {

    public OperationJ32() {
        super(
                "j (32 bits)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0x1d, 8));
    }
}
