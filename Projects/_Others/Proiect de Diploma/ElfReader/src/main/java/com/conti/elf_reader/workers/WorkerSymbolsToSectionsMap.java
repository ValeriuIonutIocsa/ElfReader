package com.conti.elf_reader.workers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.size_analyzer.data.DataElementSize;
import com.conti.elf_reader.data_info.tables.DataInfoSize;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.settings.SettingNames;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.utils.concurrency.ConcurrencyUtils;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.conti.elf_reader.workers.tables.WorkerTableView;
import com.utils.ar.AArchive;
import com.utils.ar.FactoryAArchive;
import com.utils.ar.entry.AArchiveEntry;
import com.utils.log.Logger;

public class WorkerSymbolsToSectionsMap extends Worker {

	private final Path outputFilePath;

	public WorkerSymbolsToSectionsMap(final Path outputFilePath) {

		this.outputFilePath = outputFilePath;
	}

	@Override
	public void generateDataFile(final Settings settings) {

		try {
			Logger.printProgress("generating the object file section to symbol mapping...");

			final List<Path> objectFilePaths = settings.parseObjectFilePaths();

			final Map<String, String> symbolToSectionMap = Collections.synchronizedMap(
					new TreeMap<>(String::compareToIgnoreCase));
			final List<Callable<Void>> callableList = new ArrayList<>();
			for (final Path objectFilePath : objectFilePaths) {

				if (objectFilePath.toString().endsWith(".a")) {
					parseAFile(objectFilePath, null, null, objectFilePath.getFileName().toString(),
							settings, symbolToSectionMap);
				}

				callableList.add(() -> {

					try {
						final String objectFileName = objectFilePath.getFileName().toString();
						Logger.printProgress("parsing object file: " + objectFileName);

						final ElfFile elfFile = new ElfFile(objectFilePath);
						parseElfObjectFile(elfFile, objectFileName, settings, symbolToSectionMap);

					} catch (final Exception exc) {
						Logger.printError("failed to parse elf file: " + objectFilePath);
						Logger.printException(exc);
					}
					return null;
				});
			}

			final int numberOfThreads = Math.max(DataTypes.tryParseInteger(
					settings.get(SettingNames.number_of_threads)), 1);
			ConcurrencyUtils.executeMultithreadedTask(numberOfThreads, callableList);

			try (final PrintWriter printWriter = new PrintWriter(
					new BufferedWriter(new FileWriter(outputFilePath.toFile())))) {

				for (final String symbolName : symbolToSectionMap.keySet()) {

					final String sectionName = symbolToSectionMap.get(symbolName);
					printWriter.println(symbolName + "," + sectionName);
				}
			}

		} catch (final Exception exc) {
			Logger.printException(exc);
			Logger.printError("failed to generate the symbol to section name map!");
		}
	}

	private void parseAFile(
			final Path objectFilePath, final Long fileOffsetParam, final Long fileLengthParam,
			final String objectFileName, final Settings settings,
			final Map<String, String> symbolToSectionMap) throws Exception {

		final AArchive aArchive = FactoryAArchive.INSTANCE
				.parse(objectFilePath, fileOffsetParam, fileLengthParam);

		final List<AArchiveEntry> aArchiveEntries = aArchive.getEntries();
		for (final AArchiveEntry aArchiveEntry : aArchiveEntries) {

			final String sourceFileName = aArchiveEntry.getFileName();
			final long fileOffset = aArchiveEntry.getFileOffset();
			if (sourceFileName.endsWith(".a")) {
				final long fileLength = aArchiveEntry.getSize();
				parseAFile(objectFilePath, fileOffset, fileLength, objectFileName, settings, symbolToSectionMap);

			} else {
				long elfFileOffset = fileOffset;
				if (fileOffsetParam != null) {
					elfFileOffset += fileOffsetParam;
				}
				final ElfFile elfFile = new ElfFile(objectFilePath, elfFileOffset);
				parseElfObjectFile(elfFile, objectFileName, settings, symbolToSectionMap);
			}
		}
	}

	private void parseElfObjectFile(
			final ElfFile elfFile, final String objectFileName,
			final Settings settings, final Map<String, String> symbolToSectionMap) throws Exception {

		elfFile.readFile(DataInfoSize.getInstance(), false);
		final WorkerTableView workerTableView = DataInfoSize.getInstance()
				.createWorkerTableView(elfFile, outputFilePath, false);
		final Collection<? extends DataElementTableViewRow> dataElementTableViewRowList =
				workerTableView.getDataElementTableViewRowList(settings);
		for (final DataElementTableViewRow dataElementTableViewRow : dataElementTableViewRowList) {

			final DataElementSize dataElementSize = (DataElementSize) dataElementTableViewRow;
			final String symbolName = dataElementSize.getSymbolName();
			String sectionName = dataElementSize.getSectionName();
			if (symbolName != null && !symbolName.isEmpty() &&
					sectionName != null && !sectionName.isEmpty()) {
				if (sectionName.endsWith("." + symbolName)) {
					sectionName = sectionName.substring(0,
							sectionName.length() - symbolName.length() - 1);
				}
				symbolToSectionMap.putIfAbsent(objectFileName + "," + symbolName, sectionName);
			}
		}
	}
}
