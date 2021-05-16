package com.conti.elf_reader.gui.utils;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FilterComboBox<T> extends ComboBox<T> {

    private final ObservableList<T> items;

    public FilterComboBox() {

        super();
        this.items = getItems();

        setOnKeyReleased(new KeyHandler());
    }

    private class KeyHandler implements EventHandler<KeyEvent> {

        private final SingleSelectionModel<T> singleSelectionModel;
        private String typedString;

        KeyHandler() {

            singleSelectionModel = getSelectionModel();
            typedString = "";
        }

        @Override
        public void handle(KeyEvent event) {

            KeyCode keyCode = event.getCode();

            if (keyCode == KeyCode.DELETE) {
                typedString = "";

            } else if (keyCode == KeyCode.BACK_SPACE && typedString.length() > 0) {
                typedString = typedString.substring(0, typedString.length() - 1);

            } else if (keyCode != KeyCode.TAB) {
                typedString += event.getText();
            }

            if (typedString.length() == 0) {
                singleSelectionModel.selectFirst();
                return;
            }

            for (T item : items) {

                String displayString = item.toString().toLowerCase();
                if (displayString.contains(this.typedString)) {
                    singleSelectionModel.select(item);
                    return;
                }
            }
        }
    }
}
