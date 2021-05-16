package com.conti.elf_reader.gui;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FilenameUtils;
import org.tbee.javafx.scene.layout.MigPane;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_writers.Writer;
import com.conti.elf_reader.gui.utils.*;
import com.conti.elf_reader.settings.SettingNames;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.utils.regex.FilterPatterns;
import com.utils.io.IoUtils;
import com.utils.log.Logger;
import com.utils.xml.stax.XmlReader;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import net.miginfocom.layout.LC;

public abstract class PaneTabTable extends MigPane {

	protected final Settings settings;
	protected final DataInfoTable dataInfoTable;

	protected WindowMain windowMain;

	protected TextField txtSearch;
	private Button btnSearch;
	private ComboBox<String> comboBoxSearchColumn;

	private TextArea txtFilterPatterns;
	private ComboBox<String> cmBxFilterColumn;
	private CheckBox chBxCaseSensitivePatterns;

	private Button btnGenerateView;
	protected TextField textFieldOutputFilePath;
	private CheckBox chBxOverwrite;
	private Button btnGenerateOutput;

	private TextArea textAreaExecutionStatus;

	protected TableView<DataElementTableViewRow> tableView;
	protected final Map<String, TableColumn<DataElementTableViewRow, Object>> tableColumnsByNameMap = new HashMap<>();
	protected final ObservableList<DataElementTableViewRow> dataElementTableViewRows =
			FXCollections.observableArrayList();
	private Predicate<DataElementTableViewRow> tableViewFilterPredicate = tableViewItem -> true;

	protected PaneTabTable(final Settings settings, final DataInfoTable dataInfoTable) {

		this.settings = settings;
		this.dataInfoTable = dataInfoTable;

		initialize();
	}

	void setWindowMain(final WindowMain windowMain) {
		this.windowMain = windowMain;
	}

	private void initialize() {

		setOpaqueInsets(new Insets(0));

		final MigPane toolsPanel = createToolsPanel();
		add(toolsPanel, "grow, wrap, width max(100%, 100%), height max(20%, 20%)");

		final SplitPane jSplitPane = createSplitPane();
		add(jSplitPane, "grow, width max(100%, 100%), height max(80%, 80%)");
	}

	private SplitPane createSplitPane() {

		final SplitPane splitPane = new SplitPane();

		final Node view = createViewComponent();
		splitPane.getItems().add(view);

		final MigPane statusPanel = createStatusPanel();
		splitPane.getItems().add(statusPanel);

		Platform.runLater(() -> splitPane.setDividerPosition(0, 0.78f));
		return splitPane;
	}

	protected Node createViewComponent() {

		tableView = createTableView();
		return tableView;
	}

	protected TableView<DataElementTableViewRow> createTableView() {

		final TableView<DataElementTableViewRow> tableView = new CustomTableView<>();

		comboBoxSearchColumn.getItems().clear();
		cmBxFilterColumn.getItems().clear();
		tableColumnsByNameMap.clear();
		final TableViewColumnInfo[] columnInfoArray = dataInfoTable.getColumnInfoArray();
		final int columnCount = columnInfoArray.length;
		for (int i = 0; i < columnCount; i++) {

			final int columnIndex = i;
			final String columnTitleName = columnInfoArray[columnIndex].getColumnTitleName();
			comboBoxSearchColumn.getItems().add(columnTitleName);
			cmBxFilterColumn.getItems().add(columnTitleName);
			final TableColumn<DataElementTableViewRow, Object> tableColumn =
					new TableColumn<>(columnTitleName);

			final double columnWidthPercentage = columnInfoArray[columnIndex].getWidthPercentage();
			final DoubleBinding widthBinding = tableView.widthProperty().multiply(columnWidthPercentage);
			tableColumn.prefWidthProperty().bind(widthBinding);

			tableColumn.setCellValueFactory(cellDataFeatures -> {
				final Object object = cellDataFeatures.getValue().getRowData()[columnIndex];
				return new SimpleObjectProperty<>(object);
			});

			tableColumnsByNameMap.put(columnTitleName, tableColumn);
			tableView.getColumns().add(tableColumn);
		}
		comboBoxSearchColumn.getSelectionModel().select(1);
		cmBxFilterColumn.getSelectionModel().select(1);

		return tableView;
	}

