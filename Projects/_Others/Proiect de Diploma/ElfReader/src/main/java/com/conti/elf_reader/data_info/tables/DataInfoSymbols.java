package com.conti.elf_reader.data_info.tables;

import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.tables.PaneTabTableSymbols;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import com.conti.elf_reader.workers.tables.WorkerTableViewSymbols;

import java.nio.file.Path;

public class DataInfoSymbols extends DataInfoTable {

    private static DataInfoSymbols instance;

    public static DataInfoSymbols getInstance() {

        if (instance == null) {
            instance = new DataInfoSymbols();
        }
        return instance;
    }

    private DataInfoSymbols() {
    }

    @Override
    public String getOption() {
        return "-symbols";
    }

    @Override
    public String getTabName() {
        return "Symbols";
    }

    @Override
    public WorkerTableView createWorkerTableView(ElfFile elfFile, Path outputFilePath, boolean verbose) {
        return new WorkerTableViewSymbols(elfFile, outputFilePath);
    }

    @Override
    public PaneTabTable createPaneTab(Settings settings) {
        return new PaneTabTableSymbols(settings);
    }

    @Override
    public TableViewColumnInfo[] getColumnInfoArray() {
        return new TableViewColumnInfo[]{
                new TableViewColumnInfo("No.", 0.06),
                new TableViewColumnInfo("Name", 0.4),
                new TableViewColumnInfo("Value", 0.12),
                new TableViewColumnInfo("Size", 0.12),
                new TableViewColumnInfo("Info", 0.12),
                new TableViewColumnInfo("Other", 0.06),
                new TableViewColumnInfo("SectionIndex", 0.12)
        };
    }

    @Override
    public String getRootElementTagName() {
        return "symbol";
    }

    @Override
    public String getDataElementTagName() {
        return "symbol_table_entries";
    }
}
