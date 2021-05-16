package com.conti.elf_reader.data_parsers.indirect_calls;

import com.conti.elf_reader.data_parsers.indirect_calls.data.IndirectCallReplacement;
import com.utils.log.Logger;
import com.utils.io.IoUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IndirectCalls {

    public static void parse(
            Path indirectCallsReplacementsFilePath,
            Map<IndirectCallReplacement, IndirectCallReplacement> indirectCallReplacementMap) {

        try {
            if (!IoUtils.fileExists(indirectCallsReplacementsFilePath))
                return;

            indirectCallReplacementMap.clear();
            final List<String> lines = IoUtils.fileLines(indirectCallsReplacementsFilePath);
            for (String line : lines) {

                final IndirectCallReplacement indirectCallReplacement = IndirectCallReplacement.parse(line);
                if (indirectCallReplacement != null) {
                    indirectCallReplacementMap.putIfAbsent(indirectCallReplacement, indirectCallReplacement);
                }
            }

        } catch (Exception exc) {
            Logger.printException(exc);
            Logger.printError("failed to parse the indirect call replacements file:" +
                    System.lineSeparator() + indirectCallsReplacementsFilePath);
        }
    }

    public static void save(
            Path indirectCallsReplacementsFilePath, Collection<IndirectCallReplacement> indirectCallReplacements) {

        try {
            if (!IoUtils.fileExists(indirectCallsReplacementsFilePath)) {
                IoUtils.createDirectories(indirectCallsReplacementsFilePath.getParent());
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (IndirectCallReplacement indirectCallReplacement : indirectCallReplacements) {

                stringBuilder
                        .append(indirectCallReplacement.getPathInTree()).append(',')
                        .append(indirectCallReplacement.getIndexInCallsList()).append(',')
                        .append(indirectCallReplacement.getReplacementValue()).append(System.lineSeparator());
            }
            Files.write(indirectCallsReplacementsFilePath, stringBuilder.toString().getBytes());

        } catch (Exception exc) {
            Logger.printException(exc);
            Logger.printError("failed to parse the indirect call replacements file:" +
                    System.lineSeparator() + indirectCallsReplacementsFilePath);
        }
    }
}
