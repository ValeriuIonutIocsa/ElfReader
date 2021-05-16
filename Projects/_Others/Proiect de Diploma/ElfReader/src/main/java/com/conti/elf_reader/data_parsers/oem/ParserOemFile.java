package com.conti.elf_reader.data_parsers.oem;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang3.StringUtils;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.gui.tables.call_tree_tables.runnables.CustomTreeItemRunnables;
import com.utils.io.IoUtils;
import com.utils.log.Logger;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;

import javafx.scene.control.TreeItem;

public class ParserOemFile {

	public static void generateOemRunnablesFile(
			final String inputOemFilePathString,
			final Map<String, TreeItem<DataElementCallTree>> osEntryTaskToTreeMap) {

		try {
			final Path inputOemFilePath = Paths.get(inputOemFilePathString);
			if (!IoUtils.fileExists(inputOemFilePath)) {
				Logger.printError("invalid input OEM file!" + System.lineSeparator() + inputOemFilePath);
				return;
			}

			final Map<String, List<String>> selectedFunctionsByTaskMap = new HashMap<>();
			fillSelectedFunctionsByTaskMap(osEntryTaskToTreeMap, selectedFunctionsByTaskMap);

			editOemRunnablesFile(inputOemFilePath, selectedFunctionsByTaskMap);

		} catch (final Exception exc) {
			Logger.printError("failed to generate the .t1p file!");
			Logger.printException(exc);
		}
	}

	private static void fillSelectedFunctionsByTaskMap(
			final Map<String, TreeItem<DataElementCallTree>> osEntryTaskToTreeMap,
			final Map<String, List<String>> selectedFunctionsByTaskMap) {

		for (final String osEntryTaskName : osEntryTaskToTreeMap.keySet()) {

			final List<String> selectedTasks = new ArrayList<>();
			final TreeItem<DataElementCallTree> osEntryTaskTree = osEntryTaskToTreeMap.get(osEntryTaskName);
			fillSelectedFunctionsList(osEntryTaskTree, selectedTasks);
			selectedFunctionsByTaskMap.put(osEntryTaskName, selectedTasks);
		}
	}

	private static void fillSelectedFunctionsList(
			final TreeItem<DataElementCallTree> treeItem, final List<String> selectedTasks) {

		if (((CustomTreeItemRunnables) treeItem).isSelected()) {
			selectedTasks.add(treeItem.getValue().getFunctionName());
		}
		for (final TreeItem<DataElementCallTree> childTreeItem : treeItem.getChildren()) {
			fillSelectedFunctionsList(childTreeItem, selectedTasks);
		}
	}

	private static void editOemRunnablesFile(
			final Path inputOemFilePath, final Map<String, List<String>> selectedFunctionsByTaskMap) {

		try {
			Logger.printProgress("parsing the input OEM file and creating the output OEM file...");

			final Path outputT1pFilePath = computeOutT1pFilePath(inputOemFilePath);
			if (outputT1pFilePath == null) {
				return;
			}

			new XmlWriter(outputT1pFilePath) {

				@Override
				protected void write() {

					new XmlReader(inputOemFilePath) {

						private int index = 10000;

						@Override
						protected void parseXmlEvent(final Stack<String> pathInXml, final XMLEvent xmlEvent) {

							if (xmlEvent.isStartElement()) {
								final StartElement startElement = xmlEvent.asStartElement();
								writeXmlEvent(startElement);

								final String lastInPath = pathInXml.lastElement();
								if (StringUtils.equals(lastInPath, "SystemElement")) {
									final String type = getAttribute(startElement, "Type");
									if ("Task".equals(type)) {
										final String symbolRef = getAttribute(startElement, "SymbolRef");
										for (final String taskName : selectedFunctionsByTaskMap.keySet()) {

											if (!taskName.endsWith(symbolRef)) {
												continue;
											}

											final List<String> selectedFunctions = selectedFunctionsByTaskMap.get(
													taskName);
											createSymbolElements(selectedFunctions);
											break;
										}
									}
								}

							} else if (xmlEvent.isCharacters()) {
								final Characters characters = xmlEvent.asCharacters();
								if (characters != null && !characters.getData().trim().isEmpty()) {
									final String lastInPath = pathInXml.lastElement();
									if (StringUtils.equals(lastInPath, "HeaderContent")) {

										if (!characters.getData().contains("![CDATA[")) {
											writeCData(characters.getData());
										}
									} else {
										writeXmlEvent(characters);
									}
								}

							} else {
								if (xmlEvent.isStartDocument()) {
									writeStartDocument((StartDocument) xmlEvent);

								} else {
									writeXmlEvent(xmlEvent);
								}
							}
						}

						private void createSymbolElements(final List<String> selectedFunctions) {

							final Map<String, Integer> functionNameToOccurrencesInListMap = new HashMap<>();
							for (final String functionName : selectedFunctions) {

								if (DataElementCallTree.indirectCallFunctionName.equals(functionName)) {
									continue;
								}

								writeStartElement("SystemElement");
								final int occurrenceCount = functionNameToOccurrencesInListMap
										.getOrDefault(functionName, 0);
								functionNameToOccurrencesInListMap.put(functionName, occurrenceCount + 1);
								writeAttribute("Name", occurrenceCount > 0 ?
										functionName + "_" + occurrenceCount : functionName);
								writeAttribute("ID", String.valueOf(index++));
								writeAttribute("Type", "Runnable");
								writeAttribute("SymbolRef", functionName);
								writeEndElement("SystemElement");
							}
						}

					}.readXml();
				}
			}.writeXml();

			Logger.printStatus("The output .t1p file was successfully generated:" +
					System.lineSeparator() + outputT1pFilePath);

		} catch (final Exception exc) {
			Logger.printError("failed to create OEM file!");
			Logger.printException(exc);
		}
	}

	private static Path computeOutT1pFilePath(final Path inputOemFilePath) {

		try {
			final Path outputDirectoryPath = Paths.get(
					inputOemFilePath.getParent().getParent().getParent().getParent().toAbsolutePath().toString(),
					"out", "corema", "T1_out");
			IoUtils.createDirectories(outputDirectoryPath);
			return Paths.get(outputDirectoryPath.toAbsolutePath().toString(),
					inputOemFilePath.getFileName().toString().replace(".t1p", "_Runnables.t1p"));

		} catch (final Exception exc) {
			Logger.printError("failed to compute the output Runnables .t1p file path");
			Logger.printException(exc);
			return null;
		}
	}
}
