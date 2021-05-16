package com.conti.elf_reader.data_parsers.elf;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.*;

import com.conti.elf_reader.data_info.DataInfo;
import com.conti.elf_reader.data_info.DataInfoDumpAll;
import com.conti.elf_reader.data_info.DataInfoSymbolsToSectionsMap;
import com.conti.elf_reader.data_info.tables.DataInfoSections;
import com.conti.elf_reader.data_info.tables.DataInfoSelfStack;
import com.conti.elf_reader.data_info.tables.DataInfoSize;
import com.conti.elf_reader.data_info.tables.DataInfoSymbols;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeMemories;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRegular;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRunnables;
import com.conti.elf_reader.data_parsers.dwarf.DwarfData;
import com.conti.elf_reader.data_parsers.dwarf.FactoryDwarfData;
import com.conti.elf_reader.data_parsers.elf.data.*;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.utils.log.Logger;

public class ElfFile {

	private Path elfFilePath;
	private final long elfFileOffset;
	private Path readElfFilePath = null;
	private FileTime readLastModifiedTime = null;

	private ElfHeader elfHeader = null;
	private final List<ProgramHeaderEntry> programHeaderEntries = new ArrayList<>();
	private final List<SectionHeaderEntry> sectionHeaderEntries = new ArrayList<>();
	private final List<SymbolTableEntryRow> symbolTableEntries = new ArrayList<>();
	private final List<CopyTableEntry> copyTableEntries = new ArrayList<>();
	private final List<AssemblyCodeSection> assemblyCodeSections = new ArrayList<>();
	private final Map<Long, String> functionSymbolsAddressNameMap = new HashMap<>();
	private final Map<Long, List<Long>> copyTableSymbolAddressMap = new HashMap<>();
	private DwarfData dwarfData = null;

	public ElfFile(final Path elfFilePath) {

		this(elfFilePath, 0);
	}

	public ElfFile(final Path elfFilePath, final long elfFileOffset) {

		this.elfFilePath = elfFilePath;
		this.elfFileOffset = elfFileOffset;
	}

	public void readFile(final DataInfo dataInfo) throws Exception {
		readFile(dataInfo, true);
	}

