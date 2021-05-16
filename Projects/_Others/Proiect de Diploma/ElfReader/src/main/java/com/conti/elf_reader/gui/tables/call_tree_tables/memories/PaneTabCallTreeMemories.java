package com.conti.elf_reader.gui.tables.call_tree_tables.memories;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.stream.events.StartElement;

import org.tbee.javafx.scene.layout.MigPane;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeMemories;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfoMemories;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.ErrorType;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.MemoryError;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeMemories;
import com.conti.elf_reader.gui.tables.PaneCoreArchitectureFile;
import com.conti.elf_reader.gui.tables.call_tree_tables.PaneTabCallTree;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.settings.SettingNames;
import com.conti.elf_reader.settings.Settings;
import com.utils.io.IoUtils;
import com.utils.log.Logger;
import com.utils.xml.stax.XmlReader;

import javafx.scene.Node;
import javafx.scene.control.*;
import net.miginfocom.layout.LC;

public class PaneTabCallTreeMemories extends PaneTabCallTree {

	private PaneCoreArchitectureFile migPaneCoreArchitectureFile;

	public PaneTabCallTreeMemories(final Settings settings) {
		super(settings, DataInfoCallTreeMemories.getInstance());
	}

	@Override
	protected Node createViewComponent() {

		final Node node = super.createViewComponent();

		final TableColumn<DataElementTableViewRow, Object> tableColumnErrorDetails = tableColumnsByNameMap
				.getOrDefault("ErrorDetails", null);
		if (tableColumnErrorDetails == null) {
			return tableView;
		}

		tableColumnErrorDetails.setCellFactory(column -> new CustomTableCellErrorDetails());

		return node;
	}

	@Override
	protected TabPane createTabPaneSettings() {

		final TabPane tabPaneSettings = super.createTabPaneSettings();

		final MigPane migPaneMemoryErrors = createMigPaneMemoryErrors();

		final Tab tabMemoryErrors = new Tab("Memories", migPaneMemoryErrors);
		tabPaneSettings.getTabs().add(tabMemoryErrors);

		tabPaneSettings.getSelectionModel().selectLast();
		return tabPaneSettings;
	}

	private MigPane createMigPaneMemoryErrors() {

		final MigPane migPaneMemoryErrors = new MigPane(new LC().bottomToTop());

		final Button buttonConfigureMemorySettings = GuiUtils.createButton("Memory and Protection Settings");
		buttonConfigureMemorySettings.setOnAction(e -> configureMemorySettings());
		migPaneMemoryErrors.add(buttonConfigureMemorySettings, "grow, width 100%, wrap 10px");

		migPaneCoreArchitectureFile = new PaneCoreArchitectureFile(this, "0");
		migPaneMemoryErrors.add(migPaneCoreArchitectureFile, "grow, width 100%");

		return migPaneMemoryErrors;
	}

	private void configureMemorySettings() {

		Path coreArchitectureFilePath = getCoreArchitectureFilePath();
		if (!IoUtils.fileExists(coreArchitectureFilePath)) {
			coreArchitectureFilePath = Paths.get(settings.get(SettingNames.core_architecture_file_path));
		}
		if (!IoUtils.fileExists(coreArchitectureFilePath)) {
			Logger.printWarning("The Core Architecture file does not exist:" +
					System.lineSeparator() + coreArchitectureFilePath);
			return;
		}

		new WindowMemoryErrors(this);
	}

	@Override
	protected void elfPathChanged() {

		super.elfPathChanged();

		migPaneCoreArchitectureFile.elfPathChanged();
	}

	@Override
	protected void readData() throws Exception {

		super.readData();

		analyzeMemoryErrors();
	}

	@Override
	protected TreeCell<DataElementCallTree> createCustomTreeViewCell() {
		return new CustomTreeViewCellMemories(this);
	}

	private void analyzeMemoryErrors() {

		Logger.printProgress("analyzing the memory errors...");
		final List<MemoryError> memoryErrors = settings.parseMemoryErrors();

		for (final String functionName : functionNameToDataElementMap.keySet()) {

			final DataElementCallTree dataElementCaller = functionNameToDataElementMap.get(functionName);
			final CallTreeAdditionalInfoMemories additionalInfoCaller =
					((DataElementCallTreeMemories) dataElementCaller).getAdditionalInfo();

			final List<String> calls = dataElementCaller.getCalls();
			if (calls == null) {
				continue;
			}

			for (final String calledFunctionName : calls) {

				final DataElementCallTreeMemories dataElementCalled =
						(DataElementCallTreeMemories) functionNameToDataElementMap.get(calledFunctionName);
				if (dataElementCalled == null) {
					continue;
				}

				final CallTreeAdditionalInfoMemories additionalInfoCalled =
						dataElementCalled.getAdditionalInfo();

				for (final MemoryError memoryError : memoryErrors) {

					final String target = memoryError.getTarget();
					if (target == null) {
						continue;
					}

					String callerProperty = null;
					String calledProperty = null;
					switch (target) {

						case "memories":
							callerProperty = additionalInfoCaller.getMemoryName();
							calledProperty = additionalInfoCalled.getMemoryName();
							break;

						case "protection level":
							callerProperty = additionalInfoCaller.getProtection();
							calledProperty = additionalInfoCalled.getProtection();
							break;
					}

					final ErrorType errorType = memoryError.searchForErrorType(callerProperty, calledProperty);
					if (errorType == null) {
						continue;
					}

					additionalInfoCaller.setErrorType(errorType);
					additionalInfoCaller.getErrorDetails()
							.addError(errorType, dataElementCaller + " --> " + dataElementCalled);

					additionalInfoCalled.setErrorType(errorType);
					additionalInfoCalled.getErrorDetails()
							.addError(errorType, dataElementCalled + " <-- " + dataElementCaller);
				}
			}
		}
		Logger.printStatus("Finished analyzing the memory errors.");
	}

	@Override
	protected void saveSettings() {

		super.saveSettings();

		final String coreArchitectureFilePathString = getCoreArchitectureFilePath().toAbsolutePath().toString();
		if (!coreArchitectureFilePathString.isEmpty()) {
			settings.set(SettingNames.core_architecture_file_path, coreArchitectureFilePathString);
		}
	}

	@Override
	protected DataElementTableViewRow createTableViewItem(
			final XmlReader xmlReader, final StartElement startElement, final int rowIndex) {
		return new DataElementCallTreeMemories(xmlReader, startElement, rowIndex);
	}

	public Path getCoreArchitectureFilePath() {
		return Paths.get(migPaneCoreArchitectureFile.getCoreArchitectureFilePath());
	}
}
