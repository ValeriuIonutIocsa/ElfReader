package com.conti.elf_reader.gui.tables.call_tree_tables;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.tbee.javafx.scene.layout.MigPane;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.DataAnalyzerCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.data_parsers.indirect_calls.IndirectCalls;
import com.conti.elf_reader.data_parsers.indirect_calls.data.IndirectCallReplacement;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.gui.WindowMain;
import com.conti.elf_reader.gui.utils.ChangeListenerTextFieldPath;
import com.conti.elf_reader.gui.utils.FilterComboBox;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.settings.SettingNames;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.utils.regex.FilterPatterns;
import com.utils.io.IoUtils;
import com.utils.log.Logger;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import net.miginfocom.layout.LC;

public abstract class PaneTabCallTree extends PaneTabTable {

	private enum SearchIn {
		TableView, TreeView, ListView
	}

	private TreeView<DataElementCallTree> treeViewCalls;
	private ListView<DataElementCallTree> listViewCalledBy;
	private TextField textFieldIndirectCallsFilePath;
	private CheckBox checkBoxGenerateDebugOutput;
	private ComboBox<SearchIn> comboBoxSearchIn;

	protected final Map<String, DataElementCallTree> functionNameToDataElementMap = new HashMap<>();
	private final Map<IndirectCallReplacement, IndirectCallReplacement> indirectCallReplacementMap = new HashMap<>();
	protected final List<TreeItem<DataElementCallTree>> treeViewItemList = new ArrayList<>();
	private boolean switchingSelections;

	protected PaneTabCallTree(final Settings settings, final DataInfoTable dataInfoTable) {
		super(settings, dataInfoTable);
	}

