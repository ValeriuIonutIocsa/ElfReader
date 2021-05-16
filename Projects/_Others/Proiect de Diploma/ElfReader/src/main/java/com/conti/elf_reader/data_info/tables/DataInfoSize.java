package com.conti.elf_reader.data_info.tables;

import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.tables.PaneTabTableSize;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import com.conti.elf_reader.workers.tables.data_analyzers.WorkerDataAnalyzerSize;

import java.nio.file.Path;

public class DataInfoSize extends DataInfoTable {

    private static DataInfoSize instance;

    public static DataInfoSize getInstance() {

        if (instance == null) {
            instance = new DataInfoSize();
        }
        return instance;
    }

    private DataInfoSize() {
    }

    @Override
    public String getOption() {
        return "-size";
    }

    @Override
    public String getTabName() {
        return "Size";
    }

    @Override
    public WorkerTableView createWorkerTableView(ElfFile elfFile, Path outputFilePath, boolean verbose) {
        return new WorkerDataAnalyzerSize(elfFile, outputFilePath, verbose);
    }

    @Override
    public PaneTabTable createPaneTab(Settings settings) {
        return new PaneTabTableSize(settings);
    }

    @Override
    public TableViewColumnInfo[] getColumnInfoArray() {
        return new TableViewColumnInfo[]{
                new TableViewColumnInfo("No.", 0.06),
                new TableViewColumnInfo("SecName", 0.18),
                new TableViewColumnInfo("SecAddr", 0.07),
                new TableViewColumnInfo("SecAlign", 0.06),
                new TableViewColumnInfo("SecSize", 0.06),
                new TableViewColumnInfo("SymName", 0.18),
                new TableViewColumnInfo("SymAddr", 0.07),
                new TableViewColumnInfo("Memory", 0.08),
                new TableViewColumnInfo("SymFile", 0.07),
                new TableViewColumnInfo("SymSize", 0.06),
                new TableViewColumnInfo("SymInfo", 0.11)
        };
    }

    @Override
    public String getRootElementTagName() {
        return "Size";
    }

    @Override
    public String getDataElementTagName() {
        return "SizeDetails";
    }
}