	public void readFile(final DataInfo dataInfo, final boolean verbose) throws Exception {

		if (dataInfo.equals(DataInfoSymbolsToSectionsMap.getInstance())) {
			return;
		}

		final FileTime lastModifiedTime = Files.getLastModifiedTime(elfFilePath);
		if (readElfFilePath == null ||
				!readElfFilePath.equals(elfFilePath) || !readLastModifiedTime.equals(lastModifiedTime)) {
			readElfFilePath = elfFilePath;
			readLastModifiedTime = lastModifiedTime;
			clearData();
		}

		if (verbose) {
			Logger.printProgress("reading ELF file:" + System.lineSeparator() + elfFilePath);
		}
		final FileChannel channel = FileChannel.open(elfFilePath, StandardOpenOption.READ);

		if (elfHeader == null) {
			if (verbose) {
				Logger.printProgress("reading the ELF file header...");
			}
			elfHeader = new ElfHeader(channel);
		}

		if (programHeaderEntries.isEmpty() &&
				dataInfo.equals(DataInfoDumpAll.getInstance())) {
			if (verbose) {
				Logger.printProgress("reading the program headers...");
			}
			readProgramHeaderEntries(channel);
		}

		if (sectionHeaderEntries.isEmpty() &&
				(dataInfo.equals(DataInfoSections.getInstance()) || dataInfo.equals(DataInfoSymbols.getInstance()) ||
						dataInfo.equals(DataInfoSelfStack.getInstance()) || dataInfo.equals(DataInfoSize
								.getInstance()) || dataInfo.equals(DataInfoCallTreeRegular.getInstance()) || dataInfo
										.equals(DataInfoCallTreeMemories.getInstance()) || dataInfo.equals(
												DataInfoCallTreeRunnables.getInstance()) || dataInfo.equals(
														DataInfoDumpAll.getInstance()))) {
			if (verbose) {
				Logger.printProgress("reading the section headers...");
			}
			readSectionHeaderEntries(channel);
		}

		if (symbolTableEntries.isEmpty() &&
				(dataInfo.equals(DataInfoSymbols.getInstance()) || dataInfo.equals(DataInfoSelfStack.getInstance()) ||
						dataInfo.equals(DataInfoSize.getInstance()) || dataInfo.equals(DataInfoCallTreeRegular
								.getInstance()) || dataInfo.equals(DataInfoCallTreeMemories.getInstance()) || dataInfo
										.equals(DataInfoCallTreeRunnables.getInstance()) || dataInfo.equals(
												DataInfoDumpAll.getInstance()))) {
			if (verbose) {
				Logger.printProgress("reading the symbols table...");
			}
			readSymbolsTable(channel);
		}

		if (copyTableEntries.isEmpty() &&
				(dataInfo.equals(DataInfoCallTreeRegular.getInstance()) || dataInfo.equals(DataInfoCallTreeMemories
						.getInstance()) || dataInfo.equals(DataInfoCallTreeRunnables.getInstance()) || dataInfo.equals(
								DataInfoDumpAll.getInstance()))) {
			readCopyTable(channel);
		}

		if (assemblyCodeSections.isEmpty() &&
				(dataInfo.equals(DataInfoSelfStack.getInstance()) || dataInfo.equals(DataInfoCallTreeRegular
						.getInstance()) || dataInfo.equals(DataInfoCallTreeMemories.getInstance()) || dataInfo.equals(
								DataInfoCallTreeRunnables.getInstance()) || dataInfo.equals(DataInfoDumpAll
										.getInstance()))) {
			if (verbose) {
				Logger.printProgress("reading the assembly code sections...");
			}
			createFunctionSymbolsAddressNameMap();
			readAssemblyCodeSections(channel);
		}

		if (dwarfData == null &&
				(dataInfo.equals(DataInfoSize.getInstance()) || dataInfo.equals(DataInfoDumpAll.getInstance()))) {
			if (elfFilePath.toAbsolutePath().toString().endsWith(".elf")) {
				Logger.printProgress("reading the debug information sections...");
				readDwarfSections(channel);
			}
		}

		if (verbose) {
			Logger.printStatus("The .elf file has been read successfully.");
		}
		channel.close();
	}

	private void clearData() {

		elfHeader = null;
		programHeaderEntries.clear();
		sectionHeaderEntries.clear();
		symbolTableEntries.clear();
		copyTableEntries.clear();
		assemblyCodeSections.clear();
		functionSymbolsAddressNameMap.clear();
		copyTableEntries.clear();
		dwarfData = null;
	}

	private void readProgramHeaderEntries(final FileChannel channel) {

		try {
			final ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
			byteBuffer.order(elfHeader.getElfByteOrder());

			position(channel, elfHeader.getProgramHeaderOffset());
			byteBuffer.limit(elfHeader.getProgramHeaderEntrySize());

			programHeaderEntries.clear();
			for (int i = 0; i < elfHeader.getProgramHeaderEntryCount(); i++) {

				DataTypes.readFully(channel, byteBuffer);
				programHeaderEntries.add(new ProgramHeaderEntry(elfHeader.getElfClass(), byteBuffer));
			}

		} catch (final Exception ignored) {
			Logger.printError("failed to read the program header entries!");
		}
	}

	private void readSectionHeaderEntries(final FileChannel channel) {

		try {
			final ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
			byteBuffer.order(elfHeader.getElfByteOrder());

			final long sectionHeaderOffset = elfHeader.getSectionHeaderOffset();
			final int sectionHeaderEntrySize = elfHeader.getSectionHeaderEntrySize();
			final int nameTableSectionIndex = elfHeader.getNameTableSectionIndex();

			byteBuffer.limit(sectionHeaderEntrySize);
			position(channel, sectionHeaderOffset + nameTableSectionIndex * sectionHeaderEntrySize);
			DataTypes.readFully(channel, byteBuffer);
			final SectionHeaderEntry nameTableSection = SectionHeaderEntry.parse(byteBuffer, null);
			final ByteBuffer nameTableSectionByteBuffer = getSectionByteBuffer(channel, nameTableSection);

			sectionHeaderEntries.clear();
			position(channel, sectionHeaderOffset);

			long sectionHeaderEntryCount = elfHeader.getSectionHeaderEntryCount();
			if (sectionHeaderEntryCount == 0) {
				DataTypes.readFully(channel, byteBuffer);
				final SectionHeaderEntry firstSectionHeaderEntry = SectionHeaderEntry
						.parse(byteBuffer, nameTableSectionByteBuffer);
				sectionHeaderEntryCount = firstSectionHeaderEntry.getSize();
				sectionHeaderEntries.add(firstSectionHeaderEntry);
			}

			for (int i = 0; i < sectionHeaderEntryCount; i++) {

				DataTypes.readFully(channel, byteBuffer);
				sectionHeaderEntries.add(SectionHeaderEntry.parse(byteBuffer, nameTableSectionByteBuffer));
			}
		} catch (final Exception ignored) {
			Logger.printError("failed to read the section header entries!");
		}
	}

