package com.conti.elf_reader.gui;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.tbee.javafx.scene.layout.MigPane;

import com.conti.elf_reader.data_info.DataInfo;
import com.conti.elf_reader.data_info.tables.DataInfoTable;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.settings.SettingNames;
import com.conti.elf_reader.settings.Settings;
import com.utils.io.IoUtils;
import com.utils.log.Logger;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.miginfocom.layout.LC;

public class WindowMain extends Application {

	private static Stage primaryStage;
	private Scene primaryScene;

	private TabPane tabPane;
	private TextField textFieldElfFilePath;

	private final ElfFile elfFile = new ElfFile(null);
	private static final Map<String, PaneTabTable> paneTabTableByNameMap = new HashMap<>();

	@Override
	public void init() {

		final java.util.List<String> parameters = getParameters().getRaw();
		final String elfReaderCfgPathString = parameters.size() > 0 ? parameters.get(0) : "ElfReaderCfg.xml";
		final Settings settings = new Settings(true, elfReaderCfgPathString);

		GuiUtils.setupCustomTooltipBehavior(100, 1000000, 100);

		final MigPane migPane = new MigPane();
		migPane.setLayoutConstraints(new LC().insets("0"));

		final MenuBar jMenuBar = createMenuBar();
		migPane.add(jMenuBar, "wrap");

		final MigPane browsePanel = createBrowsePane(settings);
		migPane.add(browsePanel, "grow, width 100%, wrap");

		final TabPane jTabbedPane = createTabbedPane(settings);
		migPane.add(jTabbedPane, "grow, height 100%, width 100%");

		primaryScene = new Scene(migPane);
		primaryScene.getStylesheets().add("gui/style_main.css");
	}

	@Override
	public void start(final Stage primaryStage) {

		WindowMain.primaryStage = primaryStage;

		primaryStage.setMaximized(true);
		primaryStage.setTitle(WindowAbout.getAppTitleAndVersion());
		GuiUtils.setAppIcon(primaryStage);

		primaryStage.setX(50);
		primaryStage.setY(50);
		primaryStage.setWidth(1200);
		primaryStage.setHeight(800);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(600);

		primaryStage.setScene(primaryScene);
		primaryScene.getRoot().requestFocus();
		primaryStage.setOnShown(event -> WindowMainPreloader.hide());

		primaryStage.show();
	}

	private MenuBar createMenuBar() {

		final MenuBar menuBar = new MenuBar();

		final Menu menuFile = new Menu("File");

		final MenuItem menuItemOpenCacheDataFolder = new MenuItem("Open Cache Folder");
		menuItemOpenCacheDataFolder.setOnAction(e -> openCacheFolder());
		menuFile.getItems().add(menuItemOpenCacheDataFolder);

		final MenuItem menuItemOpenClearCacheData = new MenuItem("Clear Cache Data");
		menuItemOpenClearCacheData.setOnAction(e -> clearCacheData());
		menuFile.getItems().add(menuItemOpenClearCacheData);
		menuBar.getMenus().add(menuFile);

		final MenuItem menuItemExit = new MenuItem("Exit");
		menuItemExit.setOnAction(event -> primaryStage.close());
		menuFile.getItems().add(menuItemExit);

		final Menu menuDocs = new Menu("Docs");

		final MenuItem menuItemDocumentation = new MenuItem("Documentation");
		menuItemDocumentation.setOnAction(e -> GuiUtils.openResourceFile("docs/documentation.txt"));
		menuDocs.getItems().add(menuItemDocumentation);

		final MenuItem menuItemElfFileFormat = new MenuItem("Elf File Format");
		menuItemElfFileFormat.setOnAction(e -> GuiUtils.openResourceFile("docs/elf.pdf"));
		menuDocs.getItems().add(menuItemElfFileFormat);

		final MenuItem menuItemDwarfFileFormat = new MenuItem("Dwarf Standard 5.0");
		menuItemDwarfFileFormat.setOnAction(e -> GuiUtils.openResourceFile("docs/dwarf5.pdf"));
		menuDocs.getItems().add(menuItemDwarfFileFormat);
		menuBar.getMenus().add(menuDocs);

		final Menu menuHelp = new Menu("Help");

		final MenuItem menuItemHelp = new MenuItem("Help");
		menuItemHelp.setOnAction(e -> new WindowHelp());
		menuHelp.getItems().add(menuItemHelp);

		final MenuItem menuItemAbout = new MenuItem("About");
		menuItemAbout.setOnAction(actionEvent -> new WindowAbout());
		menuHelp.getItems().add(menuItemAbout);

		menuBar.getMenus().add(menuHelp);
		return menuBar;
	}

