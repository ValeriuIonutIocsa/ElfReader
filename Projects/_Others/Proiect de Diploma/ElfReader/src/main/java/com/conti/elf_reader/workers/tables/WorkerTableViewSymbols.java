package com.conti.elf_reader.workers.tables;

import com.conti.elf_reader.data_info.tables.DataInfoSymbols;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.settings.Settings;

import java.nio.file.Path;
import java.util.Collection;

public class WorkerTableViewSymbols extends WorkerTableView {

    public WorkerTableViewSymbols(ElfFile elfFile, Path outputFilePath) {
        super(DataInfoSymbols.getInstance(), elfFile, outputFilePath);
    }

    @Override
    public Collection<? extends DataElementTableViewRow> getDataElementTableViewRowList(Settings settings) {
        return elfFile.getSymbolTableEntries();
    }
}
