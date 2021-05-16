package com.conti.elf_reader.gui.utils;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;

public class CustomTableView<T> extends TableView<T> {

    public CustomTableView(){

        setEditable(false);
        getSelectionModel().setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        widthProperty().addListener((source, oldWidth, newWidth) -> {

            TableHeaderRow header = (TableHeaderRow) lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });

        setOnKeyPressed(event -> {

            if (event.isControlDown() && event.getCode() == KeyCode.C) {

                getSelectionModel().getSelectedCells().stream().findFirst().ifPresent(tablePosition -> {

                    Object cellData = tablePosition.getTableColumn().getCellData(tablePosition.getRow());
                    String cellDataString = cellData != null ? cellData.toString() : "";
                    GuiUtils.putStringInClipBoard(cellDataString);
                });
            }
        });
    }
}
