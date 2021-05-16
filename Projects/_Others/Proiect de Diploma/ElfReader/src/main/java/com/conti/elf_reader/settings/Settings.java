package com.conti.elf_reader.settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.conti.elf_reader.cli.Options;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.ErrorType;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.MemoryError;
import com.conti.elf_reader.data_info.DataInfo;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeMemories;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRegular;
import com.conti.elf_reader.data_parsers.core_architecture.ParserCoreArchitectureFile;
import com.conti.elf_reader.gui.WindowMain;
import com.conti.elf_reader.gui.tables.call_tree_tables.memories.PaneTabCallTreeMemories;
import com.utils.io.IoUtils;
import com.utils.log.Logger;
import com.utils.xml.dom.XmlDomParsingMethods;

public class Settings {

	public final static String dataFilesDirectoryPath = Paths.get(
			javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory().getPath(),
			"ElfReader").toString();

	private final boolean gui;
	private final String settingsFilePathString;
	private final Document document;
	private final Options options = new Options();

	public Settings(final boolean gui, final String settingsFilePathString) {

		this.gui = gui;
		this.settingsFilePathString = settingsFilePathString;
		document = createDocument();
	}

	public DataInfo findSettingsOption() {

		final String option = get(SettingNames.option);
		return options.getOptionToDataInfoMap().getOrDefault(option, DataInfoCallTreeRegular.getInstance());
	}

	private Document createDocument() {

		if (settingsFilePathString == null || settingsFilePathString.trim().isEmpty()) {
			return null;
		}

		final Path settingsFilePath = Paths.get(settingsFilePathString);
		if (IoUtils.fileExists(settingsFilePath)) {
			try {
				return XmlDomParsingMethods.openDocument(settingsFilePath);

			} catch (final Exception ignored) {
				Logger.printWarning("failed to load the settings from:" +
						System.lineSeparator() + settingsFilePathString);
			}
		}

		try {
			return XmlDomParsingMethods.createNewDocument();

		} catch (final Exception e) {
			Logger.printWarning("failed to create a settings file in:" +
					System.lineSeparator() + settingsFilePathString);
			return null;
		}
	}

	public void save() {

		try {
			XmlDomParsingMethods.saveXmlFile(document, settingsFilePathString);
			Logger.printStatus("Settings saved to:" + System.lineSeparator() + settingsFilePathString);

		} catch (final Exception ignored) {
			Logger.printWarning("failed to save the settings to: " + settingsFilePathString);
		}
	}

	public void set(final SettingNames settingName, final String value) {

		if (document == null) {
			return;
		}

		final Element documentElement = getOrCreateDocumentElement();

		final NodeList elementsByTagName = documentElement.getElementsByTagName(settingName.toString());
		final int elementsByTagNameLength = elementsByTagName.getLength();

		if (elementsByTagNameLength >= 1) {
			for (int i = 1; i < elementsByTagNameLength; i++) {
				documentElement.removeChild(elementsByTagName.item(i));
			}
			elementsByTagName.item(0).setTextContent(value);

		} else {
			final Element element = document.createElement(settingName.toString());
			element.setTextContent(value);
			documentElement.appendChild(element);
		}
	}

	public String get(final SettingNames settingName) {

		if (document == null) {
			return "";
		}

		final NodeList elementsByTagName = document.getElementsByTagName(settingName.toString());
		if (elementsByTagName.getLength() != 1) {
			return "";
		} else {
			return elementsByTagName.item(0).getTextContent();
		}
	}

	public List<Path> parseObjectFilePaths() {

		final List<Path> elfFilePaths = new ArrayList<>();
		if (document == null) {
			return elfFilePaths;
		}

		try {
			final NodeList elfFilePathsList = document.getElementsByTagName(SettingNames.object_file_paths.toString());
			final NodeList childNodes = elfFilePathsList.item(0).getChildNodes();
			final int childNodesLength = childNodes.getLength();
			for (int i = 0; i < childNodesLength; i++) {

				final Node item = childNodes.item(i);
				if (item instanceof Element) {
					final Element childElement = (Element) item;
					if (SettingNames.object_file_path.toString().equals(childElement.getTagName())) {
						final String textContent = childElement.getTextContent();
						if (textContent != null && !textContent.isEmpty()) {
							elfFilePaths.add(Paths.get(textContent));
						}
					}
				}
			}
		} catch (final Exception ignored) {
		}

		return elfFilePaths;
	}

