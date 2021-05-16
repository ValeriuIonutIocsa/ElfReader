package com.conti.elf_reader.gui.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

public class ChangeListenerTextFieldPath implements ChangeListener<String> {

    private final Control control;

    public ChangeListenerTextFieldPath(Control control) {
        this.control = control;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

        Tooltip tooltip = newValue.trim().isEmpty() ? null : new Tooltip(newValue);
        control.setTooltip(tooltip);
    }
}