	private MigPane createStatusPanel() {

		final MigPane panelStatus = new MigPane();
		panelStatus.setLayoutConstraints(new LC().insets("n n 0 n"));

		btnGenerateView = GuiUtils.createButton("Generate View");
		btnGenerateView.setId("btnGenerateView");
		btnGenerateView.setOnAction(event -> generateViewOnClick());
		panelStatus.add(btnGenerateView, "grow, width max(100%, 100%), height max(8%, 8%)");

		chBxOverwrite = GuiUtils.createCheckBox("overwrite cache data");
		panelStatus.add(chBxOverwrite, "wrap 10px, height max(8%, 8%)");

		textAreaExecutionStatus = new TextArea();
		textAreaExecutionStatus.setId("textAreaExecutionStatus");
		textAreaExecutionStatus.setEditable(false);

		final ContextMenu jPopupMenuExecutionStatus = new ContextMenu();
		final MenuItem jMenuItem = new MenuItem("clear");
		jMenuItem.setOnAction(event -> textAreaExecutionStatus.clear());
		jPopupMenuExecutionStatus.getItems().add(jMenuItem);
		textAreaExecutionStatus.setContextMenu(jPopupMenuExecutionStatus);

		panelStatus.add(textAreaExecutionStatus, "grow, span, width max(100%, 100%), height max(92%, 92%)");

		return panelStatus;
	}

	private MigPane createToolsPanel() {

		final MigPane toolsPanel = new MigPane();
		toolsPanel.setLayoutConstraints(new LC().insets("0"));

		final MigPane settingsPanel = createMigPaneSettings();
		toolsPanel.add(settingsPanel, "cell 0 0, grow, width max(25%, 25%)");

		final MigPane exportPanel = createMigPaneExport();
		toolsPanel.add(exportPanel, "cell 1 0, grow, width max(25%, 25%)");

		final MigPane searchPanel = createMigPaneSearch();
		toolsPanel.add(searchPanel, "cell 2 0, grow, width max(25%, 25%)");

		final MigPane filterPanel = createFilterPanel();
		toolsPanel.add(filterPanel, "cell 3 0, grow, width max(25%, 25%)");

		return toolsPanel;
	}

	protected MigPane createMigPaneSettings() {

		final MigPane migPaneSettings = new MigPane();
		migPaneSettings.setLayoutConstraints(new LC().bottomToTop().insets("0"));
		return migPaneSettings;
	}

	protected MigPane createMigPaneExport() {

		final MigPane migPaneExport = new MigPane();
		migPaneExport.setLayoutConstraints(new LC().insets("0").bottomToTop());

		final MigPane browseOutputPanel = createMigPaneBrowse(settings.get(SettingNames.output_file_path));
		migPaneExport.add(browseOutputPanel, "grow, north, width max(100%, 100%)");

		return migPaneExport;
	}

	private MigPane createMigPaneBrowse(String outputFilePath) {

		final MigPane migPaneBrowse = new MigPane();

		migPaneBrowse.add(GuiUtils.createLabel("Output file path: "),
				"span, grow, wrap 10px, width max(100%, 100%)");

		if (outputFilePath != null) {
			outputFilePath = outputFilePath.replace("-gui", dataInfoTable.getOption());
		}
		textFieldOutputFilePath = new TextField(outputFilePath);
		textFieldOutputFilePath.textProperty().addListener(new ChangeListenerTextFieldPath(textFieldOutputFilePath));
		migPaneBrowse.add(textFieldOutputFilePath, "grow, width max(95%, 95%)");

		final Button btnBrowse = GuiUtils.createButton("...");
		btnBrowse.setOnAction(e -> browseOutputPath());
		migPaneBrowse.add(btnBrowse, "grow, wrap 10px, width max(5%, 5%)");

		btnGenerateOutput = GuiUtils.createButton("Generate Output File");
		btnGenerateOutput.setDisable(true);
		btnGenerateOutput.setOnAction(e -> generateOutput());
		migPaneBrowse.add(btnGenerateOutput, "span, grow, width max(100%, 100%)");

		return migPaneBrowse;
	}

