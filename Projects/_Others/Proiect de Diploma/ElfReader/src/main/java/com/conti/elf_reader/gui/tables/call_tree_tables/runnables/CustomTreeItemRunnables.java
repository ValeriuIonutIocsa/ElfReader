package com.conti.elf_reader.gui.tables.call_tree_tables.runnables;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import javafx.scene.control.TreeItem;

public class CustomTreeItemRunnables extends TreeItem<DataElementCallTree> {

    private boolean selected;

    CustomTreeItemRunnables(DataElementCallTree dataElementCallTree){
        super(dataElementCallTree);
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
