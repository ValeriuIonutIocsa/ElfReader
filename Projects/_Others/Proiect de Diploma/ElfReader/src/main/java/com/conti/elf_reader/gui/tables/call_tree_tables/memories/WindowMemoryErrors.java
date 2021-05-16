package com.conti.elf_reader.gui.tables.call_tree_tables.memories;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.ErrorType;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.MemoryError;
import com.conti.elf_reader.data_parsers.core_architecture.ParserCoreArchitectureFile;
import com.conti.elf_reader.gui.utils.CustomTableView;
import com.conti.elf_reader.gui.utils.FilterComboBox;
import com.conti.elf_reader.gui.utils.GuiUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.Optional;

class WindowMemoryErrors extends Stage {

    private final PaneTabCallTreeMemories paneTabCallTreeMemories;
    private TableView<MemoryError> tableViewMemoryErrors;

    WindowMemoryErrors(PaneTabCallTreeMemories paneTabCallTreeMemories) {

        super();

        this.paneTabCallTreeMemories = paneTabCallTreeMemories;
        initialize();
    }

    private void initialize() {

        setTitle("Configure Memory and Protection Settings");
        GuiUtils.setAppIcon(this);
        setAlwaysOnTop(true);
        initModality(Modality.APPLICATION_MODAL);

        setX(100);
        setY(100);
        setMinWidth(500);
        setMinHeight(400);
        setWidth(700);
        setHeight(700);

        MigPane migPane = new MigPane();

        migPane.add(GuiUtils.createLabel("Error codes when jumping from memory to memory:"),
                "width 100%, wrap");

        tableViewMemoryErrors = createTableViewMemoryErrors();
        migPane.add(tableViewMemoryErrors, "grow, width 100%, height 100%, wrap 15");

        Separator separatorTop = new Separator(Orientation.HORIZONTAL);
        separatorTop.prefWidthProperty().bind(migPane.widthProperty());
        migPane.add(separatorTop, "wrap 8");

        MigPane migPaneAdd = createMigPaneAddAndDeleteRows();
        migPane.add(migPaneAdd, "width 100%, wrap 8");

        Separator separatorBottom = new Separator(Orientation.HORIZONTAL);
        separatorBottom.prefWidthProperty().bind(migPane.widthProperty());
        migPane.add(separatorBottom, "wrap 12");

        MigPane migPaneButtons = createMigPaneButtons();
        migPane.add(migPaneButtons, "width 100%, wrap");

        Scene scene = new Scene(migPane);
        scene.getStylesheets().add("gui/style_memories.css");
        setScene(scene);

        migPane.requestFocus();
        showAndWait();
    }

