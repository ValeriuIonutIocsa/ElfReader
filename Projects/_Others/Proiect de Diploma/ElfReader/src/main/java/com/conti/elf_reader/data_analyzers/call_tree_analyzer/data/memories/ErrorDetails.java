package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories;

import java.util.*;

public class ErrorDetails {

    private final Map<ErrorType, List<String>> errorMap = new TreeMap<>(Comparator.reverseOrder());

    public void addError(ErrorType errorType, String errorString) {

        List<String> errors = errorMap.getOrDefault(errorType, null);
        if (errors == null) {
            errors = new ArrayList<>();
            errorMap.put(errorType, errors);
        }
        errors.add(errorString);
    }

    public String createExpandInConsoleString() {

        StringBuilder stringBuilder = new StringBuilder();

        for (ErrorType errorType : errorMap.keySet()) {

            List<String> errors = errorMap.get(errorType);
            int errorCount = errors.size();
            stringBuilder.append("   !!! ").append(errorCount).append(' ');
            stringBuilder.append(errorType.toString().toLowerCase());
            if (errorCount > 1) {
                stringBuilder.append('s');
            }
            stringBuilder.append(':');

            for (String error : errors) {
                stringBuilder.append(System.lineSeparator()).append(error);
            }
            stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        int size = errorMap.size();
        int i = 0;
        for (ErrorType errorType : errorMap.keySet()) {

            List<String> errors = errorMap.get(errorType);
            int errorCount = errors.size();
            stringBuilder.append(errorCount).append(' ');
            stringBuilder.append(errorType.toString().toLowerCase());
            if (errorCount > 1) {
                stringBuilder.append('s');
            }
            if (i < size - 1) {
                stringBuilder.append(", ");
            }
            i++;
        }
        return stringBuilder.toString();
    }
}
