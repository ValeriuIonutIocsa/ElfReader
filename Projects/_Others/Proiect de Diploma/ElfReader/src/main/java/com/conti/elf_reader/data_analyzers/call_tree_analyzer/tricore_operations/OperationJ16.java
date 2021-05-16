package com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations;

import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationJ16 extends OperationCall16 {

    public OperationJ16() {
        super(
                "j (16 bits)",
                DataTypes.start32String.substring(0, 8) + DataTypes.intToPaddedBinaryString(0x3c, 8),
                "PC = PC + sign_ext(disp8) * 2");
    }
}