	public List<MemoryError> parseMemoryErrors() {

		final List<MemoryError> memoryErrors = new ArrayList<>();
		if (document == null) {
			return memoryErrors;
		}

		try {
			Logger.printProgress("parsing the memory error settings...");
			final PaneTabCallTreeMemories paneTabCallTreeMemories = (PaneTabCallTreeMemories) WindowMain
					.getPaneTabTableByName(DataInfoCallTreeMemories.getInstance().getTabName());
			ParserCoreArchitectureFile.parse(paneTabCallTreeMemories.getCoreArchitectureFilePath());

			final NodeList nodeListMemoryErrors = document.getElementsByTagName(SettingNames.memory_error.toString());
			final int nodeListMemoryErrorsLength = nodeListMemoryErrors.getLength();
			for (int i = 0; i < nodeListMemoryErrorsLength; i++) {
				final Element element = (Element) nodeListMemoryErrors.item(i);
				final String target = element.getAttribute("Target");
				final String callerName = element.getAttribute("CallerProperty");
				final String calledName = element.getAttribute("CalledProperty");
				final String errorTypeName = element.getAttribute("ErrorType");
				if (ErrorType.contains(errorTypeName)) {
					if ((calledName.contains(".") && callerName.contains(".")) ||
							(ParserCoreArchitectureFile.getMemoriesByNameMap().containsKey(callerName) &&
									ParserCoreArchitectureFile.getMemoriesByNameMap().containsKey(calledName))) {
						memoryErrors.add(new MemoryError(
								target, callerName, calledName, ErrorType.valueOf(errorTypeName)));
					}
				}
			}

		} catch (final Exception exc) {
			Logger.printException(exc);
		}

		return memoryErrors;
	}

	public void writeMemoryErrors(final List<MemoryError> memoryErrors) {

		if (document == null) {
			return;
		}

		try {
			Logger.printProgress("saving the memory error settings...");

			final NodeList nodeList = document.getElementsByTagName(SettingNames.memory_errors.toString());
			if (nodeList.getLength() > 0) {
				for (int i = 0; i < nodeList.getLength(); i++) {

					final Node node = nodeList.item(i);
					node.getParentNode().removeChild(node);
				}
			}

			final Element elementMemoryErrors = document.createElement(SettingNames.memory_errors.toString());
			final Element documentElement = getOrCreateDocumentElement();
			documentElement.appendChild(elementMemoryErrors);
			for (final MemoryError memoryError : memoryErrors) {

				final Element elementMemoryError = document.createElement(SettingNames.memory_error.toString());
				elementMemoryError.setAttribute("Target", memoryError.getTarget());
				elementMemoryError.setAttribute("CallerProperty", memoryError.getCallerProperty());
				elementMemoryError.setAttribute("CalledProperty", memoryError.getCalledProperty());
				elementMemoryError.setAttribute("ErrorType", memoryError.getErrorType().toString());
				elementMemoryErrors.appendChild(elementMemoryError);
			}

			XmlDomParsingMethods.saveXmlFile(document, settingsFilePathString);

			Logger.printStatus("Memory error settings saved successfully.");

		} catch (final Exception exc) {
			Logger.printException(exc);
			Logger.printError("failed to save the memory error settings!");
		}
	}

	private Element getOrCreateDocumentElement() {

		Element documentElement = document.getDocumentElement();
		if (documentElement == null) {
			documentElement = document.createElement("elf_reader_settings");
			document.appendChild(documentElement);
		}
		return documentElement;
	}

	public String supportedOptionsToString() {
		return options.supportedOptionsToString();
	}

	public Map<String, DataInfo> getOptionToDataInfoMap() {
		return options.getOptionToDataInfoMap();
	}

	public boolean isGui() {
		return gui;
	}
}