	private void browseOutputPath() {

		final FileChooser fileChooser = GuiUtils.createFileChooser();
		fileChooser.setTitle("Choose the output path:");

		final FileChooser.ExtensionFilter xmlExtensionFilter = new FileChooser.ExtensionFilter("XML file (*.xml)",
				"*.xml");
		final FileChooser.ExtensionFilter csvExtensionFilter = new FileChooser.ExtensionFilter("CSV file (*.csv)",
				"*.csv");
		final FileChooser.ExtensionFilter xlsxExtensionFilter = new FileChooser.ExtensionFilter("XLSX file (*.xlsx)",
				"*.xlsx");
		fileChooser.getExtensionFilters().addAll(xmlExtensionFilter, csvExtensionFilter, xlsxExtensionFilter);

		File file = fileChooser.showSaveDialog(WindowMain.getPrimaryStage());
		if (file == null) {
			return;
		}

		final String extension = "." + fileChooser.getSelectedExtensionFilter()
				.getDescription().split(" ", 0)[0].toLowerCase();
		if (!".All".equals(extension)) {
			file = new File(file.getParentFile(),
					FilenameUtils.getBaseName(file.getName()) + extension);
		}

		textFieldOutputFilePath.setText(file.getAbsolutePath());
	}

	protected void generateOutput() {

		final String outputPathString = GuiUtils.getPath(textFieldOutputFilePath);
		if (outputPathString.isEmpty()) {
			Logger.printWarning("The output path is empty!");
			return;
		}
		new Thread(() -> Writer.write(Paths.get(outputPathString), dataInfoTable, tableView.getItems())).start();
	}

	protected MigPane createMigPaneSearch() {

		final MigPane migPaneSearch = new MigPane();
		migPaneSearch.setLayoutConstraints(new LC().bottomToTop());

		chBxCaseSensitivePatterns = GuiUtils.createCheckBox("case sensitive patterns");
		migPaneSearch.add(chBxCaseSensitivePatterns, "al right, wrap");

		final MigPane migPaneSearchText = createSearchTextPanel();
		migPaneSearch.add(migPaneSearchText, "grow, wrap, width max(100%, 100%)");

		final MigPane migPaneSearchColumn = createMigPaneSearchColumn();
		migPaneSearch.add(migPaneSearchColumn, "grow, width 100%, wrap");

		return migPaneSearch;
	}

	private MigPane createSearchTextPanel() {

		final MigPane migPaneSearchText = new MigPane();
		migPaneSearchText.setLayoutConstraints(new LC().insets("0"));

		txtSearch = new TextField();
		txtSearch.setOnAction(e -> searchInView());
		migPaneSearchText.add(txtSearch, "grow, width max(90%, 90%)");

		btnSearch = GuiUtils.createButton("Search");
		btnSearch.setDisable(true);
		btnSearch.setOnAction(e -> searchInView());
		migPaneSearchText.add(btnSearch, "grow, width max(10%, 10%)");

		return migPaneSearchText;
	}

	protected void searchInView() {

		final int searchColumnIndex = comboBoxSearchColumn.getSelectionModel().getSelectedIndex();
		final TableColumn<DataElementTableViewRow, ?> searchColumn = tableView.getColumns().get(searchColumnIndex);

		final String searchString = txtSearch.getText();
		if (searchString.isEmpty()) {
			return;
		}

		searchInTableView(searchColumn, searchString, true);
	}

