package com.conti.elf_reader.data_parsers.dwarf.data.die;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.entries.AbbrevEntry;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwTagType;
import com.conti.elf_reader.utils.data_types.ObjectWrapper;

public class FactoryDebugInfoEntry extends FactoryDebugInfoEntryAbstr<DebugInfoEntry> {

	public static final FactoryDebugInfoEntry INSTANCE = new FactoryDebugInfoEntry();

	private FactoryDebugInfoEntry() {
	}

	@Override
    void resetAbbrevValues() {

		AbbrevEntry.nameWrapper.setValue(null);
		AbbrevEntry.startAddressWrapper.setValue(-1L);
		AbbrevEntry.endAddressWrapper.setValue(-1L);
		AbbrevEntry.typeDieOffsetWrapper.setValue(-1);
		AbbrevEntry.isExternalWrapper.setValue(false);
		AbbrevEntry.isDeclarationWrapper.setValue(false);
		AbbrevEntry.declarationFileIndexWrapper.setValue(-1);
	}

	@Override
    DebugInfoEntry analyzeAbbrevValues(
			final DwTagType dwTagType, final String compilationUnitName, final int offset,
			final Map<Integer, Integer> typeReferenceMap, final Map<Integer, Integer> typeByteSizeMap,
			final Set<String> globalSymbolNames, final List<DwarfSymbol> dwarfSymbols,
			final ObjectWrapper<String> compilationUnitNameWrapper,
			final ObjectWrapper<DwarfSymbol> dwarfSymbolWrapper) {

		final String name = AbbrevEntry.nameWrapper.getValue();

		if (dwTagType == DwTagType.DW_TAG_compile_unit) {
			compilationUnitNameWrapper.setValue(name);

		} else if (dwTagType == DwTagType.DW_TAG_pointer_type) {
			typeByteSizeMap.put(offset, 4);

		} else {
			final DwarfSymbol dwarfSymbol = addDwarfSymbol(name, compilationUnitName,
					typeReferenceMap, typeByteSizeMap, globalSymbolNames, dwarfSymbols);
			dwarfSymbolWrapper.setValue(dwarfSymbol);
		}
		return null;
	}

	private static DwarfSymbol addDwarfSymbol(
			final String dwarfSymbolName, final String compilationUnitName,
			final Map<Integer, Integer> typeReferenceMap, final Map<Integer, Integer> typeByteSizeMap,
			final Set<String> globalSymbolNames, final List<DwarfSymbol> dwarfSymbols) {

		final int declarationFileIndex = AbbrevEntry.declarationFileIndexWrapper.getValue();
		if (declarationFileIndex < 0) {
			return null;
		}

		final boolean fullDeclaration = isFullDeclaration(dwarfSymbolName, globalSymbolNames);
		if (!fullDeclaration) {
			return null;
		}

		final long startAddress = AbbrevEntry.startAddressWrapper.getValue();

		final int size = computeDwarfSymbolSize(typeReferenceMap, typeByteSizeMap);

		final DwarfSymbol dwarfSymbol =
				new DwarfSymbol(dwarfSymbolName, startAddress, size, compilationUnitName);
		dwarfSymbols.add(dwarfSymbol);
		return dwarfSymbol;
	}

	private static boolean isFullDeclaration(final String symbolName, final Set<String> globalSymbolNames) {

		if (StringUtils.isBlank(symbolName)) {
			return false;
		}

		final boolean globalSymbol = globalSymbolNames.contains(symbolName);
		if (!globalSymbol) {
			return true;
		}

		final boolean declaration = AbbrevEntry.isDeclarationWrapper.getValue();
		final boolean external = AbbrevEntry.isExternalWrapper.getValue();
		return external && !declaration;
	}

	private static int computeDwarfSymbolSize(
			final Map<Integer, Integer> typeReferenceMap, final Map<Integer, Integer> typeByteSizeMap) {

		final long startAddress = AbbrevEntry.startAddressWrapper.getValue();
		final long endAddress = AbbrevEntry.endAddressWrapper.getValue();
		if (endAddress > startAddress && startAddress >= 0) {
			final long size = endAddress - startAddress;
			return (int) size;
		}

		int typeDieOffset = AbbrevEntry.typeDieOffsetWrapper.getValue();
		if (typeDieOffset < 0) {
			return 0;
		}

		while (true) {

			final int byteSize = typeByteSizeMap.getOrDefault(typeDieOffset, -1);
			if (byteSize > 0) {
				return byteSize;
			}

			final int referredTypeDieOffset = typeReferenceMap.getOrDefault(typeDieOffset, -1);
			if (referredTypeDieOffset < 0) {
				return 0;
			}

			typeDieOffset = referredTypeDieOffset;
		}
	}

	@Override
    DebugInfoEntry createEntry(
            final int offset, final DwTagType dwTagType, final DwarfSymbol dwarfSymbol,
            final List<DebugInfoEntry> children) {

		return new DebugInfoEntry(offset, dwTagType, dwarfSymbol, children);
	}
}
