package com.conti.elf_reader.data_info;

import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.workers.Worker;

import java.nio.file.Path;

public abstract class DataInfo {

    public abstract String getOption();

    public abstract String getTabName();

    public abstract Worker createWorker(ElfFile elfFile, Path outputPath);
}
