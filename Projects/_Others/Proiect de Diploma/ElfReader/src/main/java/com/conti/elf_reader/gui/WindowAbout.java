package com.conti.elf_reader.gui;

import com.conti.elf_reader.gui.utils.GuiUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

class WindowAbout extends Stage {

    private final static String appTitle = "Elf Reader";
    final static double appVersion = 1.2;

    WindowAbout() {

        super();

        initialize();
    }

    private void initialize() {

        setTitle("About");
        GuiUtils.setAppIcon(this);
        setAlwaysOnTop(true);
        initModality(Modality.APPLICATION_MODAL);

        setX(100);
        setY(100);
        setWidth(400);
        setHeight(250);

        MigPane primaryPane = new MigPane();

        MigPane migPaneTitle = createMigPaneTitle();
        primaryPane.add(migPaneTitle, "width 100%, gaptop 10, wrap");

        primaryPane.add(GuiUtils.createLabel(""), "width 100%, height 40%, gap 10, wrap");

        Button okButton = GuiUtils.createButton("OK");
        okButton.setPrefWidth(56);
        okButton.setPrefHeight(40);
        okButton.setOnAction(event -> close());
        primaryPane.add(okButton, "center, height 20%");

        Scene scene = new Scene(primaryPane);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                close();
            }
        });
        scene.getStylesheets().add("gui/style_about.css");
        setScene(scene);

        okButton.requestFocus();
        show();
    }

    private MigPane createMigPaneTitle() {

        MigPane migPaneTitle = new MigPane(new LC().insets("0"));

        ImageView imageView = new ImageView(GuiUtils.imageApp);
        migPaneTitle.add(imageView, "gap 10");

        Label lblAbout = GuiUtils.createLabel(getAppTitleAndVersion());
        lblAbout.setId("lblAbout");
        migPaneTitle.add(lblAbout, "push");

        return migPaneTitle;
    }

    static String getAppTitleAndVersion() {
        return appTitle + " v" + appVersion;
    }
}