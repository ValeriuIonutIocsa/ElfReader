package com.conti.elf_reader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.apache.commons.io.FilenameUtils;

import com.conti.elf_reader.cli.CommandLineArguments;
import com.conti.elf_reader.data_info.DataInfo;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.gui.WindowMain;
import com.conti.elf_reader.settings.SettingNames;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.workers.Worker;
import com.utils.log.Logger;

import javafx.application.Application;

class AppStart_ElfReader {

	public static void main(final String[] args) {

		// TODO debug mode
		Logger.setDebugMode(true);

		final CommandLineArguments commandLineArguments = new CommandLineArguments();
		commandLineArguments.parse(args);

		if (commandLineArguments.isCommandLineMode()) {
			runCommandLineMode(commandLineArguments);

		} else {
			final String settingsFilePathString = commandLineArguments.getSettingsFilePathString();
			System.setProperty("javafx.preloader", "com.conti.elf_reader.gui.WindowMainPreloader");
			Application.launch(WindowMain.class, settingsFilePathString);
		}
	}

	private static void runCommandLineMode(final CommandLineArguments commandLineArguments) {

		final Settings settings = new Settings(false, commandLineArguments.getSettingsFilePathString());

		final DataInfo dataInfoSettings = settings.findSettingsOption();
		if (dataInfoSettings == null) {
			Logger.printError("Unsupported option! Please check the settings file!" +
					System.lineSeparator() + settings.supportedOptionsToString());
			System.exit(1);
		}
		final Instant start = Instant.now();

		final ElfFile elfFile = readElf(settings, dataInfoSettings);
		processElfFile(settings, dataInfoSettings, elfFile);

		Logger.printFinishMessage(start);
	}

	private static ElfFile readElf(final Settings settings, final DataInfo dataInfoSettings) {

		final String elfFilePathString = settings.get(SettingNames.elf_file_path);
		if (elfFilePathString == null || elfFilePathString.isEmpty()) {
			return null;
		}

		try {
			final ElfFile elfFile = new ElfFile(Paths.get(elfFilePathString));
			elfFile.readFile(dataInfoSettings);
			return elfFile;

		} catch (final Exception exc) {
			Logger.printError("failed to read the elf file!");
			Logger.printException(exc);
			System.exit(1);
		}
		return null;
	}

	private static void processElfFile(
			final Settings settings, final DataInfo dataInfoSettings, final ElfFile elfFile) {

		String outputFilePath = settings.get(SettingNames.output_file_path);
		if (outputFilePath.isEmpty()) {
			final String elfFilePath = settings.get(SettingNames.elf_file_path);
			outputFilePath = FilenameUtils.removeExtension(elfFilePath) + dataInfoSettings.getOption() + ".xml";
		}
		final Path outputPath = Paths.get(outputFilePath);

		final Worker worker = dataInfoSettings.createWorker(elfFile, outputPath);
		worker.generateDataFile(settings);
	}
}
