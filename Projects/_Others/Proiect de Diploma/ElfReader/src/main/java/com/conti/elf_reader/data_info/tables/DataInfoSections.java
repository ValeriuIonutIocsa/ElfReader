package com.conti.elf_reader.data_info.tables;

import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.tables.PaneTabTableSections;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import com.conti.elf_reader.workers.tables.WorkerTableViewSections;

import java.nio.file.Path;

public class DataInfoSections extends DataInfoTable {

    private static DataInfoSections instance;

    public static DataInfoSections getInstance() {

        if (instance == null) {
            instance = new DataInfoSections();
        }
        return instance;
    }

    private DataInfoSections() {
    }

    @Override
    public String getOption() {
        return "-sections";
    }

    @Override
    public String getTabName() {
        return "Sections";
    }

    @Override
    public WorkerTableView createWorkerTableView(ElfFile elfFile, Path outputFilePath, boolean verbose) {
        return new WorkerTableViewSections(elfFile, outputFilePath);
    }

    @Override
    public PaneTabTable createPaneTab(Settings settings) {
        return new PaneTabTableSections(settings);
    }

    @Override
    public TableViewColumnInfo[] getColumnInfoArray() {
        return new TableViewColumnInfo[]{
                new TableViewColumnInfo("No.", 0.06),
                new TableViewColumnInfo("Name", 0.36),
                new TableViewColumnInfo("Size", 0.1),
                new TableViewColumnInfo("Flags", 0.1),
                new TableViewColumnInfo("Addr", 0.1),
                new TableViewColumnInfo("Offset", 0.1),
                new TableViewColumnInfo("Align", 0.06),
                new TableViewColumnInfo("Type", 0.12)
        };
    }

    @Override
    public String getRootElementTagName() {
        return "section_header_entries";
    }

    @Override
    public String getDataElementTagName() {
        return "section";
    }
}
