package com.conti.elf_reader.workers.tables;

import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_writers.writers.DataFileWriterXml;
import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.settings.Settings;
import com.utils.log.Logger;
import com.conti.elf_reader.workers.Worker;

import java.nio.file.Path;
import java.util.Collection;

public abstract class WorkerTableView extends Worker {

    private final DataInfoTable dataInfoTable;
    protected final ElfFile elfFile;
    private final Path outputFilePath;

    protected WorkerTableView(DataInfoTable dataInfoTable, ElfFile elfFile, Path outputFilePath) {

        this.dataInfoTable = dataInfoTable;
        this.elfFile = elfFile;
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void generateDataFile(Settings settings) {

        try {
            Collection<? extends DataElementTableViewRow> dataElementTableViewRowList =
                    getDataElementTableViewRowList(settings);

            Logger.printProgress("generating the " + dataInfoTable.getTabName() + " report...");
            DataFileWriterXml dataFileWriterXml = new DataFileWriterXml(
                    outputFilePath, dataInfoTable, dataElementTableViewRowList);
            dataFileWriterXml.write();
            Logger.printStatus("Finished generating the " + dataInfoTable.getTabName() + " report.");

        } catch (Exception exc) {
            Logger.printException(exc);
            Logger.printError("failed to generate the output!");
        }
    }

    public abstract Collection<? extends DataElementTableViewRow> getDataElementTableViewRowList(Settings settings);
}
