package com.conti.elf_reader.data_info;

import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.workers.Worker;
import com.conti.elf_reader.workers.WorkerSymbolsToSectionsMap;

import java.nio.file.Path;

public class DataInfoSymbolsToSectionsMap extends DataInfo {

    private static DataInfoSymbolsToSectionsMap instance;

    public static DataInfo getInstance() {

        if (instance == null) {
            instance = new DataInfoSymbolsToSectionsMap();
        }
        return instance;
    }

    private DataInfoSymbolsToSectionsMap() {
    }

    @Override
    public String getOption() {
        return "-symbols_to_sections_map";
    }

    @Override
    public String getTabName() {
        return "Symbol to Sections Map";
    }

    @Override
    public Worker createWorker(ElfFile elfFile, Path outputPath) {
        return new WorkerSymbolsToSectionsMap(outputPath);
    }
}
