package com.conti.elf_reader.utils.regex;

import java.util.*;

public class FilterPatterns {

    public final static String helpTextPatterns = "Inclusion patterns:" +
            System.lineSeparator() + "  Contains:       *text*" +
            System.lineSeparator() + "  Starts With:    text* " +
            System.lineSeparator() + "  Ends With:      *text " +
            System.lineSeparator() + System.lineSeparator() + "Exclusion patterns:" +
            System.lineSeparator() + "  Contains:       #text#" +
            System.lineSeparator() + "  Starts With:    text# " +
            System.lineSeparator() + "  Ends With:      #text ";

    public final static String helpTextParsing = "the patterns cannot contain spaces" +
            System.lineSeparator() + System.lineSeparator() + "between patterns on the same line" +
            System.lineSeparator() + "separated by space or tab" +
            System.lineSeparator() + System.lineSeparator() + "the OR operator will be applied" +
            System.lineSeparator() + "between the lists of patterns on different lines" +
            System.lineSeparator() + "(shows only what matches at least one of the patterns)" +
            System.lineSeparator() + System.lineSeparator() + "the AND operator will be applied" +
            System.lineSeparator() + "between the patterns on the same lines" +
            System.lineSeparator() + "(shows only what matches all of the patterns)";

    public static List<String[]> parseFilterPatterns(String filterPatternsText) {

        if (filterPatternsText.trim().isEmpty())
            return null;

        List<String[]> patterns = new ArrayList<>();
        String[] linesSplit = filterPatternsText.split("\\r\\n|\\n|\\r", 0);
        for (String patternLine : linesSplit) {
            patterns.add(patternLine.split("\\s+", 0));
        }
        return patterns;
    }

    public static boolean matchesPatterns(String text, List<String[]> patterns, boolean caseSensitive) {

        if (text == null)
            return false;

        if (patterns == null)
            return true;

        for (String[] andPatterns : patterns) {
            if (matchesAndPatterns(text, andPatterns, caseSensitive))
                return true;
        }
        return false;
    }

    private static boolean matchesAndPatterns(String text, String[] andPatterns, boolean caseSensitive) {

        for (String pattern : andPatterns) {
            if (!matchesPattern(text, pattern, caseSensitive))
                return false;
        }
        return true;
    }

    public static boolean matchesPattern(String text, String pattern, boolean caseSensitive) {

        if (!caseSensitive) {
            text = text.toLowerCase();
            pattern = pattern.toLowerCase();
        }

        int length = pattern.length();

        String actualPattern;
        if (pattern.startsWith("*") && pattern.endsWith("*")) {
            actualPattern = pattern.substring(1, length - 1);
            return text.contains(actualPattern);

        } else if (pattern.startsWith("*")) {
            actualPattern = pattern.substring(1);
            return text.endsWith(actualPattern);

        } else if (pattern.endsWith("*")) {
            actualPattern = pattern.substring(0, length - 1);
            return text.startsWith(actualPattern);

        } else if (pattern.startsWith("#") && pattern.endsWith("#")) {
            actualPattern = pattern.substring(1, length - 1);
            return !text.contains(actualPattern);

        } else if (pattern.startsWith("#")) {
            actualPattern = pattern.substring(1);
            return !text.endsWith(actualPattern);

        } else if (pattern.endsWith("#")) {
            actualPattern = pattern.substring(0, length - 1);
            return !text.startsWith(actualPattern);
        }
        return false;
    }
}
