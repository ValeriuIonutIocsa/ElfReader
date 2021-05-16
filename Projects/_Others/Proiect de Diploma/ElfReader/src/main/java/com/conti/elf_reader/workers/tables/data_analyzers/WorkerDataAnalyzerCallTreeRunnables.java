package com.conti.elf_reader.workers.tables.data_analyzers;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.DataAnalyzerCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.DataAnalyzerCallTreeRunnables;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRunnables;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.settings.Settings;

import java.nio.file.Path;

public class WorkerDataAnalyzerCallTreeRunnables extends WorkerDataAnalyzer {

    public WorkerDataAnalyzerCallTreeRunnables(ElfFile elfFile, Path outputFilePath) {
        super(DataInfoCallTreeRunnables.getInstance(), elfFile, outputFilePath, false);
    }

    @Override
    DataAnalyzerCallTree createDataAnalyzer(Settings settings) {
        return new DataAnalyzerCallTreeRunnables(settings, elfFile);
    }
}
