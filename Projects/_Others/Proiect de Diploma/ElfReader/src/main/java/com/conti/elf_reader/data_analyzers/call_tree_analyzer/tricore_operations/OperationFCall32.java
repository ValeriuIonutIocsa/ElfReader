package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationFCall32 extends OperationCall32 {

    public OperationFCall32() {

        super("fcall (32 bits)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0x61, 8));
    }
}
