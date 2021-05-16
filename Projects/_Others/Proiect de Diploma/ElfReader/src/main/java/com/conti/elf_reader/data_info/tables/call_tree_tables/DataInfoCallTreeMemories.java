package com.conti.elf_reader.data_info.tables.call_tree_tables;

import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.tables.call_tree_tables.memories.PaneTabCallTreeMemories;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import com.conti.elf_reader.workers.tables.data_analyzers.WorkerDataAnalyzerCallTreeMemories;

import java.nio.file.Path;

public class DataInfoCallTreeMemories extends DataInfoCallTree {

    private static DataInfoCallTreeMemories instance;

    public static DataInfoCallTreeMemories getInstance() {

        if (instance == null) {
            instance = new DataInfoCallTreeMemories();
        }
        return instance;
    }

    private DataInfoCallTreeMemories() {
    }

    @Override
    public String getOption() {
        return "-call_tree_memories";
    }

    @Override
    public String getTabName() {
        return "Memories";
    }

    @Override
    public WorkerTableView createWorkerTableView(ElfFile elfFile, Path outputFilePath, boolean verbose) {
        return new WorkerDataAnalyzerCallTreeMemories(elfFile, outputFilePath);
    }

    @Override
    public PaneTabTable createPaneTab(Settings settings) {
        return new PaneTabCallTreeMemories(settings);
    }

    @Override
    public TableViewColumnInfo[] getColumnInfoArray() {
        return new TableViewColumnInfo[]{
                new TableViewColumnInfo("No.", 0.06),
                new TableViewColumnInfo("Function", 0.2),
                new TableViewColumnInfo("SourceFile", 0.11),
                new TableViewColumnInfo("Address", 0.1),
                new TableViewColumnInfo("Memory", 0.1),
                new TableViewColumnInfo("Protection", 0.1),
                new TableViewColumnInfo("Error", 0.06),
                new TableViewColumnInfo("ErrorDetails", 0.1),
                new TableViewColumnInfo("Calls", 0.09),
                new TableViewColumnInfo("CalledBy", 0.09)
        };
    }

    @Override
    public String getRootElementTagName() {
        return "CallTreeMemories";
    }
}