	private void readSymbolsTable(final FileChannel channel) {

		try {
			symbolTableEntries.clear();
			final ByteBuffer byteBuffer = getSectionByteBuffer(channel,
					Objects.requireNonNull(getSectionHeaderEntryByName(".symtab")));
			final ByteBuffer strTabByteBuffer = getSectionByteBuffer(channel,
					Objects.requireNonNull(getSectionHeaderEntryByName(".strtab")));
			while (byteBuffer.remaining() >= 16) {
				symbolTableEntries.add(SymbolTableEntryRow.parse(byteBuffer, strTabByteBuffer));
			}

		} catch (final Exception exc) {
			Logger.printError("failed to read the symbols table!");
			Logger.printException(exc);
		}
	}

	private void readCopyTable(final FileChannel channel) {

		try {
			copyTableEntries.clear();
			final ByteBuffer byteBufferTable = getSectionBufferByName(channel, "table");
			if (byteBufferTable == null) {
				return;
			}

			while (byteBufferTable.remaining() >= 4) {
				final CopyTableEntry copyTableEntry = CopyTableEntry.parse(byteBufferTable);
				if (copyTableEntry == null) {
					continue;
				}

				copyTableEntries.add(copyTableEntry);
			}

			createCopyTableSymbolAddressMap();

		} catch (final Exception exc) {
			Logger.printError("failed to create the Copy Table symbol address map!");
			Logger.printException(exc);
		}
	}

	private void readAssemblyCodeSections(final FileChannel channel) {

		assemblyCodeSections.clear();
		for (final SectionHeaderEntry sectionHeaderEntry : sectionHeaderEntries) {

			if (!sectionHeaderEntry.isAssemblyCodeSection()) {
				continue;
			}

			readAssemblyCodeSection(channel, sectionHeaderEntry);
		}
	}

	private void readAssemblyCodeSection(final FileChannel channel, final SectionHeaderEntry sectionHeaderEntry) {

		try {
			final ByteBuffer sectionByteBuffer = getSectionByteBuffer(channel, sectionHeaderEntry);
			AssemblyCodeSection assemblyCodeSection = null;
			switch (elfHeader.getMachineType()) {

				case TriCore:
					assemblyCodeSection = new AssemblyCodeSectionTriCore(
							sectionByteBuffer, sectionHeaderEntry,
							functionSymbolsAddressNameMap, copyTableSymbolAddressMap);
					break;

				case PowerPC:
					assemblyCodeSection = new AssemblyCodeSectionPowerPC(
							sectionByteBuffer, sectionHeaderEntry,
							functionSymbolsAddressNameMap, copyTableSymbolAddressMap);
					break;
			}

			assemblyCodeSections.add(assemblyCodeSection);

		} catch (final Exception ignored) {
			Logger.printError("failed to read Assembly Code section: " + sectionHeaderEntry.getName() + "!");
		}
	}

	private void readDwarfSections(final FileChannel channel) {

		try {
			dwarfData = FactoryDwarfData.parse(true, this, channel);
		} catch (final Exception ignored) {
			Logger.printError("failed to read the debug information (DWARF)!");
		}
	}

