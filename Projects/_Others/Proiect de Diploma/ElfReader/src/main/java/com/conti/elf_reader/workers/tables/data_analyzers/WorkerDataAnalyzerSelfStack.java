package com.conti.elf_reader.workers.tables.data_analyzers;

import com.conti.elf_reader.data_analyzers.DataAnalyzer;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.DataAnalyzerSelfStack;
import com.conti.elf_reader.data_info.tables.DataInfoSelfStack;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.settings.Settings;

import java.nio.file.Path;

public class WorkerDataAnalyzerSelfStack extends WorkerDataAnalyzer {

    public WorkerDataAnalyzerSelfStack(ElfFile elfFile, Path outputFilePath) {
        super(DataInfoSelfStack.getInstance(), elfFile, outputFilePath, false);
    }

    @Override
    DataAnalyzer createDataAnalyzer(Settings settings) {
        return new DataAnalyzerSelfStack(settings, elfFile);
    }
}
