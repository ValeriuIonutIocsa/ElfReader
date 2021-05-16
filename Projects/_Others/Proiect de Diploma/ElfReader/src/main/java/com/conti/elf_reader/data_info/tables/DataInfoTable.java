package com.conti.elf_reader.data_info.tables;

import com.conti.elf_reader.data_info.DataInfo;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.Worker;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import javafx.scene.image.Image;

import java.nio.file.Path;

public abstract class DataInfoTable extends DataInfo {

    @Override
    public Worker createWorker(ElfFile elfFile, Path outputPath) {
        return createWorkerTableView(elfFile, outputPath, false);
    }

    public abstract WorkerTableView createWorkerTableView(ElfFile elfFile, Path outputFilePath, boolean verbose);

    public abstract PaneTabTable createPaneTab(Settings settings);

    public Image getTabImage() {
        return GuiUtils.imageTableStructure;
    }

    public abstract TableViewColumnInfo[] getColumnInfoArray();

    public abstract String getRootElementTagName();

    public abstract String getDataElementTagName();

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof DataInfo))
            return false;

        DataInfo dataInfo = (DataInfo) o;
        return getOption() != null && getOption().equals(dataInfo.getOption());
    }
}