	@Override
	protected Node createViewComponent() {

		final SplitPane splitPaneCallTree = new SplitPane();
		splitPaneCallTree.setOrientation(Orientation.HORIZONTAL);

		tableView = createTableView();
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (!switchingSelections) {
				switchingSelections = true;
				tableViewItemSelected(newSelection);
				switchingSelections = false;
			}
		});
		splitPaneCallTree.getItems().add(tableView);

		final SplitPane splitPaneRight = createSplitPaneRight();
		splitPaneCallTree.getItems().add(splitPaneRight);

		Platform.runLater(() -> splitPaneCallTree.setDividerPosition(0, 0.64f));
		return splitPaneCallTree;
	}

	private SplitPane createSplitPaneRight() {

		final SplitPane splitPaneRight = new SplitPane();
		splitPaneRight.setOrientation(Orientation.VERTICAL);

		final MigPane migPaneTreeViewCalls = createMigPaneTreeViewCalls();
		splitPaneRight.getItems().add(migPaneTreeViewCalls);

		final MigPane migPaneListViewCalledBy = createMigPaneListViewCalledBy();
		splitPaneRight.getItems().add(migPaneListViewCalledBy);

		Platform.runLater(() -> splitPaneRight.setDividerPosition(0, 0.5f));
		return splitPaneRight;
	}

	private MigPane createMigPaneTreeViewCalls() {

		final MigPane migPaneTreeViewCalls = new MigPane(new LC().insets("n n n 0"));

		final Label labelCallTree = GuiUtils.createLabel("Selected function's call tree:");
		labelCallTree.getStyleClass().add("label_bold");
		migPaneTreeViewCalls.add(labelCallTree, "gapleft 5, wrap");

		treeViewCalls = createTreeViewCalls();
		migPaneTreeViewCalls.add(treeViewCalls, "grow, width 100%, height 100%");

		return migPaneTreeViewCalls;
	}

	private TreeView<DataElementCallTree> createTreeViewCalls() {

		final TreeView<DataElementCallTree> treeViewCalls = new TreeView<>();
		treeViewCalls.setEditable(true);
		treeViewCalls.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		treeViewCalls.setCellFactory(param -> createCustomTreeViewCell());
		treeViewCalls.setOnKeyPressed(event -> {
			if (event.isControlDown() && event.getCode() == KeyCode.C) {

				final DataElementCallTree selectedItem = treeViewCalls.getSelectionModel().getSelectedItem().getValue();
				if (selectedItem == null) {
					return;
				}

				GuiUtils.putStringInClipBoard(selectedItem.toString());
			}
		});
		return treeViewCalls;
	}

	protected TreeCell<DataElementCallTree> createCustomTreeViewCell() {
		return new CustomTreeViewCell(this);
	}

	private MigPane createMigPaneListViewCalledBy() {

		final MigPane migPaneListViewCalledBy = new MigPane(new LC().insets("n n n 0"));

		final Label labelCalledBy = GuiUtils.createLabel("Functions calling the selected function:");
		labelCalledBy.getStyleClass().add("label_bold");
		migPaneListViewCalledBy.add(labelCalledBy, "gapleft 5, wrap");

		listViewCalledBy = createListViewCalledBy();
		migPaneListViewCalledBy.add(listViewCalledBy, "grow, width 100%, height 100%");

		return migPaneListViewCalledBy;
	}

	private ListView<DataElementCallTree> createListViewCalledBy() {

		final ListView<DataElementCallTree> listViewCalledBy = new ListView<>();
		listViewCalledBy.setOnKeyPressed(event -> {
			if (event.isControlDown() && event.getCode() == KeyCode.C) {

				final DataElementCallTree selectedItem = listViewCalledBy.getSelectionModel().getSelectedItem();
				if (selectedItem == null) {
					return;
				}

				GuiUtils.putStringInClipBoard(selectedItem.toString());
			}
		});

		listViewCalledBy.setCellFactory(param -> new CustomListCell(this));

		return listViewCalledBy;
	}

	@Override
	protected MigPane createMigPaneSettings() {

		final MigPane migPaneSettings = super.createMigPaneSettings();

		final TabPane tabPaneSettings = createTabPaneSettings();
		migPaneSettings.add(tabPaneSettings, "grow, width 100%, height 100%");

		return migPaneSettings;
	}

	protected TabPane createTabPaneSettings() {

		final TabPane tabPaneSettings = new TabPane();
		tabPaneSettings.setSide(Side.LEFT);
		tabPaneSettings.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		final MigPane migPaneIndirectCalls = createMigPaneIndirectCalls();

		final Tab tabIndirectCalls = new Tab("Indirects", migPaneIndirectCalls);
		tabPaneSettings.getTabs().add(tabIndirectCalls);

		return tabPaneSettings;
	}

	private MigPane createMigPaneIndirectCalls() {

		final MigPane migPaneIndirectCalls = new MigPane(new LC().bottomToTop());

		textFieldIndirectCallsFilePath = new TextField();
		final String indirectCallsFilePathString = settings.get(SettingNames.indirect_calls_file_path);
		if (!indirectCallsFilePathString.trim().isEmpty()) {
			textFieldIndirectCallsFilePath.setText(indirectCallsFilePathString);
		}
		textFieldIndirectCallsFilePath.textProperty().addListener(
				new ChangeListenerTextFieldPath(textFieldIndirectCallsFilePath));
		migPaneIndirectCalls.add(textFieldIndirectCallsFilePath, "grow, width max(95%, 95%)");

		final Button btnBrowse = GuiUtils.createButton("...");
		btnBrowse.setOnAction(e -> browseIndirectCallsFilePath(textFieldIndirectCallsFilePath));
		migPaneIndirectCalls.add(btnBrowse, "grow, wrap 10px, width max(5%, 5%)");

		migPaneIndirectCalls.add(GuiUtils.createLabel("Indirect calls replacements file path:"), "span");

		return migPaneIndirectCalls;
	}

	private void browseIndirectCallsFilePath(final TextField txtOemFilePath) {

		final FileChooser fileChooser = GuiUtils.createFileChooser();
		fileChooser.setTitle("Browse to the input indirect calls replacements .csv file:");
		final FileChooser.ExtensionFilter t1pExtensionFilter = new FileChooser.ExtensionFilter(".csv file", "*.csv");
		fileChooser.getExtensionFilters().add(t1pExtensionFilter);

		final File file = fileChooser.showOpenDialog(WindowMain.getPrimaryStage());
		if (file != null) {
			txtOemFilePath.setText(file.getAbsolutePath());
		}
	}

	@Override
	protected MigPane createMigPaneExport() {

		final MigPane migPaneExport = super.createMigPaneExport();

		checkBoxGenerateDebugOutput = GuiUtils.createCheckBox("Generate debug output");
		migPaneExport.add(checkBoxGenerateDebugOutput, "grow, wrap, gap 7");

		return migPaneExport;
	}

	@Override
	protected MigPane createMigPaneSearch() {

		final MigPane migPaneSearch = super.createMigPaneSearch();

		final MigPane searchInPanel = createSearchInPanel();
		migPaneSearch.add(searchInPanel, "grow, width 100%, wrap");

		return migPaneSearch;
	}

	private MigPane createSearchInPanel() {

		final MigPane migPaneSearchIn = new MigPane();
		migPaneSearchIn.setLayoutConstraints(new LC().insets("0"));

		final Label lblSearchColumn = GuiUtils.createLabel("Search in:");
		lblSearchColumn.setMinWidth(84);
		migPaneSearchIn.add(lblSearchColumn);

		comboBoxSearchIn = new FilterComboBox<>();
		comboBoxSearchIn.getItems().addAll(SearchIn.values());
		comboBoxSearchIn.setMinWidth(Region.USE_PREF_SIZE);
		comboBoxSearchIn.getSelectionModel().select(SearchIn.TableView);
		migPaneSearchIn.add(comboBoxSearchIn, "grow, width 100%");

		return migPaneSearchIn;
	}

	@Override
	protected void searchInView() {

		final SearchIn searchIn = comboBoxSearchIn.getSelectionModel().getSelectedItem();
		switch (searchIn) {

			case TableView:
				super.searchInView();
				break;

			case TreeView:
				searchInTreeView();
				break;

			case ListView:
				searchInListView();
				break;
		}
	}

	private void searchInTreeView() {

		final String searchString = txtSearch.getText();
		if (searchString.isEmpty()) {
			return;
		}

		final TreeItem<DataElementCallTree> currentNode = treeViewCalls.getSelectionModel().getSelectedItem();

		int bookmark = -1;
		if (currentNode != null) {
			for (int i = 0; i < treeViewItemList.size(); i++) {

				if (currentNode.equals(treeViewItemList.get(i))) {
					bookmark = i;
					break;
				}
			}
		}

		TreeItem<DataElementCallTree> foundTreeItem = null;
		for (int i = bookmark + 1; i < treeViewItemList.size(); i++) {

			final DataElementCallTree dataElementCallTree = treeViewItemList.get(i).getValue();
			final String nodeFunctionName = dataElementCallTree.getFunctionName();
			if (FilterPatterns.matchesPattern(nodeFunctionName, searchString, caseSensitivePatterns())) {
				foundTreeItem = treeViewItemList.get(i);
				break;
			}
		}

		if (foundTreeItem == null) {
			for (int i = 0; i <= bookmark; i++) {

				final DataElementCallTree dataElementCallTree = treeViewItemList.get(i).getValue();
				final String nodeFunctionName = dataElementCallTree.getFunctionName();
				if (FilterPatterns.matchesPattern(nodeFunctionName, searchString, caseSensitivePatterns())) {
					Logger.printStatus("The search wrapped around the end of the file.");
					foundTreeItem = treeViewItemList.get(i);
					break;
				}
			}
		}

		if (foundTreeItem == null) {
			Logger.printStatus("The search has found no results!");
			return;
		}

		treeViewCalls.getSelectionModel().clearSelection();
		treeViewCalls.getSelectionModel().select(foundTreeItem);
		treeViewCalls.scrollTo(Math.max(0, treeViewCalls.getRow(foundTreeItem) - 7));
	}

	private void searchInListView() {

		final String searchString = txtSearch.getText();
		if (searchString.isEmpty()) {
			return;
		}

		final String currentSelectedItem = listViewCalledBy.getSelectionModel().getSelectedItem().toString();

		int bookmark = -1;
		if (currentSelectedItem != null) {
			for (int i = 0; i < listViewCalledBy.getItems().size(); i++) {

				if (currentSelectedItem.equals(listViewCalledBy.getItems().get(i).toString())) {
					bookmark = i;
					break;
				}
			}
		}

		DataElementCallTree foundItem = null;
		int foundIndex = -1;
		for (int i = bookmark + 1; i < listViewCalledBy.getItems().size(); i++) {

			final String listViewItem = listViewCalledBy.getItems().get(i).toString();
			if (FilterPatterns.matchesPattern(listViewItem, searchString, caseSensitivePatterns())) {
				foundItem = listViewCalledBy.getItems().get(i);
				foundIndex = i;
				break;
			}
		}

		if (foundItem == null) {
			for (int i = 0; i <= bookmark; i++) {

				final String listViewItem = listViewCalledBy.getItems().get(i).toString();
				if (FilterPatterns.matchesPattern(listViewItem, searchString, caseSensitivePatterns())) {
					Logger.printStatus("The search wrapped around the end of the file.");
					foundItem = listViewCalledBy.getItems().get(i);
					foundIndex = i;
					break;
				}
			}
		}

		if (foundItem == null) {
			Logger.printStatus("The search has found no results!");
			return;
		}

		listViewCalledBy.getSelectionModel().clearSelection();
		listViewCalledBy.getSelectionModel().select(foundItem);
		listViewCalledBy.scrollTo(Math.max(0, foundIndex - 7));
	}

	@Override
	protected void readData() throws Exception {

		super.readData();

		functionNameToDataElementMap.clear();

		for (final DataElementTableViewRow dataElementTableViewRow : dataElementTableViewRows) {

			if (!(dataElementTableViewRow instanceof DataElementCallTree)) {
				continue;
			}

			final DataElementCallTree dataElementCallTree = (DataElementCallTree) dataElementTableViewRow;
			functionNameToDataElementMap.put(dataElementCallTree.getFunctionName(), dataElementCallTree);
		}

		IndirectCalls.parse(getIndirectCallReplacementsFilePath(), indirectCallReplacementMap);
	}

	@Override
	protected void setTableViewItems() {

		super.setTableViewItems();

		treeViewCalls.setRoot(null);
		treeViewCalls.refresh();
		listViewCalledBy.getItems().clear();
		listViewCalledBy.refresh();
	}

	private void tableViewItemSelected(final DataElementTableViewRow dataElementTableViewRow) {

		if (dataElementTableViewRow == null || (!(dataElementTableViewRow instanceof DataElementCallTree))) {
			return;
		}

		final DataElementCallTree dataElementCallTree = (DataElementCallTree) dataElementTableViewRow;

		treeViewItemList.clear();
		treeViewCalls.setRoot(createTreeViewRoot(dataElementCallTree));

		listViewCalledBy.getItems().clear();
		addCellsToListView(dataElementCallTree);
	}

	protected TreeItem<DataElementCallTree> createTreeViewRoot(final DataElementCallTree dataElementCallTree) {

		final TreeItem<DataElementCallTree> treeViewRoot = createAndProcessTreeItem(dataElementCallTree);
		addCellsToTreeView(treeViewRoot, ":" + dataElementCallTree.getFunctionName() + ":", 0);
		return treeViewRoot;
	}

	void addCellsToTreeView(final TreeItem<DataElementCallTree> treeViewCell, final String pathInTree, int depth) {

		if (depth >= 4) {
			return;
		}

		final List<String> calls = treeViewCell.getValue().getCalls();
		if (calls == null) {
			return;
		}

		depth++;
		for (final String calledFunctionName : calls) {

			final DataElementCallTree calledDataElementCallTree = functionNameToDataElementMap
					.getOrDefault(calledFunctionName, null);
			if (calledDataElementCallTree == null) {
				continue;
			}

			final TreeItem<DataElementCallTree> childTreeViewCell = createAndProcessTreeItem(calledDataElementCallTree);
			treeViewCell.getChildren().add(childTreeViewCell);

			if (depth > 0) {
				final String pathInTreeIncrement = ":" + calledFunctionName + ":";
				if (!pathInTree.contains(pathInTreeIncrement)) {
					addCellsToTreeView(childTreeViewCell, pathInTree + pathInTreeIncrement, depth);
				}
			}
		}
	}

	private TreeItem<DataElementCallTree> createAndProcessTreeItem(final DataElementCallTree dataElementCallTree) {

		final TreeItem<DataElementCallTree> treeItem = createTreeItem(dataElementCallTree);
		treeViewItemList.add(treeItem);
		treeItem.setExpanded(true);
		return treeItem;
	}

	protected TreeItem<DataElementCallTree> createTreeItem(final DataElementCallTree dataElementCallTree) {
		return new TreeItem<>(dataElementCallTree);
	}

	private void addCellsToListView(final DataElementCallTree dataElementCallTree) {

		final Set<String> calledBy = dataElementCallTree.getCalledBy();
		if (calledBy == null) {
			return;
		}

		for (final String calledByFunctionName : calledBy) {

			final DataElementCallTree calledByDataElementCallTree = functionNameToDataElementMap
					.getOrDefault(calledByFunctionName, null);
			if (calledByDataElementCallTree == null) {
				continue;
			}

			listViewCalledBy.getItems().add(calledByDataElementCallTree);
		}
	}

	void navigateTo(final String functionName) {

		GuiUtils.runAndWait(() -> {
			final boolean found = searchInTableView(tableView.getColumns().get(1),
					functionName + "*", false);
			if (!found) {
				Logger.printWarning(
						"Could not find the function in the TableView! Table column filters might be active." +
								System.lineSeparator() +
								"Clear the filters if you wish to be able to navigate to every function.");
				return;
			}
			tableViewItemSelected(tableView.getSelectionModel().getSelectedItem());
		});
	}

	void collapseTreeViewToLevel(final int depthInTreeView) {

		for (final TreeItem<DataElementCallTree> treeItem : treeViewItemList) {

			if (treeViewCalls.getTreeItemLevel(treeItem) == depthInTreeView) {
				fullyCollapseTreeItem(treeItem);
			}
		}
	}

	private static void fullyCollapseTreeItem(final TreeItem<DataElementCallTree> item) {

		if (item == null || item.isLeaf()) {
			return;
		}

		item.setExpanded(false);
		for (final TreeItem<DataElementCallTree> treeItem : item.getChildren()) {
			fullyCollapseTreeItem(treeItem);
		}
	}

	@Override
	protected void elfPathChanged() {

		super.elfPathChanged();

		try {
			final Path elfFilePath = windowMain.getElfFilePath();
			final Path projectPath = getProjectPath(elfFilePath);
			final Path indirectCallsFilePath = Paths.get(projectPath.toAbsolutePath().toString(),
					"work", "tools", "ElfReader", "ElfReaderIndirectCallReplacements.csv");

			if (IoUtils.fileExists(indirectCallsFilePath)) {
				textFieldIndirectCallsFilePath.setText(indirectCallsFilePath.toAbsolutePath().toString());

			} else if (!IoUtils.fileExists(getIndirectCallReplacementsFilePath())) {
				textFieldIndirectCallsFilePath.setText("");
			}

		} catch (final Exception ignored) {
		}
	}

	@Override
	protected void saveSettings() {

		final String indirectCallsFilePathString = GuiUtils.getPath(textFieldIndirectCallsFilePath);
		if (!indirectCallsFilePathString.isEmpty()) {
			settings.set(SettingNames.indirect_calls_file_path, indirectCallsFilePathString);
		}

		super.saveSettings();
	}

	@Override
	protected void generateOutput() {

		super.generateOutput();

		if (checkBoxGenerateDebugOutput.isSelected()) {
			generateDebugOutput();
		}
	}

	private void generateDebugOutput() {

		try {
			Logger.printProgress("generating the debug output file...");
			final String txtOutputFilePathString = GuiUtils.getPath(textFieldOutputFilePath);

			final String debugText = DataAnalyzerCallTree.getStringBuilderDebug().toString();
			if (debugText.trim().isEmpty()) {
				Logger.printWarning("can not generate the debug output file, no debug information available!" + System
						.lineSeparator() + "generate the view without using the cache data!");
			} else {
				final String pathWithoutExtension = FilenameUtils.removeExtension(txtOutputFilePathString);
				final Path debugOutputPath = Paths.get(pathWithoutExtension + "_debug.txt");
				Files.write(debugOutputPath, debugText.getBytes());
				Logger.printStatus("The debug output file was successfully generated:" + System.lineSeparator() +
						debugOutputPath);
			}

		} catch (final Exception ignored) {
			Logger.printError("failed to generate the debug output!");
		}
	}

	Path getIndirectCallReplacementsFilePath() {
		return Paths.get(GuiUtils.getPath(textFieldIndirectCallsFilePath));
	}

	Map<IndirectCallReplacement, IndirectCallReplacement> getIndirectCallReplacementMap() {
		return indirectCallReplacementMap;
	}

	Map<String, DataElementCallTree> getFunctionNameToDataElementMap() {
		return functionNameToDataElementMap;
	}
}
