package com.conti.elf_reader.data_parsers.core_architecture;

import java.nio.file.Path;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.conti.elf_reader.data_parsers.core_architecture.data.Memory;
import com.conti.elf_reader.utils.data_types.HexString;
import com.utils.io.IoUtils;
import com.utils.log.Logger;
import com.utils.xml.stax.XmlReader;

public class ParserCoreArchitectureFile {

	private static final Map<String, Memory> memoriesByNameMap = new TreeMap<>(String::compareToIgnoreCase);

	public static void parse(final Path coreArchitectureFilePath) {

		try {
			Logger.printProgress("parsing the Core Architecture file...");
			memoriesByNameMap.clear();

			if (!IoUtils.fileExists(coreArchitectureFilePath)) {
				Logger.printWarning("The Core Architecture file does not exist:" +
						System.lineSeparator() + coreArchitectureFilePath);
				return;
			}

			new XmlReader(coreArchitectureFilePath) {

				private String name;
				private HexString startAddress;
				private HexString endAddress;

				@Override
				protected void parseXmlEvent(final Stack<String> pathInXmlStack, final XMLEvent xmlEvent) {

					if (xmlEvent.isStartElement()) {
						final String pathInXml = String.join("/", pathInXmlStack);
						if (pathInXml.endsWith("/Memories/Memory")) {
							final StartElement startElement = xmlEvent.asStartElement();
							name = getAttribute(startElement, "Name");

						} else if (pathInXml.endsWith("/Memories/Memory/StartAddress")) {
							final StartElement startElement = xmlEvent.asStartElement();
							startAddress = new HexString(getAttribute(startElement, "Value"));

						} else if (pathInXml.endsWith("/Memories/Memory/EndAddress")) {
							final StartElement startElement = xmlEvent.asStartElement();
							endAddress = new HexString(getAttribute(startElement, "Value"));
						}

					} else if (xmlEvent.isEndElement()) {
						final String pathInXml = String.join("/", pathInXmlStack);
						if (pathInXml.endsWith("/Memories/Memory")) {
							memoriesByNameMap.put(name, new Memory(name, startAddress, endAddress));
						}
					}
				}

			}.readXml();

		} catch (final Exception exc) {
			Logger.printError("failed to parse the Core Architecture file!");
			Logger.printException(exc);
		}
	}

	public static String computeMemoryName(final HexString address) {

		if (address == null) {
			return "";
		}

		for (final Memory memory : ParserCoreArchitectureFile.getMemoriesByNameMap().values()) {

			if (memory.isInsideMemory(address)) {
				return memory.getName();
			}
		}
		return "";
	}

	public static Map<String, Memory> getMemoriesByNameMap() {
		return memoriesByNameMap;
	}
}
