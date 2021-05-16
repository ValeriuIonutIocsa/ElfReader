package com.conti.elf_reader.data_parsers.dwarf.data.abbrev;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.entries.AbbrevEntry;
import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.entries.FactoryAbbrevEntry;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwTagType;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class FactoryAbbrev {

	public static Abbrev parse(final ByteBuffer byteBuffer) {

		final int number = (int) DataTypes.parseUnsignedLittleEndianBase128(byteBuffer);
		if (number == 0) {
			return new Abbrev(number, false, null, null);
		}

		final int dwTagTypeValue = (int) DataTypes.parseUnsignedLittleEndianBase128(byteBuffer);
		final DwTagType dwTagType = DwTagType.byValue(dwTagTypeValue);
		final boolean hasChildren = !(byteBuffer.get() == 0);

		final List<AbbrevEntry> abbrevEntries = new ArrayList<>();
		while (true) {

			final AbbrevEntry abbrevEntry = FactoryAbbrevEntry.parse(byteBuffer);
			if (abbrevEntry == null) {
				break;
			}

			abbrevEntries.add(abbrevEntry);
		}

		return new Abbrev(number, hasChildren, dwTagType, abbrevEntries);
	}
}
