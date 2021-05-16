package com.conti.elf_reader.data_analyzers.self_stack_analyzer.tricore_operations;

import com.conti.elf_reader.data_analyzers.self_stack_analyzer.OperationSelfStack;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationLeaLongOffset extends OperationSelfStack {

    public OperationLeaLongOffset() {
        super(
                "lea (long offset)",
                DataTypes.start32String.substring(0, 24) + DataTypes.intToPaddedBinaryString(0xd9, 8),
                "A[a] = EA[31:0]; EA = A[b] + sign_ext(off16)");
    }

    @Override
    public int getSelfStack(byte[] bytes) {

        String registerString = DataTypes.byteToString(bytes[2]);
        String a = registerString.substring(0, 4);
        String b = registerString.substring(4, 8);
        int aValue = Integer.parseInt(a, 2);
        int bValue = Integer.parseInt(b, 2);
        if (aValue != 10 || bValue != 10)
            return -1;

        String offsetString = DataTypes.byteToString(bytes[0]) + DataTypes.byteToString(bytes[1]);
        String offset = offsetString.substring(4, 10) + offsetString.substring(0, 4) + offsetString.substring(10, 16);
        int offsetValue = Integer.parseInt(offset, 2);
        if (offset.startsWith("1"))
            offsetValue = 0x10000 - offsetValue;
        return offsetValue;
    }
}