	private void openCacheFolder() {

		try {
			Desktop.getDesktop().open(new File(Settings.dataFilesDirectoryPath));

		} catch (final Exception ignored) {
			Logger.printError("failed to open the cache folder!");
		}
	}

	private void clearCacheData() {

		try {
			final Stream<Path> list = Files.walk(Paths.get(Settings.dataFilesDirectoryPath));
			list.forEach(path -> {
				try {
					Files.delete(path);
				} catch (final Exception ignored) {
				}
			});
			list.close();
		} catch (final Exception ignored) {
			Logger.printError("failed to delete the cache data!");
		}
	}

	private MigPane createBrowsePane(final Settings settings) {

		final MigPane pnlBrowse = new MigPane();
		pnlBrowse.setLayoutConstraints(new LC().insets("0 10 0 10"));

		pnlBrowse.add(GuiUtils.createLabel("Elf file path: "));

		textFieldElfFilePath = new TextField();
		textFieldElfFilePath.setOnAction(event -> elfPathChanged());
		textFieldElfFilePath.textProperty().addListener((observable, oldValue, newValue) -> elfPathChanged());
		textFieldElfFilePath.setText(settings.get(SettingNames.elf_file_path));
		pnlBrowse.add(textFieldElfFilePath, "grow, push");

		final Button btnBrowse = GuiUtils.createButton("...");
		btnBrowse.setOnAction(event -> browseElfFilePath());
		pnlBrowse.add(btnBrowse, "width 3%");

		return pnlBrowse;
	}

	private void browseElfFilePath() {

		final FileChooser fileChooser = GuiUtils.createFileChooser();
		fileChooser.setTitle("Browse to the .elf file:");

		final FileChooser.ExtensionFilter elfExtensionFilter = new FileChooser.ExtensionFilter("ELF files (*.elf)",
				"*.elf");
		fileChooser.getExtensionFilters().add(elfExtensionFilter);
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("LIBRARY files (*.a)", "*.a"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OBJECT files (*.o)", "*.o"));
		fileChooser.setSelectedExtensionFilter(elfExtensionFilter);
		final File selectedFile = fileChooser.showOpenDialog(primaryStage);
		if (selectedFile != null) {
			textFieldElfFilePath.setText(selectedFile.getAbsolutePath());
			elfPathChanged();
		}
	}

	private TabPane createTabbedPane(final Settings settings) {

		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		final DataInfo dataInfoSettings = settings.findSettingsOption();
		final Collection<DataInfo> dataInfoCollection = settings.getOptionToDataInfoMap().values();

		for (final DataInfo dataInfo : dataInfoCollection) {

			if (!(dataInfo instanceof DataInfoTable)) {
				continue;
			}

			final DataInfoTable dataInfoTable = (DataInfoTable) dataInfo;

			final String tabName = dataInfoTable.getTabName();
			final PaneTabTable paneTabTable = dataInfoTable.createPaneTab(settings);
			paneTabTable.setWindowMain(this);

			final Tab tab = new Tab(tabName, paneTabTable);
			paneTabTableByNameMap.put(tabName, paneTabTable);
			tab.setGraphic(new ImageView(dataInfoTable.getTabImage()));
			this.tabPane.getTabs().add(tab);

			if (dataInfoTable.equals(dataInfoSettings)) {
				this.tabPane.getSelectionModel().select(tab);
				((PaneTabTable) tab.getContent()).redirectConsoleToTextArea();
			}
		}

		tabPane.getSelectionModel().selectedItemProperty().addListener(observable -> {

			final Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
			final PaneTabTable paneTabTable = (PaneTabTable) selectedItem.getContent();
			paneTabTable.redirectConsoleToTextArea();
		});

		if (IoUtils.fileExists(getElfFilePath())) {
			elfPathChanged();
		}

		return tabPane;
	}

	private void elfPathChanged() {

		try {
			elfFile.setElfFilePath(getElfFilePath());
			if (tabPane != null) {
				final ObservableList<Tab> tabs = tabPane.getTabs();
				for (final Tab tab : tabs) {
					((PaneTabTable) tab.getContent()).elfPathChanged();
				}
			}

		} catch (final Exception ignored) {
		}
	}

	void disableTagSwitching(final boolean b) {
		tabPane.setDisable(b);
	}

	public Path getElfFilePath() {
		return Paths.get(GuiUtils.getPath(textFieldElfFilePath));
	}

	public ElfFile getElfFile() {
		return elfFile;
	}

	public static Window getPrimaryStage() {
		return primaryStage;
	}

	public static PaneTabTable getPaneTabTableByName(final String tabName) {
		return paneTabTableByNameMap.getOrDefault(tabName, null);
	}
}
