package com.conti.elf_reader.data_parsers.dwarf.data.die;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.Abbrev;
import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.entries.AbbrevEntry;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwTagType;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.conti.elf_reader.utils.data_types.ObjectWrapper;

public abstract class FactoryDebugInfoEntryAbstr<T> {

	public T parse(
			final ByteBuffer byteBuffer, final ByteBuffer debugStrByteBuffer,
			final String compilationUnitNameParam, final int compilationUnitAddress,
			final int offset, final Map<Integer, Abbrev> abbrevSequence, final int abbrevNumber,
			final Map<Integer, Integer> typeReferenceMap, final Map<Integer, Integer> typeByteSizeMap,
			final Set<String> globalSymbolNames, final List<DwarfSymbol> dwarfSymbols) throws Exception {

		final Abbrev abbrev = abbrevSequence.getOrDefault(abbrevNumber, null);
		if (abbrev == null) {
			return null;
		}

		resetAbbrevValues();

		final List<AbbrevEntry> abbrevEntries = abbrev.getAbbrevEntries();
		for (final AbbrevEntry abbrevEntry : abbrevEntries) {
			abbrevEntry.parseAttribute(byteBuffer, debugStrByteBuffer,
					compilationUnitAddress, offset, typeReferenceMap, typeByteSizeMap);
		}

		final DwTagType dwTagType = abbrev.getDwTagType();
		final ObjectWrapper<String> compilationUnitNameWrapper = new ObjectWrapper<>(compilationUnitNameParam);
		final ObjectWrapper<DwarfSymbol> dwarfSymbolWrapper = new ObjectWrapper<>();
		final T entry = analyzeAbbrevValues(
				dwTagType, compilationUnitNameParam, offset, typeReferenceMap, typeByteSizeMap,
				globalSymbolNames, dwarfSymbols, compilationUnitNameWrapper, dwarfSymbolWrapper);
		if (entry != null) {
			return entry;
		}

		final String compilationUnitName = compilationUnitNameWrapper.getValue();
		final DwarfSymbol dwarfSymbol = dwarfSymbolWrapper.getValue();

		final List<T> children = new ArrayList<>();
		final boolean hasChildren = abbrev.isHasChildren();
		if (hasChildren) {
			parseChildren(byteBuffer, debugStrByteBuffer, compilationUnitName, compilationUnitAddress,
					abbrevSequence, children, typeReferenceMap, typeByteSizeMap, globalSymbolNames, dwarfSymbols);
		}

		return createEntry(offset, dwTagType, dwarfSymbol, children);
	}

	abstract void resetAbbrevValues();

	abstract T analyzeAbbrevValues(
			DwTagType dwTagType, String compilationUnitNameParam, int offset, Map<Integer, Integer> typeReferenceMap,
			Map<Integer, Integer> typeByteSizeMap, Set<String> globalSymbolNames, List<DwarfSymbol> dwarfSymbols,
			ObjectWrapper<String> compilationUnitNameWrapper, ObjectWrapper<DwarfSymbol> dwarfSymbolWrapper);

	abstract T createEntry(
			int offset, DwTagType dwTagType, DwarfSymbol dwarfSymbol, List<T> children);

	private void parseChildren(
			final ByteBuffer byteBuffer, final ByteBuffer debugStrByteBuffer,
			final String compilationUnitName, final int compilationUnitAddress,
			final Map<Integer, Abbrev> abbrevSequence,
			final List<T> children, final Map<Integer, Integer> typeReferenceMap,
			final Map<Integer, Integer> typeByteSizeMap, final Set<String> globalSymbolNames,
			final List<DwarfSymbol> dwarfSymbols) throws Exception {

		while (true) {

			final int offset = byteBuffer.position();
			final int abbrevNumber = (int) DataTypes.parseUnsignedLittleEndianBase128(byteBuffer);
			if (abbrevNumber == 0) {
				break;
			}

			final T childEntry = parse(byteBuffer, debugStrByteBuffer,
					compilationUnitName, compilationUnitAddress, offset, abbrevSequence, abbrevNumber,
					typeReferenceMap, typeByteSizeMap, globalSymbolNames, dwarfSymbols);
			if (childEntry != null) {
				children.add(childEntry);
			}
		}
	}
}
