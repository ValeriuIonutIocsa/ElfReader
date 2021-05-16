package com.conti.elf_reader.data_parsers.dwarf;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.Abbrev;
import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.FactoryAbbrev;
import com.conti.elf_reader.data_parsers.dwarf.data.cu.CompilationUnit;
import com.conti.elf_reader.data_parsers.dwarf.data.cu.FactoryCompilationUnit;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_parsers.elf.data.SymbolTableEntryRow;
import com.conti.elf_reader.utils.data_types.SymbolInfo;

public class FactoryDwarfData {

	public static DwarfData parse(
			final boolean dumpAll, final ElfFile elfFile, final FileChannel channel) throws Exception {

		final List<DwarfSymbol> dwarfSymbols = new ArrayList<>();
		final List<CompilationUnit> compilationUnits = new ArrayList<>();

		final Set<String> globalSymbolNames = new HashSet<>();
		final List<SymbolTableEntryRow> elfSymbols = elfFile.getSymbolTableEntries();
		for (final SymbolTableEntryRow elfSymbol : elfSymbols) {

			final SymbolInfo symbolInfo = elfSymbol.getInfo();
			final int symbolInfoValue = symbolInfo.getValue();
			if (symbolInfoValue == 17 || symbolInfoValue == 18) {
				final String symbolName = elfSymbol.getName();
				globalSymbolNames.add(symbolName);
			}
		}

		final Map<Integer, Map<Integer, Abbrev>> abbrevMap = new HashMap<>();
		parseDebugAbbrev(elfFile, channel, abbrevMap);

		parseDebugInfo(dumpAll, elfFile, channel, globalSymbolNames, abbrevMap,
				dwarfSymbols, compilationUnits);

		return new DwarfData(dwarfSymbols, dumpAll, compilationUnits);
	}

	private static void parseDebugAbbrev(
			final ElfFile elfFile, final FileChannel channel,
			final Map<Integer, Map<Integer, Abbrev>> abbrevSequences) throws Exception {

		final ByteBuffer byteBuffer = elfFile.getSectionBufferByName(channel, ".debug_abbrev");
		if (byteBuffer == null) {
			return;
		}

		Map<Integer, Abbrev> abbrevSequence = new HashMap<>();
		int sequenceOffset = 0;
		while (byteBuffer.hasRemaining()) {

			final Abbrev abbrev = FactoryAbbrev.parse(byteBuffer);
			if (abbrev.getNumber() == 0) {
				abbrevSequences.put(sequenceOffset, abbrevSequence);
				sequenceOffset = byteBuffer.position();
				abbrevSequence = new HashMap<>();
			}

			abbrevSequence.put(abbrev.getNumber(), abbrev);
		}

		abbrevSequences.put(sequenceOffset, abbrevSequence);
	}

	private static void parseDebugInfo(
			final boolean dumpAll, final ElfFile elfFile, final FileChannel channel,
			final Set<String> globalSymbolNames, final Map<Integer, Map<Integer, Abbrev>> abbrevSequences,
			final List<DwarfSymbol> dwarfSymbols, final List<CompilationUnit> compilationUnits) throws Exception {

		final ByteBuffer debugInfoByteBuffer = elfFile.getSectionBufferByName(channel, ".debug_info");
		if (debugInfoByteBuffer == null) {
			return;
		}

		final Map<Integer, CompilationUnit> compilationUnitsByRootDIEOffsetMap = new HashMap<>();
		fillCompilationUnitsByRootDIEOffsetMap(
				debugInfoByteBuffer, abbrevSequences, compilationUnitsByRootDIEOffsetMap);

		final ByteBuffer debugStrByteBuffer = elfFile.getSectionBufferByName(channel, ".debug_str");

		final Map<Integer, List<Integer>> compilationUnitImportsMap = new HashMap<>();
		fillCompilationUnitImportsMap(debugInfoByteBuffer, debugStrByteBuffer,
				compilationUnitsByRootDIEOffsetMap, compilationUnitImportsMap);

		parseDebugInfoEntries(dumpAll, debugInfoByteBuffer, debugStrByteBuffer,
				compilationUnitsByRootDIEOffsetMap, compilationUnitImportsMap, globalSymbolNames, dwarfSymbols);

		if (dumpAll) {
			compilationUnits.addAll(compilationUnitsByRootDIEOffsetMap.values());
		}
	}

	private static void fillCompilationUnitsByRootDIEOffsetMap(
			final ByteBuffer debugInfoByteBuffer, final Map<Integer, Map<Integer, Abbrev>> abbrevSequences,
			final Map<Integer, CompilationUnit> compilationUnitsByRootDIEOffsetMap) {

		while (debugInfoByteBuffer.remaining() > 11) {

			final int offset = debugInfoByteBuffer.position();
			final CompilationUnit compilationUnit = FactoryCompilationUnit
					.parse(offset, debugInfoByteBuffer, abbrevSequences);

			final int rootDIEOffset = compilationUnit.getRootDIEOffset();
			compilationUnitsByRootDIEOffsetMap.put(rootDIEOffset, compilationUnit);
		}
	}

	private static void fillCompilationUnitImportsMap(
			final ByteBuffer debugInfoByteBuffer, final ByteBuffer debugStrByteBuffer,
			final Map<Integer, CompilationUnit> compilationUnitsByRootDIEOffsetMap,
			final Map<Integer, List<Integer>> compilationUnitImportsMap) {

		final Collection<CompilationUnit> compilationUnits = compilationUnitsByRootDIEOffsetMap.values();
		for (final CompilationUnit compilationUnit : compilationUnits) {

			compilationUnit.parseImportedUnit(
					debugInfoByteBuffer, debugStrByteBuffer, compilationUnitImportsMap);
		}
	}

	private static void parseDebugInfoEntries(
			final boolean dumpAll, final ByteBuffer debugInfoByteBuffer, final ByteBuffer debugStrByteBuffer,
			final Map<Integer, CompilationUnit> compilationUnitsByRootDIEOffsetMap,
			final Map<Integer, List<Integer>> compilationUnitImportsMap,
			final Set<String> globalSymbolNames, final List<DwarfSymbol> dwarfSymbols) {

		final Map<Integer, Integer> typeReferenceMap = new HashMap<>();
		final Map<Integer, Integer> typeByteSizeMap = new HashMap<>();
		final Collection<CompilationUnit> compilationUnits = compilationUnitsByRootDIEOffsetMap.values();
		for (final CompilationUnit compilationUnit : compilationUnits) {

			final boolean imported = compilationUnit.isImported();
			if (imported) {
				continue;
			}

			typeReferenceMap.clear();
			typeByteSizeMap.clear();
			compilationUnit.parseDebugInfoEntries(dumpAll, debugInfoByteBuffer, debugStrByteBuffer,
					typeReferenceMap, typeByteSizeMap, globalSymbolNames, dwarfSymbols);

			final int rootDIEOffset = compilationUnit.getRootDIEOffset();
			final List<Integer> extendedUnitOffsets = compilationUnitImportsMap
					.getOrDefault(rootDIEOffset, null);
			if (extendedUnitOffsets != null) {
				for (final int extendedUnitOffset : extendedUnitOffsets) {

					final CompilationUnit importedCompilationUnit =
							compilationUnitsByRootDIEOffsetMap.get(extendedUnitOffset);
					importedCompilationUnit.parseDebugInfoEntries(dumpAll, debugInfoByteBuffer, debugStrByteBuffer,
							typeReferenceMap, typeByteSizeMap, globalSymbolNames, dwarfSymbols);
				}
			}
		}
	}
}
