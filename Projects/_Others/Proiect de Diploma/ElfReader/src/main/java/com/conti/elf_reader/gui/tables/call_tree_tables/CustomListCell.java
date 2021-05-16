package com.conti.elf_reader.gui.tables.call_tree_tables;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;

public class CustomListCell extends ListCell<DataElementCallTree> {

    private final PaneTabCallTree paneTabCallTree;

    CustomListCell(PaneTabCallTree paneTabCallTree) {

        this.paneTabCallTree = paneTabCallTree;
    }

    @Override
    public void updateItem(DataElementCallTree item, boolean empty) {

        super.updateItem(item, empty);

        if (empty) {
            setText("");
            setContextMenu(null);
            return;
        }

        setText(item != null ? item.toString() : "");

        ContextMenu contextMenu = createContextMenu();
        setContextMenu(contextMenu);
    }

    private ContextMenu createContextMenu() {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItemNavigateTo = new MenuItem("navigate to");
        menuItemNavigateTo.setOnAction(event ->
                paneTabCallTree.navigateTo(itemProperty().getValue().getFunctionName()));
        contextMenu.getItems().add(menuItemNavigateTo);

        return contextMenu;
    }
}
