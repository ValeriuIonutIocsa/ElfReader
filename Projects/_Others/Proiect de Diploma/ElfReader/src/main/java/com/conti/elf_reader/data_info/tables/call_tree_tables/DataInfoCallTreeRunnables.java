package com.conti.elf_reader.data_info.tables.call_tree_tables;

import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.tables.call_tree_tables.runnables.PaneTabCallTreeRunnables;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import com.conti.elf_reader.workers.tables.data_analyzers.WorkerDataAnalyzerCallTreeRunnables;

import java.nio.file.Path;

public class DataInfoCallTreeRunnables extends DataInfoCallTree {

    private static DataInfoCallTreeRunnables instance;

    public static DataInfoCallTreeRunnables getInstance() {

        if (instance == null) {
            instance = new DataInfoCallTreeRunnables();
        }
        return instance;
    }

    private DataInfoCallTreeRunnables() {
    }

    @Override
    public String getOption() {
        return "-call_tree_runnables";
    }

    @Override
    public String getTabName() {
        return "Runnables";
    }

    @Override
    public WorkerTableView createWorkerTableView(ElfFile elfFile, Path outputFilePath, boolean verbose) {
        return new WorkerDataAnalyzerCallTreeRunnables(elfFile, outputFilePath);
    }

    @Override
    public PaneTabTable createPaneTab(Settings settings) {
        return new PaneTabCallTreeRunnables(settings);
    }

    @Override
    public TableViewColumnInfo[] getColumnInfoArray() {
        return new TableViewColumnInfo[]{
                new TableViewColumnInfo("No.", 0.06),
                new TableViewColumnInfo("Function", 0.36),
                new TableViewColumnInfo("SelfStack", 0.1),
                new TableViewColumnInfo("Reachable", 0.1),
                new TableViewColumnInfo("Recursive", 0.1),
                new TableViewColumnInfo("IsTask", 0.1),
                new TableViewColumnInfo("Calls", 0.09),
                new TableViewColumnInfo("CalledBy", 0.09)
        };
    }

    @Override
    public String getRootElementTagName() {
        return "CallTreeRunnables";
    }
}
