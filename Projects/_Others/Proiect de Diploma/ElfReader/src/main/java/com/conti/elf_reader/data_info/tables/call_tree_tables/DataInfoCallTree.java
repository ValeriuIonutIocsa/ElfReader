package com.conti.elf_reader.data_info.tables.call_tree_tables;

import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.gui.utils.GuiUtils;
import javafx.scene.image.Image;

public abstract class DataInfoCallTree extends DataInfoTable {

    @Override
    public Image getTabImage() {
        return GuiUtils.imageTreeStructure;
    }

    @Override
    public String getDataElementTagName() {
        return "Function";
    }
}