    private TableView<MemoryError> createTableViewMemoryErrors() {

        TableView<MemoryError> tableViewMemoryErrors = new CustomTableView<>();
        tableViewMemoryErrors.setOnKeyPressed(event -> {

            if (event.isControlDown() && event.getCode() == KeyCode.C) {

                tableViewMemoryErrors.getSelectionModel().getSelectedCells().stream()
                        .findFirst().ifPresent(tablePosition -> {
                    Object cellData = tablePosition.getTableColumn().getCellData(tablePosition.getRow());
                    String cellDataString = cellData != null ? cellData.toString() : "";
                    GuiUtils.putStringInClipBoard(cellDataString);
                });

            } else if (event.getCode() == KeyCode.DELETE) {
                tableViewMemoryErrors.getItems().removeAll(
                        tableViewMemoryErrors.getSelectionModel().getSelectedItems());
            }
        });

        TableColumn<MemoryError, String> tableColumnTarget = new TableColumn<>("Target");
        tableColumnTarget.prefWidthProperty().bind(tableViewMemoryErrors.widthProperty().multiply(0.2));
        tableColumnTarget.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getTarget()));
        tableViewMemoryErrors.getColumns().add(tableColumnTarget);

        TableColumn<MemoryError, String> tableColumnCallerMemory = new TableColumn<>("Caller Property");
        tableColumnCallerMemory.prefWidthProperty().bind(tableViewMemoryErrors.widthProperty().multiply(0.3));
        tableColumnCallerMemory.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getCallerProperty()));
        tableViewMemoryErrors.getColumns().add(tableColumnCallerMemory);

        TableColumn<MemoryError, String> tableColumnCalledMemory = new TableColumn<>("Called Property");
        tableColumnCalledMemory.prefWidthProperty().bind(tableViewMemoryErrors.widthProperty().multiply(0.3));
        tableColumnCalledMemory.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getCalledProperty()));
        tableViewMemoryErrors.getColumns().add(tableColumnCalledMemory);

        TableColumn<MemoryError, String> tableColumnErrorType = new TableColumn<>("Error Type");
        tableColumnErrorType.prefWidthProperty().bind(tableViewMemoryErrors.widthProperty().multiply(0.2));
        tableColumnErrorType.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getErrorType().toString()));
        tableViewMemoryErrors.getColumns().add(tableColumnErrorType);

        tableViewMemoryErrors.getItems().addAll(paneTabCallTreeMemories.getSettings().parseMemoryErrors());

        return tableViewMemoryErrors;
    }

    private MigPane createMigPaneAddAndDeleteRows() {

        MigPane migPaneAddAndDeleteRows = new MigPane(new LC().insets("0"));

        Label labelMemories = GuiUtils.createLabel("memories:");
        migPaneAddAndDeleteRows.add(labelMemories, "grow, cell 0 0, gapbottom 7");

        ComboBox<String> comboBoxMemoriesCaller = createComboBoxMemories();
        comboBoxMemoriesCaller.getSelectionModel().selectFirst();
        migPaneAddAndDeleteRows.add(comboBoxMemoriesCaller, "grow, cell 1 0, gapbottom 7");

        ComboBox<String> comboBoxMemoriesCalled = createComboBoxMemories();
        comboBoxMemoriesCalled.getSelectionModel().selectLast();
        migPaneAddAndDeleteRows.add(comboBoxMemoriesCalled, "grow, cell 2 0, gapbottom 7");

        ComboBox<String> comboBoxErrorType = new FilterComboBox<>();
        for (ErrorType errorType : ErrorType.values()) {
            comboBoxErrorType.getItems().add(errorType.toString());
        }
        comboBoxErrorType.getSelectionModel().selectLast();
        migPaneAddAndDeleteRows.add(comboBoxErrorType, "grow, cell 3 0, gapbottom 7");

        Button buttonAddComboBoxRow = GuiUtils.createButton("Add Row");
        buttonAddComboBoxRow.setOnAction(event -> addMemoryError(
                "memories",
                comboBoxMemoriesCaller.getSelectionModel().getSelectedItem(),
                comboBoxMemoriesCalled.getSelectionModel().getSelectedItem(),
                comboBoxErrorType.getSelectionModel().getSelectedItem()
        ));
        migPaneAddAndDeleteRows.add(buttonAddComboBoxRow, "grow, cell 4 0, gapbottom 7");

        Label labelProtectionLevels = GuiUtils.createLabel("protection levels:");
        migPaneAddAndDeleteRows.add(labelProtectionLevels, "grow, cell 0 1, gapbottom 7");

        TextField textFieldCaller = new TextField();
        migPaneAddAndDeleteRows.add(textFieldCaller, "grow, cell 1 1, gapbottom 7");

        TextField textFieldCalled = new TextField();
        migPaneAddAndDeleteRows.add(textFieldCalled, "grow, cell 2 1, gapbottom 7");

        ComboBox<String> comboBoxTextFieldErrorType = new FilterComboBox<>();
        for (ErrorType errorType : ErrorType.values()) {
            comboBoxTextFieldErrorType.getItems().add(errorType.toString());
        }
        comboBoxTextFieldErrorType.getSelectionModel().selectLast();
        migPaneAddAndDeleteRows.add(comboBoxTextFieldErrorType, "grow, cell 3 1, gapbottom 7");

        Button buttonAddTextFieldRow = GuiUtils.createButton("Add Row");
        buttonAddTextFieldRow.setOnAction(event -> addMemoryError(
                "protection level",
                textFieldCaller.getText(),
                textFieldCalled.getText(),
                comboBoxTextFieldErrorType.getSelectionModel().getSelectedItem()
        ));
        migPaneAddAndDeleteRows.add(buttonAddTextFieldRow, "grow, cell 4 1, gapbottom 7");

        Button buttonDeleteSelectedRows = GuiUtils.createButton("Delete Selected Rows");
        buttonDeleteSelectedRows.setOnAction(event -> removeMemoryError());
        migPaneAddAndDeleteRows.add(buttonDeleteSelectedRows, "grow, cell 4 2");

        return migPaneAddAndDeleteRows;
    }

    private ComboBox<String> createComboBoxMemories() {

        ComboBox<String> comboBoxMemories = new FilterComboBox<>();
        comboBoxMemories.getItems().addAll(ParserCoreArchitectureFile.getMemoriesByNameMap().keySet());
        return comboBoxMemories;
    }

    private void addMemoryError(String target, String callerProperty, String calledProperty, String errorTypeName) {

        if (callerProperty == null || callerProperty.isEmpty()
                || calledProperty == null || calledProperty.isEmpty()
                || errorTypeName == null || errorTypeName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill " +
                    "both the caller and the called properties to the right first!", ButtonType.OK);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(this);
            alert.showAndWait();
            return;
        }

        MemoryError memoryError = new MemoryError(target, callerProperty, calledProperty,
                ErrorType.valueOf(errorTypeName));

        switch (target) {
            case "memories":
                if (tableViewMemoryErrors.getItems().contains(memoryError)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "This setting is already configured in the TableView!" +
                                    System.lineSeparator() + "Overwrite?", ButtonType.YES, ButtonType.NO);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.initOwner(this);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get().equals(ButtonType.YES)) {
                        tableViewMemoryErrors.getItems().remove(memoryError);
                    } else {
                        return;
                    }
                }
                break;

            case "protection level":
                if (!(callerProperty.contains(".") && calledProperty.contains("."))) {
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "Invalid values for protection level (they have to contain at least one dot)!",
                            ButtonType.OK);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.initOwner(this);
                    alert.showAndWait();
                    return;
                }
                break;
        }

        tableViewMemoryErrors.getItems().add(memoryError);
        tableViewMemoryErrors.refresh();
    }

    private void removeMemoryError() {

        ObservableList<MemoryError> selectedItems = tableViewMemoryErrors.getSelectionModel().getSelectedItems();
        if (selectedItems == null || selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "No items selected in the TableView!", ButtonType.OK);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(this);
            alert.showAndWait();
            return;
        }
        tableViewMemoryErrors.getItems().removeAll(selectedItems);
        tableViewMemoryErrors.refresh();
    }

    private MigPane createMigPaneButtons() {

        MigPane migPaneButtons = new MigPane(new LC().insets("0").fillX().fillY());
        migPaneButtons.setColumnConstraints(new AC().align("[grow][grow]"));

        Button buttonSave = GuiUtils.createButton("Save");
        buttonSave.setId("buttonPane");
        buttonSave.setOnAction(event -> save());
        migPaneButtons.add(buttonSave, "align 50% 50%");

        Button buttonCancel = GuiUtils.createButton("Cancel");
        buttonCancel.setId("buttonPane");
        buttonCancel.setOnAction(event -> close());
        migPaneButtons.add(buttonCancel, "align 50% 50%");

        return migPaneButtons;
    }

    private void save() {

        paneTabCallTreeMemories.getSettings().writeMemoryErrors(tableViewMemoryErrors.getItems());
        close();
    }
}
