package com.conti.elf_reader.data_parsers.dwarf.data.die;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.entries.AbbrevEntry;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwTagType;
import com.conti.elf_reader.utils.data_types.ObjectWrapper;

public class FactoryImportedUnitOffset extends FactoryDebugInfoEntryAbstr<Integer> {

	public static final FactoryImportedUnitOffset INSTANCE = new FactoryImportedUnitOffset();

	private FactoryImportedUnitOffset() {
	}

	@Override
	void resetAbbrevValues() {

		AbbrevEntry.importedUnitOffsetWrapper.setValue(null);
	}

	@Override
	Integer analyzeAbbrevValues(
			final DwTagType dwTagType, final String compilationUnitNameParam, final int offset,
			final Map<Integer, Integer> typeReferenceMap, final Map<Integer, Integer> typeByteSizeMap,
			final Set<String> globalSymbolNames, final List<DwarfSymbol> dwarfSymbols,
			final ObjectWrapper<String> compilationUnitNameWrapper,
			final ObjectWrapper<DwarfSymbol> dwarfSymbolWrapper) {

		if (dwTagType == DwTagType.DW_TAG_imported_unit) {
			return AbbrevEntry.importedUnitOffsetWrapper.getValue();
		}
		return null;
	}

	@Override
	Integer createEntry(final int offset, final DwTagType dwTagType, final DwarfSymbol dwarfSymbol,
			final List<Integer> children) {

		for (final Integer child : children) {

			if (child != null) {
				return child;
			}
		}
		return null;
	}
}
