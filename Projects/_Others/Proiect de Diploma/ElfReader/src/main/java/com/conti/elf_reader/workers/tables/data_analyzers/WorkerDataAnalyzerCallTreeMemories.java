package com.conti.elf_reader.workers.tables.data_analyzers;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.DataAnalyzerCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.DataAnalyzerCallTreeMemories;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeMemories;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.settings.Settings;

import java.nio.file.Path;

public class WorkerDataAnalyzerCallTreeMemories extends WorkerDataAnalyzer {

    public WorkerDataAnalyzerCallTreeMemories(ElfFile elfFile, Path outputFilePath) {
        super(DataInfoCallTreeMemories.getInstance(), elfFile, outputFilePath, false);
    }

    @Override
    DataAnalyzerCallTree createDataAnalyzer(Settings settings) {
        return new DataAnalyzerCallTreeMemories(settings, elfFile);
    }
}
