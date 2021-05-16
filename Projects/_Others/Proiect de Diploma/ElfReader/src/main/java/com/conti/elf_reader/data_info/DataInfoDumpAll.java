package com.conti.elf_reader.data_info;

import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.workers.Worker;
import com.conti.elf_reader.workers.WorkerDumpAll;

import java.nio.file.Path;

public class DataInfoDumpAll extends DataInfo {

    private static DataInfoDumpAll instance;

    public static DataInfo getInstance() {

        if (instance == null) {
            instance = new DataInfoDumpAll();
        }
        return instance;
    }

    private DataInfoDumpAll() {
    }

    @Override
    public String getOption() {
        return "-dump_all";
    }

    @Override
    public String getTabName() {
        return "Dump All";
    }

    @Override
    public Worker createWorker(ElfFile elfFile, Path outputPath) {
        return new WorkerDumpAll(elfFile, outputPath);
    }
}
