package com.conti.elf_reader.gui.tables.call_tree_tables.runnables;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeRunnables;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfoRunnables;
import com.utils.log.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;

import java.util.List;

public class CustomTableCellRecursivePath extends TableCell<DataElementTableViewRow, Object> {

    @Override
    protected void updateItem(Object item, boolean empty) {

        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setContextMenu(null);
            return;
        }

        if (item != null) {
            setGraphic((ImageView) item);
        } else {
            setGraphic(null);
        }

        ContextMenu contextMenu = createContextMenu();
        setContextMenu(contextMenu);
    }

    private ContextMenu createContextMenu() {

        DataElementCallTreeRunnables dataElement = (DataElementCallTreeRunnables) getTableRow().getItem();
        if (dataElement == null)
            return null;

        CallTreeAdditionalInfoRunnables additionalInfo = dataElement.getAdditionalInfo();
        if (!additionalInfo.isRecursive())
            return null;

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemNavigateTo = new MenuItem("show recursive path in console");
        menuItemNavigateTo.setOnAction(event -> {

            List<String> recursivePaths = additionalInfo.getRecursivePaths();
            if (recursivePaths == null)
                return;

            StringBuilder stringBuilder = new StringBuilder();

            for (String recursivePathString : recursivePaths) {
                String[] recursivePathSplit = recursivePathString.split("::", 0);
                for (int i = 0; i < recursivePathSplit.length; i++) {
                    String recursivePathElement = recursivePathSplit[i];
                    stringBuilder.append(System.lineSeparator())
                            .append(new String(new char[i]).replace("\0", "   "))
                            .append("-> ").append(recursivePathElement);
                }
                stringBuilder.append(System.lineSeparator());
            }

            Logger.printLine(System.lineSeparator() + "Recursive path for: " + dataElement + stringBuilder);
        });
        contextMenu.getItems().add(menuItemNavigateTo);
        return contextMenu;
    }
}
