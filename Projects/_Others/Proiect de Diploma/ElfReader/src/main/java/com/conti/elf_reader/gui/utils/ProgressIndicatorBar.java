package com.conti.elf_reader.gui.utils;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

public class ProgressIndicatorBar extends StackPane {

    public ProgressIndicatorBar() {

        Label label = GuiUtils.createLabel("loading...");
        label.setStyle("-fx-font-weight: bold");

        ProgressBar progressBar = new ProgressBar(-1);
        progressBar.setMinHeight(20);
        progressBar.setPrefWidth(Double.MAX_VALUE);

        progressBar.setStyle("-fx-accent: green;");

        getChildren().setAll(progressBar, label);
    }
}