	protected boolean searchInTableView(
			final TableColumn<DataElementTableViewRow, ?> searchColumn, final String searchString,
			final boolean verbose) {

		final java.util.List<String> searchRows = new ArrayList<>();
		getSearchRows(searchColumn, searchRows);
		final int currentRow = tableView.getSelectionModel().getSelectedIndex();

		int foundRow = -1;
		for (int row = currentRow + 1; row < searchRows.size(); row++) {

			final String cellValue = searchRows.get(row);
			if (FilterPatterns.matchesPattern(cellValue, searchString, caseSensitivePatterns())) {
				foundRow = row;
				break;
			}
		}

		if (foundRow == -1) {
			for (int row = 0; row <= currentRow; row++) {

				final String nodeFunctionName = searchRows.get(row);
				if (FilterPatterns.matchesPattern(nodeFunctionName, searchString, caseSensitivePatterns())) {
					if (verbose) {
						Logger.printStatus("The search wrapped around the end of the file.");
					}
					foundRow = row;
					break;
				}
			}
		}

		if (foundRow == -1) {
			if (verbose) {
				Logger.printStatus("The search has found no results!");
			}
			return false;
		}

		tableView.getSelectionModel().select(foundRow, searchColumn);
		tableView.scrollTo(Math.max(0, foundRow - 7));
		tableView.requestFocus();
		return true;
	}

	private void getSearchRows(
			final TableColumn<DataElementTableViewRow, ?> searchColumn, final Collection<String> searchRows) {

		final int rowCount = tableView.getItems().size();
		for (int i = 0; i < rowCount; i++) {
			searchRows.add(getCellValue(searchColumn, i));
		}
	}

	private String getCellValue(final TableColumn<DataElementTableViewRow, ?> searchColumn, final int row) {

		final Object valueAt = searchColumn.getCellData(row);
		return valueAt != null ? valueAt.toString() : "";
	}

	private MigPane createMigPaneSearchColumn() {

		final MigPane migPaneSearchColumn = new MigPane();
		migPaneSearchColumn.setLayoutConstraints(new LC().insets("0"));

		final Label labelSearchColumn = GuiUtils.createLabel("Search column:");
		labelSearchColumn.setMinWidth(84);
		migPaneSearchColumn.add(labelSearchColumn);

		comboBoxSearchColumn = new FilterComboBox<>();
		migPaneSearchColumn.add(comboBoxSearchColumn, "grow, width 100%");

		return migPaneSearchColumn;
	}

	private MigPane createFilterPanel() {

		final MigPane filterPanel = new MigPane();

		filterPanel.add(GuiUtils.createLabel(FilterPatterns.helpTextPatterns), "grow");

		txtFilterPatterns = new TextArea();
		txtFilterPatterns.setTooltip(new Tooltip(FilterPatterns.helpTextParsing));
		txtFilterPatterns.setMinWidth(110);
		filterPanel.add(txtFilterPatterns, "grow");

		final MigPane filterSettingsPane = createMigPaneFilterSettings();
		filterPanel.add(filterSettingsPane, "grow, gapleft 5, gapright 0");

		return filterPanel;
	}

	private MigPane createMigPaneFilterSettings() {

		final MigPane migPaneFilterSettings = new MigPane();
		migPaneFilterSettings.setLayoutConstraints(new LC().insets("0").bottomToTop());

		final Button btnSaveSettings = GuiUtils.createButton("Save Settings");
		btnSaveSettings.setOnAction(event -> saveSettings());
		migPaneFilterSettings.add(btnSaveSettings, "al right, wrap");

		final Button btnClearFilters = GuiUtils.createButton("Clear All Filters");
		btnClearFilters.setOnAction(e -> clearFilters());
		migPaneFilterSettings.add(btnClearFilters, "grow, wrap");

		final Button btnOrFilter = GuiUtils.createButton("Add OR Column Filter");
		btnOrFilter.setOnAction(e -> addColumnFilter(false));
		migPaneFilterSettings.add(btnOrFilter, "grow, wrap");

		final Button btnAddFilter = GuiUtils.createButton("Add AND Column Filter");
		btnAddFilter.setOnAction(e -> addColumnFilter(true));
		migPaneFilterSettings.add(btnAddFilter, "grow, wrap");

		final MigPane pnlFilterColumn = createFilterColumnPanel();
		migPaneFilterSettings.add(pnlFilterColumn);

		return migPaneFilterSettings;
	}

