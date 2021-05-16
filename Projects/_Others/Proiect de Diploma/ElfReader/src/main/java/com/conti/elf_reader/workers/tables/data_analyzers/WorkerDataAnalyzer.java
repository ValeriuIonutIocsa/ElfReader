package com.conti.elf_reader.workers.tables.data_analyzers;

import com.conti.elf_reader.data_analyzers.DataAnalyzer;
import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.tables.WorkerTableView;

import java.nio.file.Path;
import java.util.Collection;

public abstract class WorkerDataAnalyzer extends WorkerTableView {

    protected final boolean verbose;

    WorkerDataAnalyzer(DataInfoTable dataInfoTable, ElfFile elfFile, Path outputFilePath, boolean verbose) {

        super(dataInfoTable, elfFile, outputFilePath);

        this.verbose = verbose;
    }

    @Override
    public Collection<? extends DataElementTableViewRow> getDataElementTableViewRowList(Settings settings) {

        DataAnalyzer dataAnalyzer = createDataAnalyzer(settings);
        dataAnalyzer.analyze(verbose);
        return dataAnalyzer.getDataElementTableViewRowList();
    }

    abstract DataAnalyzer createDataAnalyzer(Settings settings);
}
