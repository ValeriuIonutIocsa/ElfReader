package com.conti.elf_reader.data_info.tables;

import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.tables.PaneTabTableSelfStack;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import com.conti.elf_reader.workers.tables.data_analyzers.WorkerDataAnalyzerSelfStack;

import java.nio.file.Path;

public class DataInfoSelfStack extends DataInfoTable {

    private static DataInfoSelfStack instance;

    public static DataInfoSelfStack getInstance() {

        if (instance == null) {
            instance = new DataInfoSelfStack();
        }
        return instance;
    }

    private DataInfoSelfStack() {
    }

    @Override
    public String getOption() {
        return "-self_stack";
    }

    @Override
    public String getTabName() {
        return "Self Stack";
    }

    @Override
    public WorkerTableView createWorkerTableView(ElfFile elfFile, Path outputFilePath, boolean verbose) {
        return new WorkerDataAnalyzerSelfStack(elfFile, outputFilePath);
    }

    @Override
    public PaneTabTable createPaneTab(Settings settings) {
        return new PaneTabTableSelfStack(settings);
    }

    @Override
    public TableViewColumnInfo[] getColumnInfoArray() {
        return new TableViewColumnInfo[]{
                new TableViewColumnInfo("No.", 0.06),
                new TableViewColumnInfo("Function", 0.84),
                new TableViewColumnInfo("SelfStack", 0.1)
        };
    }

    @Override
    public String getRootElementTagName() {
        return "SelfStack";
    }

    @Override
    public String getDataElementTagName() {
        return "Function";
    }
}