	private MigPane createFilterColumnPanel() {

		final MigPane pnlFilterColumn = new MigPane();

		pnlFilterColumn.add(GuiUtils.createLabel("Filter column: "), "cell 0 0, grow, west");

		cmBxFilterColumn = new FilterComboBox<>();
		pnlFilterColumn.add(cmBxFilterColumn, "cell 1 0, grow, west");

		pnlFilterColumn.add(GuiUtils.createLabel(""), "cell 2 0, grow, push");

		return pnlFilterColumn;
	}

	private void addColumnFilter(final boolean andOr) {

		final List<String[]> filterPatterns = FilterPatterns.parseFilterPatterns(txtFilterPatterns.getText());
		final int filerColumnIndex = cmBxFilterColumn.getSelectionModel().getSelectedIndex();
		final boolean caseSensitive = caseSensitivePatterns();

		final Predicate<DataElementTableViewRow> predicate = dataElementTableViewRow -> {

			final Object[] rowData = dataElementTableViewRow.getRowData();
			final Object rowDataObject = rowData[filerColumnIndex];
			final String filterCellString = rowDataObject != null ? rowDataObject.toString() : null;
			return FilterPatterns.matchesPatterns(filterCellString, filterPatterns, caseSensitive);
		};

		tableViewFilterPredicate = andOr ?
				tableViewFilterPredicate.and(predicate) : tableViewFilterPredicate.or(predicate);

		setTableViewItems();

		Logger.printStatus((andOr ? "AND" : "OR") + " filter applied to column: " + cmBxFilterColumn.getSelectionModel()
				.getSelectedItem());
	}

	private void clearFilters() {

		tableViewFilterPredicate = tableViewItem -> true;
		setTableViewItems();
		Logger.printStatus("Filters cleared.");
	}

	protected void saveSettings() {

		final String elfFilePathString = windowMain.getElfFilePath().toAbsolutePath().toString();
		if (!elfFilePathString.isEmpty()) {
			settings.set(SettingNames.elf_file_path, elfFilePathString);
		}

		final String outputFilePathString = GuiUtils.getPath(textFieldOutputFilePath);
		if (!outputFilePathString.isEmpty()) {
			settings.set(SettingNames.output_file_path, outputFilePathString);
		}

		settings.set(SettingNames.option, dataInfoTable.getOption());

		settings.save();
	}

	private void generateViewOnClick() {

		final Instant start = Instant.now();
		setButtonsDisabled(true);

		new Thread(() -> {

			try {
				if (!checkRequiredFiles()) {
					return;
				}

				generateView();
				Logger.printStatus("The view was generated successfully.");
				Logger.printFinishMessage(start);

			} catch (Exception exc) {
				Logger.printException(exc);
				Logger.printError("failed to generate the view! ");

			} finally {
				setButtonsDisabled(false);
			}
		}).start();
	}

	private void generateView() throws Exception {

		Logger.printProgress("generating the \"" + dataInfoTable.getTabName() + "\" view...");

		dataElementTableViewRows.clear();
		GuiUtils.runAndWait(() -> {
			tableView.getItems().clear();
			tableView.refresh();
		});

		readData();
		setTableViewItems();
	}

	protected void readData() throws Exception {

		final Path dataFile = generateDataFile();

		Logger.printProgress("filling the TableView with data...");

		new XmlReader(dataFile) {

			private int rowIndex = -1;

			@Override
			protected void parseXmlEvent(final Stack<String> pathInXml, final XMLEvent xmlEvent) {

				if (xmlEvent.isStartElement()) {

					if (rowIndex < 0) {
						rowIndex = 0;
						rowIndex++;
						return;
					}

					final StartElement startElement = xmlEvent.asStartElement();
					final DataElementTableViewRow dataElementTableViewRow = createTableViewItem(
							this, startElement, rowIndex);
					dataElementTableViewRows.add(dataElementTableViewRow);
					rowIndex++;
				}
			}

		}.readXml();
	}

