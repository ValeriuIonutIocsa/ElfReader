package com.conti.elf_reader.data_analyzers.self_stack_analyzer.tricore_operations;

import com.conti.elf_reader.data_analyzers.self_stack_analyzer.OperationSelfStack;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationSuba extends OperationSelfStack {

    public OperationSuba() {
        super(
                "sub.a",
                DataTypes.start32String.substring(0, 8) + DataTypes.intToPaddedBinaryString(0x20, 8),
                "A[10] = A[10] - zero_ext(const8)");
    }

    @Override
    public int getSelfStack(byte[] bytes) {
        return Integer.parseInt(DataTypes.byteToString(bytes[0]), 2);
    }
}
