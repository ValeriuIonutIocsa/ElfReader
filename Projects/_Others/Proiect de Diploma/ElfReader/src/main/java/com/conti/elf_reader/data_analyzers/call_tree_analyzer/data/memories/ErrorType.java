package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories;

public enum ErrorType {

    Info(1),
    Warning(2),
    Error(3);

    private int level;

    ErrorType(int level) {
        this.level = level;
    }

    public static boolean contains(String errorType) {

        for (ErrorType value : values()) {
            if (value.toString().equals(errorType)) {
                return true;
            }
        }
        return false;
    }

    public int getLevel() {
        return level;
    }
}