	protected abstract DataElementTableViewRow createTableViewItem(
			XmlReader xmlReader, StartElement startElement, int rowIndex);

	protected void setTableViewItems() {

		if (dataElementTableViewRows.isEmpty()) {
			return;
		}

		final FilteredList<DataElementTableViewRow> filteredData = new FilteredList<>(dataElementTableViewRows);
		filteredData.setPredicate(tableViewFilterPredicate);

		final SortedList<DataElementTableViewRow> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());

		GuiUtils.runAndWait(() -> {
			tableView.setItems(sortedData);
			tableView.refresh();
		});
	}

	protected void setButtonsDisabled(final boolean b) {

		windowMain.disableTagSwitching(b);
		btnGenerateView.setDisable(b);
		btnGenerateOutput.setDisable(b);
		btnSearch.setDisable(b);
	}

	void redirectConsoleToTextArea() {

		final PrintStream printConsole = new PrintStream(new ConsoleOutputStream(textAreaExecutionStatus));
		System.setOut(printConsole);
		System.setErr(printConsole);
	}

	private boolean checkRequiredFiles() {

		final Path elfFilePath = windowMain.getElfFilePath();
		final String elfFileName = elfFilePath.getFileName().toString();
		if (!IoUtils.fileExists(elfFilePath) ||
				!(elfFileName.endsWith(".elf") || elfFileName.endsWith(".o") || elfFileName.endsWith(".a"))) {
			Logger.printError("the input .elf file does not exist!");
			return false;
		}
		return true;
	}

	private Path generateDataFile() throws Exception {

		final ElfFile elfFile = windowMain.getElfFile();
		final Path elfFilePath = elfFile.getElfFilePath();
		final String elfFileName = elfFilePath.getFileName().toString();
		final String elfFileNameWithoutExtension = FilenameUtils.removeExtension(elfFileName);
		final long elfFileLastModified = Files.getLastModifiedTime(elfFilePath).toMillis();

		final Path dataFilePath = Paths.get(Settings.dataFilesDirectoryPath,
				elfFileNameWithoutExtension + dataInfoTable.getOption() +
						"_" + WindowAbout.appVersion + "_" + elfFileLastModified + ".xml");
		if (chBxOverwrite.isSelected()) {
			if (IoUtils.fileExists(dataFilePath)) {
				Files.delete(dataFilePath);
			}
		}
		if (!IoUtils.fileExists(dataFilePath)) {
			Logger.printProgress("generating the data file:" + System.lineSeparator() + dataFilePath);
			final Path dataFilesDirectoryPath = Paths.get(Settings.dataFilesDirectoryPath);
			Files.createDirectories(dataFilesDirectoryPath);

			elfFile.readFile(dataInfoTable);
			dataInfoTable.createWorkerTableView(elfFile, dataFilePath, false).generateDataFile(settings);

		} else {
			Logger.printProgress("loading from data file:" + System.lineSeparator() + dataFilePath);
		}
		return dataFilePath;
	}

	protected void elfPathChanged() {

		final String elfFilePathString = windowMain.getElfFilePath().toAbsolutePath().toString();
		final String elfFilePathWithoutExtension = FilenameUtils.removeExtension(elfFilePathString);
		textFieldOutputFilePath.setText(elfFilePathWithoutExtension + dataInfoTable.getOption() + ".xml");
	}

	public Path getProjectPath(final Path elfFilePath) {
		return elfFilePath.getParent().getParent().getParent();
	}

	protected boolean caseSensitivePatterns() {
		return chBxCaseSensitivePatterns.isSelected();
	}

	public WindowMain getWindowMain() {
		return windowMain;
	}

	public Settings getSettings() {
		return settings;
	}
}
