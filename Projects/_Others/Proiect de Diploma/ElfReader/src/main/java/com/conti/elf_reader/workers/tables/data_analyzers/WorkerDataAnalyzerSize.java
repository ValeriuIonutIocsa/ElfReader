package com.conti.elf_reader.workers.tables.data_analyzers;

import com.conti.elf_reader.data_analyzers.DataAnalyzer;
import com.conti.elf_reader.data_analyzers.size_analyzer.DataAnalyzerSize;
import com.conti.elf_reader.data_info.tables.DataInfoSize;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.settings.Settings;

import java.nio.file.Path;

public class WorkerDataAnalyzerSize extends WorkerDataAnalyzer {

    public WorkerDataAnalyzerSize(ElfFile elfFile, Path outputFilePath, boolean verbose) {
        super(DataInfoSize.getInstance(), elfFile, outputFilePath, verbose);
    }

    @Override
    DataAnalyzer createDataAnalyzer(Settings settings) {
        return new DataAnalyzerSize(settings, elfFile);
    }
}
