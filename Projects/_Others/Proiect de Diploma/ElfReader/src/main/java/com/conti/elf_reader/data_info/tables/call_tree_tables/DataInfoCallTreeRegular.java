package com.conti.elf_reader.data_info.tables.call_tree_tables;

import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.tables.call_tree_tables.regular.PaneTabCallTreeRegular;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import com.conti.elf_reader.workers.tables.data_analyzers.WorkerDataAnalyzerCallTreeRegular;

import java.nio.file.Path;

public class DataInfoCallTreeRegular extends DataInfoCallTree {

    private static DataInfoCallTreeRegular instance;

    public static DataInfoCallTreeRegular getInstance() {

        if (instance == null) {
            instance = new DataInfoCallTreeRegular();
        }
        return instance;
    }

    private DataInfoCallTreeRegular() {
    }

    @Override
    public String getOption() {
        return "-call_tree_regular";
    }

    @Override
    public String getTabName() {
        return "Call Tree";
    }

    @Override
    public WorkerTableView createWorkerTableView(ElfFile elfFile, Path outputFilePath, boolean verbose) {
        return new WorkerDataAnalyzerCallTreeRegular(elfFile, outputFilePath);
    }

    @Override
    public PaneTabTable createPaneTab(Settings settings) {
        return new PaneTabCallTreeRegular(settings);
    }

    @Override
    public TableViewColumnInfo[] getColumnInfoArray() {
        return new TableViewColumnInfo[]{
                new TableViewColumnInfo("No.", 0.06),
                new TableViewColumnInfo("Function", 0.64),
                new TableViewColumnInfo("Address", 0.12),
                new TableViewColumnInfo("Calls", 0.09),
                new TableViewColumnInfo("CalledBy", 0.09)
        };
    }

    @Override
    public String getRootElementTagName() {
        return "CallTreeRegular";
    }
}
