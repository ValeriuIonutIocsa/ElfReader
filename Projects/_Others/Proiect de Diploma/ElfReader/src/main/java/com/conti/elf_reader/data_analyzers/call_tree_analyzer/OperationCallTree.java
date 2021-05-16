package com.conti.elf_reader.data_analyzers.call_tree_analyzer;

import com.conti.elf_reader.data_analyzers.Operation;

public abstract class OperationCallTree extends Operation {

    protected OperationCallTree(String name, String pattern, String formula){
        super(name, pattern, formula);
    }

    public abstract long getCalledFunctionAddress(long address, byte[] bytes);
}
