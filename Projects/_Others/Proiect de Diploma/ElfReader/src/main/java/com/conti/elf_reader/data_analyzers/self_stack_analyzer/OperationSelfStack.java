package com.conti.elf_reader.data_analyzers.self_stack_analyzer;

import com.conti.elf_reader.data_analyzers.Operation;

public abstract class OperationSelfStack extends Operation {

    protected OperationSelfStack(String name, String pattern, String formula){
        super(name, pattern, formula);
    }

    public abstract int getSelfStack(byte[] bytes);
}
