package com.conti.elf_reader.gui;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.DataAnalyzerCallTree;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.DataAnalyzerSelfStack;
import com.conti.elf_reader.data_parsers.elf.code_maps.ElfMachineType;
import com.conti.elf_reader.gui.utils.GuiUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.miginfocom.layout.LC;
import org.apache.commons.io.IOUtils;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class WindowHelp extends Stage {

    WindowHelp() {
        initialize(printHelpMessage());
    }

    private String printHelpMessage() {

        String help = "failed to create the help message";
        try {
            String ops1 = DataAnalyzerCallTree.supportedOperationsToString(ElfMachineType.TriCore);
            String ops2 = DataAnalyzerCallTree.supportedOperationsToString(ElfMachineType.PowerPC);
            String ops3 = DataAnalyzerSelfStack.supportedOperationsToString(ElfMachineType.TriCore);
            String ops4 = DataAnalyzerSelfStack.supportedOperationsToString(ElfMachineType.PowerPC);

            InputStream helpTemplateInputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("docs/help_template.txt");
            help = IOUtils.toString(helpTemplateInputStream, StandardCharsets.UTF_8);

            if (!ops1.isEmpty()) {
                help = help.replace("@@supported_calltree_tricore_operations@@", ops1);
                help = help.replace("@@supported_calltree_powerpc_operations@@", ops2);
                help = help.replace("@@supported_selfstack_tricore_operations@@", ops3);
                help = help.replace("@@supported_selfstack_powerpc_operations@@", ops4);
            } else {
                int index = help.indexOf("@@supported_calltree_tricore_operations@@");
                help = help.substring(0, index - 1);
            }

        } catch (Exception ignored) {
        }
        return help;
    }

    private void initialize(String text) {

        setTitle("Help");
        GuiUtils.setAppIcon(this);
        setAlwaysOnTop(true);
        initModality(Modality.APPLICATION_MODAL);

        setX(100);
        setY(100);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        setWidth(primaryScreenBounds.getWidth() - 200);
        setHeight(primaryScreenBounds.getHeight() - 200);

        MigPane migPane = new MigPane();
        migPane.setLayoutConstraints(new LC().insets("10 10 10 10"));

        TextArea textArea = new TextArea(text);
        textArea.setId("textArea");
        textArea.setEditable(false);
        migPane.add(textArea, "grow, width 100%, height 92.5%, wrap");

        Button okButton = GuiUtils.createButton("OK");
        okButton.setFont(new Font("Segoe UI", 15));
        okButton.setOnAction(e -> close());
        migPane.add(okButton, "width 100, center, height 7.5%, pad 5 0 5 0");

        Scene scene = new Scene(migPane);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                close();
            }
        });
        scene.getStylesheets().add("gui/style_help.css");
        setScene(scene);

        okButton.requestFocus();
        show();
    }
}
