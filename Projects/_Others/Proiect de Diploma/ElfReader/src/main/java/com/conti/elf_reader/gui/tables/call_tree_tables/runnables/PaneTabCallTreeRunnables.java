package com.conti.elf_reader.gui.tables.call_tree_tables.runnables;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeRunnables;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRunnables;
import com.conti.elf_reader.data_parsers.oem.ParserOemFile;
import com.conti.elf_reader.gui.WindowMain;
import com.conti.elf_reader.gui.tables.call_tree_tables.PaneTabCallTree;
import com.conti.elf_reader.gui.utils.ChangeListenerTextFieldPath;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.utils.regex.FilterPatterns;
import com.utils.io.IoUtils;
import com.utils.xml.stax.XmlReader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

import javax.xml.stream.events.StartElement;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaneTabCallTreeRunnables extends PaneTabCallTree {

    private TextArea textAreaOsEntryTaskPatterns;
    private TextField textFieldInputOemFilePath;
    private Button buttonGenerateOemFile;

    private final Map<String, TreeItem<DataElementCallTree>> osEntryTaskToTreeMap = new HashMap<>();

    public PaneTabCallTreeRunnables(Settings settings) {
        super(settings, DataInfoCallTreeRunnables.getInstance());
    }

    @Override
    protected Node createViewComponent() {

        Node node = super.createViewComponent();

        TableColumn<DataElementTableViewRow, Object> tableColumnRecursive = tableColumnsByNameMap
                .getOrDefault("Recursive", null);
        if (tableColumnRecursive == null)
            return tableView;

        tableColumnRecursive.setCellFactory(column -> new CustomTableCellRecursivePath());

        return node;
    }

    @Override
    protected TabPane createTabPaneSettings() {

        TabPane tabPaneSettings = super.createTabPaneSettings();

        MigPane migPaneRunnables = new MigPane(new LC().insets("0").bottomToTop());

        MigPane generateOemFilePanel = createGenerateOemFilePanel();
        migPaneRunnables.add(generateOemFilePanel, "grow, width max(100%, 100%), wrap");

        MigPane migPaneOsEntryTaskPatterns = createMigPaneOsEntryTaskPatterns();
        migPaneRunnables.add(migPaneOsEntryTaskPatterns, "grow, width max(100%, 100%), wrap");

        Tab tabMemoryErrors = new Tab("Runnables", migPaneRunnables);
        tabPaneSettings.getTabs().add(tabMemoryErrors);

        tabPaneSettings.getSelectionModel().selectLast();
        return tabPaneSettings;
    }

    private MigPane createGenerateOemFilePanel() {

        MigPane generateOemFilePanel = new MigPane();

        generateOemFilePanel.add(GuiUtils.createLabel("Input OEM file path: "),
                "grow, span, wrap 10px, width max(100%, 100%)");

        textFieldInputOemFilePath = new TextField();
        textFieldInputOemFilePath.textProperty().addListener(
                new ChangeListenerTextFieldPath(textFieldInputOemFilePath));
        generateOemFilePanel.add(textFieldInputOemFilePath, "grow, width max(95%, 95%)");

        Button btnBrowse = GuiUtils.createButton("...");
        btnBrowse.setOnAction(e -> browseInputT1pFilePath(textFieldInputOemFilePath));
        generateOemFilePanel.add(btnBrowse, "grow, wrap 10px, width max(5%, 5%)");

        buttonGenerateOemFile = GuiUtils.createButton("Generate OEM File with Runnables");
        buttonGenerateOemFile.setDisable(true);
        buttonGenerateOemFile.setOnAction(e -> ParserOemFile.generateOemRunnablesFile(
                GuiUtils.getPath(textFieldInputOemFilePath), osEntryTaskToTreeMap));
        generateOemFilePanel.add(buttonGenerateOemFile, "span, grow, width max(100%, 100%)");

        return generateOemFilePanel;
    }

    private void browseInputT1pFilePath(TextField txtOemFilePath) {

        FileChooser fileChooser = GuiUtils.createFileChooser();
        fileChooser.setTitle("Browse to the input OEM .t1p file:");
        fileChooser.getExtensionFilters().add(new FileChooser
                .ExtensionFilter(".t1p file", "*.t1p"));

        File file = fileChooser.showOpenDialog(WindowMain.getPrimaryStage());
        if (file != null) {
            txtOemFilePath.setText(file.getAbsolutePath());
        }
    }

    private MigPane createMigPaneOsEntryTaskPatterns() {

        MigPane migPaneOsEntryTaskPatterns = new MigPane();

        migPaneOsEntryTaskPatterns.add(GuiUtils.createLabel(
                "Os Entry Task" + System.lineSeparator() + "Filter Patterns:"));

        textAreaOsEntryTaskPatterns = new TextArea("Os_Entry_*" + System.lineSeparator() + "osek_*");
        textAreaOsEntryTaskPatterns.setTooltip(new Tooltip(FilterPatterns.helpTextParsing));
        migPaneOsEntryTaskPatterns.add(textAreaOsEntryTaskPatterns, "grow, width 100%, height 100%");

        return migPaneOsEntryTaskPatterns;
    }

    @Override
    protected void elfPathChanged() {

        super.elfPathChanged();

        textFieldInputOemFilePath.setText("");
        try {
            Path elfFilePath = windowMain.getElfFilePath();
            Path projectPath = getProjectPath(elfFilePath);
            Path inputOemFolderPath = Paths.get(projectPath.toAbsolutePath().toString(),
                    "tmp", "corema", "T1_in");
            if(IoUtils.fileExists(inputOemFolderPath)) {
                final List<Path> filePaths = IoUtils.listFiles(inputOemFolderPath,
                        path -> path.toAbsolutePath().toString().endsWith(".t1p"));
                for (Path filePath : filePaths) {

                    final String filePathString = filePath.toAbsolutePath().toString();
                    textFieldInputOemFilePath.setText(filePathString);
                }
            }

        } catch (Exception ignored) {
        }
    }

    @Override
    protected void setButtonsDisabled(boolean b) {

        super.setButtonsDisabled(b);
        buttonGenerateOemFile.setDisable(b);
    }

    @Override
    protected void readData() throws Exception {

        super.readData();

        for (String functionName : functionNameToDataElementMap.keySet()) {

            String osEntryTaskPatternsText = textAreaOsEntryTaskPatterns.getText();
            List<String[]> osEntryTaskFilterPatterns = FilterPatterns.parseFilterPatterns(osEntryTaskPatternsText);
            if (FilterPatterns.matchesPatterns(functionName, osEntryTaskFilterPatterns, true)) {

                DataElementCallTreeRunnables dataElement = (DataElementCallTreeRunnables)
                        functionNameToDataElementMap.get(functionName);
                dataElement.getAdditionalInfo().setTask();
                TreeItem<DataElementCallTree> osEntryTaskTree = super.createTreeViewRoot(dataElement);
                for (TreeItem<DataElementCallTree> firstLevelTaskCalls : osEntryTaskTree.getChildren()) {
                    ((CustomTreeItemRunnables) firstLevelTaskCalls).setSelected(true);
                }
                osEntryTaskToTreeMap.put(functionName, osEntryTaskTree);
            }
        }
    }

    @Override
    protected TreeCell<DataElementCallTree> createCustomTreeViewCell() {
        return new CustomTreeViewCellRunnables(this, treeViewItemList);
    }

    @Override
    protected DataElementTableViewRow createTableViewItem(
            XmlReader xmlReader, StartElement startElement, int rowIndex) {
        return new DataElementCallTreeRunnables(xmlReader, startElement, rowIndex);
    }

    @Override
    protected TreeItem<DataElementCallTree> createTreeViewRoot(DataElementCallTree dataElementCallTree) {

        if (!((DataElementCallTreeRunnables) dataElementCallTree).getAdditionalInfo().isTask())
            return super.createTreeViewRoot(dataElementCallTree);

        String osEntryTaskName = dataElementCallTree.getFunctionName();
        TreeItem<DataElementCallTree> osEntryTaskTree = osEntryTaskToTreeMap.get(osEntryTaskName);
        fillTreeViewItemsList(osEntryTaskTree);
        return osEntryTaskTree;
    }

    private void fillTreeViewItemsList(TreeItem<DataElementCallTree> treeItem) {

        treeViewItemList.add(treeItem);
        for (TreeItem<DataElementCallTree> childTreeItem : treeItem.getChildren()) {
            fillTreeViewItemsList(childTreeItem);
        }
    }

    @Override
    protected TreeItem<DataElementCallTree> createTreeItem(DataElementCallTree dataElementCallTree) {
        return new CustomTreeItemRunnables(dataElementCallTree);
    }
}
