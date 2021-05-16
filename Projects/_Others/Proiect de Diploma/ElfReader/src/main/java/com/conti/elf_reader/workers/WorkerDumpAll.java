package com.conti.elf_reader.workers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.conti.elf_reader.data_parsers.dwarf.data.cu.CompilationUnit;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_parsers.elf.data.*;
import com.conti.elf_reader.settings.Settings;
import com.utils.log.Logger;

public class WorkerDumpAll extends Worker {

	private final ElfFile elfFile;
	private final Path outputPath;

	public WorkerDumpAll(final ElfFile elfFile, final Path outputPath) {

		this.elfFile = elfFile;
		this.outputPath = outputPath;
	}

	@Override
	public void generateDataFile(final Settings settings) {

		try {
			final StringBuilder stringBuilder = new StringBuilder();

			Logger.printProgress("dumping the elf file header...");
			stringBuilder.append(elfFile.getElfHeader()).append(System.lineSeparator());

			Logger.printProgress("dumping the program headers...");
			stringBuilder.append(System.lineSeparator()).append("Program headers:")
					.append(System.lineSeparator()).append(System.lineSeparator());
			final int programHeaderEntriesLength = elfFile.getProgramHeaderEntries().size();
			for (int i = 0; i < programHeaderEntriesLength; i++) {

				final ProgramHeaderEntry programHeaderEntry = elfFile.getProgramHeaderEntries().get(i);
				stringBuilder.append(String.format("%-10s", i + "."));
				stringBuilder.append(programHeaderEntry.toString());
				stringBuilder.append(System.lineSeparator());
			}

			Logger.printProgress("dumping the section headers...");
			stringBuilder.append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator())
					.append("Section headers:").append(System.lineSeparator()).append(System.lineSeparator());
			final int sectionHeaderEntriesLength = elfFile.getSectionHeaderEntries().size();
			for (int i = 0; i < sectionHeaderEntriesLength; i++) {

				final SectionHeaderEntry sectionHeaderEntry = elfFile.getSectionHeaderEntries().get(i);
				stringBuilder.append(String.format("%-10s", i + "."));
				stringBuilder.append(sectionHeaderEntry.toString());
				stringBuilder.append(System.lineSeparator());
			}

			Logger.printProgress("dumping the symbols table...");
			stringBuilder.append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator())
					.append("Symbols table:").append(System.lineSeparator()).append(System.lineSeparator());
			final int symbolsTableLength = elfFile.getSymbolTableEntries().size();
			for (int i = 0; i < symbolsTableLength; i++) {

				final SymbolTableEntryRow symbolTableEntry = elfFile.getSymbolTableEntries().get(i);
				stringBuilder.append(String.format("%-10s", i + "."));
				stringBuilder.append(symbolTableEntry.toString());
				stringBuilder.append(System.lineSeparator());
			}

			Logger.printProgress("dumping the copy table...");
			stringBuilder.append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator())
					.append("Copy table:").append(System.lineSeparator()).append(System.lineSeparator());
			final int copyTableEntriesLength = elfFile.getCopyTableEntries().size();
			for (int i = 0; i < copyTableEntriesLength; i++) {

				final CopyTableEntry copyTableEntry = elfFile.getCopyTableEntries().get(i);
				stringBuilder.append(String.format("%-10s", i + "."));
				stringBuilder.append(copyTableEntry.toString());
				stringBuilder.append(System.lineSeparator());
			}

			Logger.printProgress("dumping the assembly code sections...");
			stringBuilder.append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator())
					.append("Assembly code sections table:").append(System.lineSeparator()).append(System
							.lineSeparator());
			final int assemblyCodeSectionsLength = elfFile.getAssemblyCodeSections().size();
			for (int i = 0; i < assemblyCodeSectionsLength; i++) {

				final AssemblyCodeSection assemblyCodeSection = elfFile.getAssemblyCodeSections().get(i);
				stringBuilder.append(System.lineSeparator()).append(System.lineSeparator());
				stringBuilder.append(String.format("%-10s", i + "."));
				stringBuilder.append(assemblyCodeSection.toString());
				stringBuilder.append(System.lineSeparator());
			}

			Logger.printProgress("dumping the debug information...");
			stringBuilder.append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator())
					.append("Debug information:").append(System.lineSeparator()).append(System.lineSeparator());
			final List<CompilationUnit> compilationUnits = elfFile.getDwarfData().getCompilationUnits();
			for (int i = 0; i < elfFile.getDwarfData().getCompilationUnits().size(); i++) {

				final CompilationUnit compilationUnit = compilationUnits.get(i);
				stringBuilder.append(System.lineSeparator()).append(System.lineSeparator());
				stringBuilder.append(String.format("%-10s", i + "."));
				stringBuilder.append(compilationUnit.toString());
				stringBuilder.append(System.lineSeparator());
			}

			Logger.printStatus("Finished dumping the elf file.");

			Files.write(outputPath, stringBuilder.toString().getBytes());

		} catch (final Exception exc) {
			Logger.printError("failed to generate the output file!");
			Logger.printException(exc);
		}
	}
}
