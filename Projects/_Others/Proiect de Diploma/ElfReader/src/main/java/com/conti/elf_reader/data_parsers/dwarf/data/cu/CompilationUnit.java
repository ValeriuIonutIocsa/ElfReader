package com.conti.elf_reader.data_parsers.dwarf.data.cu;

import java.nio.ByteBuffer;
import java.util.*;

import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.Abbrev;
import com.conti.elf_reader.data_parsers.dwarf.data.die.DebugInfoEntry;
import com.conti.elf_reader.data_parsers.dwarf.data.die.FactoryDebugInfoEntry;
import com.conti.elf_reader.data_parsers.dwarf.data.die.FactoryImportedUnitOffset;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.utils.log.Logger;

public class CompilationUnit {

	private final int offset;
	private final int length;
	private final Map<Integer, Abbrev> abbrevSequence;
	private final boolean imported;
	private final int rootDIEOffset;
	private DebugInfoEntry rootDebugInfoEntry;

	CompilationUnit(
			final int offset, final int length, final Map<Integer, Abbrev> abbrevSequence,
			final boolean imported, final int rootDIEOffset) {

		this.offset = offset;
		this.length = length;
		this.abbrevSequence = abbrevSequence;
		this.imported = imported;
		this.rootDIEOffset = rootDIEOffset;
	}

	public void parseImportedUnit(
			final ByteBuffer debugInfoByteBuffer, final ByteBuffer debugStrByteBuffer,
			final Map<Integer, List<Integer>> compilationUnitImportsMap) {

		if (!imported) {
			return;
		}

		try {
			debugInfoByteBuffer.position(rootDIEOffset);
			final int abbrevNumber = (int) DataTypes.parseUnsignedLittleEndianBase128(debugInfoByteBuffer);

			final Integer importedUnitOffset = FactoryImportedUnitOffset.INSTANCE.parse(
					debugInfoByteBuffer, debugStrByteBuffer,
					null, offset, rootDIEOffset, abbrevSequence, abbrevNumber,
					new HashMap<>(), new HashMap<>(), new HashSet<>(), null);
			if (importedUnitOffset != null) {
				List<Integer> extendedUnitOffsets = compilationUnitImportsMap
						.getOrDefault(importedUnitOffset, null);
				if (extendedUnitOffsets == null) {
					extendedUnitOffsets = new ArrayList<>();
					compilationUnitImportsMap.put(importedUnitOffset, extendedUnitOffsets);
				}
				extendedUnitOffsets.add(rootDIEOffset);
			}

		} catch (final Exception exc) {
			Logger.printError("failed to parse the Imported Unit for a Compilation Unit!");
			Logger.printException(exc);
		}
	}

	public void parseDebugInfoEntries(
			final boolean dumpAll, final ByteBuffer debugInfoByteBuffer, final ByteBuffer debugStrByteBuffer,
			final Map<Integer, Integer> typeReferenceMap, final Map<Integer, Integer> typeByteSizeMap,
			final Set<String> globalSymbolNames, final List<DwarfSymbol> dwarfSymbols) {

		try {
			debugInfoByteBuffer.position(rootDIEOffset);
			final int abbrevNumber = (int) DataTypes.parseUnsignedLittleEndianBase128(debugInfoByteBuffer);

			final DebugInfoEntry debugInfoEntry = FactoryDebugInfoEntry.INSTANCE.parse(
					debugInfoByteBuffer, debugStrByteBuffer,
					null, offset, rootDIEOffset, abbrevSequence, abbrevNumber,
					typeReferenceMap, typeByteSizeMap, globalSymbolNames, dwarfSymbols);
			if (dumpAll) {
				rootDebugInfoEntry = debugInfoEntry;
			}

		} catch (final Exception exc) {
			Logger.printError("failed to parse the Debug Information Entry tree for a Compilation Unit!");
			Logger.printException(exc);
		}
	}

	@Override
	public String toString() {

		return "Compilation Unit      " +
				String.format("%-20s", "offset: " + offset) +
				String.format("%-20s", "length: " + length) +
				System.lineSeparator() + (rootDebugInfoEntry != null ?
						rootDebugInfoEntry.toString("    ") : "");
	}

	public int getOffset() {
		return offset;
	}

	public boolean isImported() {
		return imported;
	}

	public int getRootDIEOffset() {
		return rootDIEOffset;
	}
}
