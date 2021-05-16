package com.conti.elf_reader.data_analyzers.call_tree_analyzer.powerpc_operations;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.OperationCallTree;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class OperationBc extends OperationCallTree {

    public OperationBc() {
        super(
                "bc (b cond)",
                DataTypes.intToPaddedBinaryString(16, 6) + DataTypes.start32String.substring(6, 32),
                "AA = 30, BD = [16:29], if AA then NIA <-- sign_ext(BD || 0b00) else NIA <-- CIA + sign_ext(BD || 0b00)");
    }

    @Override
    public long getCalledFunctionAddress(long address, byte[] bytes) {

        String bytesString = DataTypes.bytesToString(bytes);

        char aa = bytesString.charAt(30);

        String liString = bytesString.substring(16, 30);

        long li = Integer.parseInt(liString, 2) | 0x0b00;
        if (aa == '0') {
            li += address;
        }
        return li;
    }
}
