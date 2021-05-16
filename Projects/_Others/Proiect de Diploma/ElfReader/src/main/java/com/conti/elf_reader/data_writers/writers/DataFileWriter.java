package com.conti.elf_reader.data_writers.writers;

import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.utils.log.Logger;

import java.nio.file.Path;

public abstract class DataFileWriter {

    final Path outputPath;
    final DataInfoTable dataInfoTable;

    DataFileWriter(Path outputPath, DataInfoTable dataInfoTable) {

        this.outputPath = outputPath;
        this.dataInfoTable = dataInfoTable;
    }

    public void write() throws Exception {

        try {
            writeData();

        } catch (Exception ignored) {
            Logger.printError("failed to generate the data file:"
                    + System.lineSeparator() + outputPath.toAbsolutePath());
            throw new Exception();
        }
    }

    abstract void writeData() throws Exception;
}
