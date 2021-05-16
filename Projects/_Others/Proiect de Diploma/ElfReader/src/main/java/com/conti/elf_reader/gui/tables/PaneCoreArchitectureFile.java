package com.conti.elf_reader.gui.tables;

import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.WindowMain;
import com.conti.elf_reader.gui.utils.ChangeListenerTextFieldPath;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.settings.SettingNames;
import com.utils.io.IoUtils;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PaneCoreArchitectureFile extends MigPane {

    private final PaneTabTable parentPane;

    private ComboBox<String> comboBoxCoreArchitectureFilePath;

    public PaneCoreArchitectureFile(PaneTabTable parentPane, String insets) {

        setLayoutConstraints(new LC().insets(insets));

        this.parentPane = parentPane;

        add(GuiUtils.createLabel("Core Architecture file path: "),
                "gapleft 3, wrap 10px");

        comboBoxCoreArchitectureFilePath = new ComboBox<>();
        comboBoxCoreArchitectureFilePath.getEditor().setText(parentPane.getSettings()
                .get(SettingNames.core_architecture_file_path));
        comboBoxCoreArchitectureFilePath.setEditable(true);
        comboBoxCoreArchitectureFilePath.getEditor().textProperty().addListener(
                new ChangeListenerTextFieldPath(comboBoxCoreArchitectureFilePath));
        add(comboBoxCoreArchitectureFilePath, "grow, width 95%");

        Button btnBrowse = GuiUtils.createButton("...");
        btnBrowse.setOnAction(e -> browseCoreArchitectureFilePath());
        add(btnBrowse, "width 5%");
    }

    private void browseCoreArchitectureFilePath() {

        FileChooser fileChooser = GuiUtils.createFileChooser();
        fileChooser.setTitle("Browse to the Core Architecture file:");
        FileChooser.ExtensionFilter t1pExtensionFilter = new FileChooser
                .ExtensionFilter(".xml file", "*.xml");
        fileChooser.getExtensionFilters().add(t1pExtensionFilter);

        File file = fileChooser.showOpenDialog(WindowMain.getPrimaryStage());
        if (file != null) {
            comboBoxCoreArchitectureFilePath.getEditor().setText(file.getAbsolutePath());
        }
    }

    public void elfPathChanged() {

        try {
            comboBoxCoreArchitectureFilePath.getEditor().setText("");

            Path elfFilePath = parentPane.getWindowMain().getElfFilePath();
            Path projectPath = parentPane.getProjectPath(elfFilePath);
            Path elfReaderFolderPath = Paths.get(projectPath.toAbsolutePath().toString(),
                    "work", "tools", "ElfReader");
            comboBoxCoreArchitectureFilePath.getItems().clear();

            if(IoUtils.fileExists(elfReaderFolderPath)) {
                final List<Path> filePaths = IoUtils.listFiles(elfReaderFolderPath, path -> {
                    String fileName = path.getFileName().toString();
                    return fileName.startsWith("CoreArchitecture") && fileName.endsWith(".xml");
                });
                for (Path filePath : filePaths) {

                    final String filePathString = filePath.toAbsolutePath().toString();
                    comboBoxCoreArchitectureFilePath.getItems().add(filePathString);
                }
            }

            comboBoxCoreArchitectureFilePath.getItems().stream().findFirst().ifPresent(
                    firstItem -> comboBoxCoreArchitectureFilePath.getSelectionModel().select(firstItem));

        } catch (Exception ignored) {
        }
    }

    public String getCoreArchitectureFilePath() {
        return GuiUtils.getPath(comboBoxCoreArchitectureFilePath.getEditor());
    }
}
