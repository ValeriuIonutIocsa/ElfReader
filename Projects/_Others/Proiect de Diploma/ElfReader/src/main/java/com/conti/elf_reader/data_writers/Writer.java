package com.conti.elf_reader.data_writers;

import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.data_writers.writers.DataFileWriter;
import com.conti.elf_reader.data_writers.writers.DataFileWriterCsv;
import com.conti.elf_reader.data_writers.writers.DataFileWriterXlsx;
import com.conti.elf_reader.data_writers.writers.DataFileWriterXml;
import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.utils.log.Logger;

import java.nio.file.Path;
import java.util.Collection;

public class Writer {

    public static void write(
            Path outputPath, DataInfoTable dataInfoTable, Collection<? extends DataElementTableViewRow> tableViewItems) {

        final DataFileWriter dataFileWriter;
        String outputPathString = outputPath.toAbsolutePath().toString();
        if (outputPathString.endsWith(".xml")) {
            dataFileWriter = new DataFileWriterXml(outputPath, dataInfoTable, tableViewItems);

        } else if (outputPathString.endsWith(".csv")) {
            dataFileWriter = new DataFileWriterCsv(outputPath, dataInfoTable, tableViewItems);

        } else if (outputPathString.endsWith(".xlsx")) {
            dataFileWriter = new DataFileWriterXlsx(outputPath, dataInfoTable, tableViewItems);
        } else {
            dataFileWriter = null;
        }

        if (dataFileWriter == null) {
            Logger.printWarning("invalid extension of the output path:"
                    + System.lineSeparator() + outputPath
                    + System.lineSeparator() + "supported extensions: .xml, .csv and .xlsx");
            return;
        }

        try {
            Logger.printProgress("generating the output file...");
            dataFileWriter.write();
            Logger.printStatus("The output file was successfully generated:"
                    + System.lineSeparator() + outputPath);

        } catch (Exception ignored) {
            Logger.printException(ignored);
            Logger.printError("failed to generate the output!");
        }
    }
}
