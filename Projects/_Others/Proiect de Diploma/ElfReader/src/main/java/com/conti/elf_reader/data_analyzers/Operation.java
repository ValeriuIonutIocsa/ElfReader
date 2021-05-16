package com.conti.elf_reader.data_analyzers;

import com.conti.elf_reader.utils.data_types.DataTypes;

public abstract class Operation {

    private final String name;
    private final String pattern;
    private final String formula;

    protected Operation(String name, String pattern, String formula) {

        this.name = name;
        this.pattern = pattern;
        this.formula = formula;
    }

    public boolean doesNotMatch(byte[] bytes) {

        String bytesToString = DataTypes.bytesToString(bytes);
        if (bytesToString.length() != pattern.length())
            return true;

        for (int i = 0; i < pattern.length(); i++) {

            char patternChar = pattern.charAt(i);
            if (patternChar == '*')
                continue;

            char bytesStringChar = bytesToString.charAt(i);
            if (patternChar != bytesStringChar)
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return System.lineSeparator() + "\tname: " + String.format("%-20s", name) +
                String.format("%-45s", "pattern: " + pattern) + "formula: " + formula + System.lineSeparator();
    }

    public String getName() {
        return name;
    }
}