	private void createFunctionSymbolsAddressNameMap() {

		try {
			functionSymbolsAddressNameMap.clear();
			for (final SymbolTableEntryRow symbolTableEntry : symbolTableEntries) {

				final long size = symbolTableEntry.getSize();
				if (size == 0) {
					continue;
				}

				final boolean compilerLabel = symbolTableEntry.isCompilerLabel();
				if (compilerLabel) {
					continue;
				}

				final int symbolInfo = symbolTableEntry.getInfo().getValue();
				if (symbolInfo == 2 || symbolInfo == 18) {
					final long address = symbolTableEntry.getAddress().getValue();
					final String name = symbolTableEntry.getName();
					functionSymbolsAddressNameMap.put(address, name);
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to create the address to symbol name map!");
			Logger.printException(exc);
		}
	}

	private void createCopyTableSymbolAddressMap() {

		copyTableSymbolAddressMap.clear();
		for (final CopyTableEntry copyTableEntry : copyTableEntries) {

			final long action = copyTableEntry.getAction();
			if (action != 1) {
				continue;
			}

			final long destinationAddress = copyTableEntry.getDestinationAddress();
			final long cloningDestinationAddress = copyTableEntry.getCloningDestinationAddress();
			final long sourceAddress = copyTableEntry.getSourceAddress();
			final long length = copyTableEntry.getLength();
			for (int i = 0; i < length; i++) {

				final long copiedSourceAddress = sourceAddress + i;
				List<Long> destinationAddressList = copyTableSymbolAddressMap
						.getOrDefault(copiedSourceAddress, null);
				if (destinationAddressList == null) {
					destinationAddressList = new ArrayList<>();
					copyTableSymbolAddressMap.put(copiedSourceAddress, destinationAddressList);
				}
				destinationAddressList.add(destinationAddress + i);
				if (cloningDestinationAddress != -1) {
					destinationAddressList.add(cloningDestinationAddress + i);
				}
			}
		}
	}

	private SectionHeaderEntry getSectionHeaderEntryByName(final String sectionName) {

		for (final SectionHeaderEntry sectionHeaderEntry : sectionHeaderEntries) {
			if (sectionHeaderEntry.getName() != null && sectionHeaderEntry.getName().equals(sectionName)) {
				return sectionHeaderEntry;
			}
		}
		return null;
	}

	public ByteBuffer getSectionBufferByName(final FileChannel channel, final String sectionName) throws Exception {

		final SectionHeaderEntry sectionHeaderEntry = getSectionHeaderEntryByName(sectionName);
		if (sectionHeaderEntry == null) {
			return null;
		}

		return getSectionByteBuffer(channel, sectionHeaderEntry);
	}

	private ByteBuffer getSectionByteBuffer(final FileChannel channel, final SectionHeaderEntry section)
			throws Exception {

		final ByteBuffer byteBuffer = ByteBuffer.allocate(((Long) section.getSize()).intValue());
		byteBuffer.order(elfHeader.getElfByteOrder());

		position(channel, section.getFileOffset().getValue());
		DataTypes.readFully(channel, byteBuffer);

		return byteBuffer;
	}

	private void position(final FileChannel channel, final long elfFileOffset) throws Exception {

		channel.position(elfHeader.getMagicNumberOffset() + elfFileOffset + this.elfFileOffset);
	}

	public void setElfFilePath(final Path elfFilePath) {
		this.elfFilePath = elfFilePath;
	}

	public Path getElfFilePath() {
		return elfFilePath;
	}

	public ElfHeader getElfHeader() {
		return elfHeader;
	}

	public List<ProgramHeaderEntry> getProgramHeaderEntries() {
		return programHeaderEntries;
	}

	public List<SectionHeaderEntry> getSectionHeaderEntries() {
		return sectionHeaderEntries;
	}

	public List<SymbolTableEntryRow> getSymbolTableEntries() {
		return symbolTableEntries;
	}

	public List<CopyTableEntry> getCopyTableEntries() {
		return copyTableEntries;
	}

	public Map<Long, String> getFunctionSymbolsAddressNameMap() {
		return functionSymbolsAddressNameMap;
	}

	public Map<Long, List<Long>> getCopyTableSymbolAddressMap() {
		return copyTableSymbolAddressMap;
	}

	public List<AssemblyCodeSection> getAssemblyCodeSections() {
		return assemblyCodeSections;
	}

	public DwarfData getDwarfData() {
		return dwarfData;
	}
}
