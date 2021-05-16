package com.conti.elf_reader.data_analyzers;

import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.settings.Settings;

import java.util.Collection;

public abstract class DataAnalyzer {

    protected final Settings settings;
    protected final ElfFile elfFile;

    public DataAnalyzer(Settings settings, ElfFile elfFile) {

        this.settings = settings;
        this.elfFile = elfFile;
    }

    public abstract Collection<? extends DataElementTableViewRow> getDataElementTableViewRowList();

    public abstract void analyze(boolean verbose);
}
