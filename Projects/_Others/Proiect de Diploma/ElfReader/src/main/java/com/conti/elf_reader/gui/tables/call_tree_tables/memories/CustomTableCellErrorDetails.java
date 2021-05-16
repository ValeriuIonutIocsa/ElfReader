package com.conti.elf_reader.gui.tables.call_tree_tables.memories;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.ErrorDetails;
import com.utils.log.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;

public class CustomTableCellErrorDetails extends TableCell<DataElementTableViewRow, Object> {

    @Override
    protected void updateItem(Object item, boolean empty) {

        super.updateItem(item, empty);

        if (empty) {
            setText("");
            setContextMenu(null);
            return;
        }

        String text = item != null ? item.toString() : "";
        setText(text);

        ContextMenu contextMenu = createContextMenu();
        setContextMenu(contextMenu);
    }

    private ContextMenu createContextMenu() {

        Object item = getItem();
        if (item == null)
            return null;

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemNavigateTo = new MenuItem("expand in console");
        menuItemNavigateTo.setOnAction(event -> {

            String expandInConsoleString = ((ErrorDetails) item).createExpandInConsoleString();
            Logger.printLine(System.lineSeparator() + "ErrorDetails for: " + getTableRow().getItem() +
                    System.lineSeparator() + expandInConsoleString);
        });
        contextMenu.getItems().add(menuItemNavigateTo);
        return contextMenu;
    }
}
