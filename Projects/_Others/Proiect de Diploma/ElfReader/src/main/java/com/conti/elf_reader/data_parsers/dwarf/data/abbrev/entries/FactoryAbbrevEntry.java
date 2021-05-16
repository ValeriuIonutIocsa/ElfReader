package com.conti.elf_reader.data_parsers.dwarf.data.abbrev.entries;

import java.nio.ByteBuffer;

import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwAtType;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwFormType;
import com.conti.elf_reader.utils.data_types.DataTypes;

public class FactoryAbbrevEntry {

	public static AbbrevEntry parse(final ByteBuffer buffer) {

		final DwAtType dwAtType = DwAtType.byValue((int) DataTypes.parseUnsignedLittleEndianBase128(buffer));
		final DwFormType dwFormType = DwFormType.byValue((int) DataTypes.parseUnsignedLittleEndianBase128(buffer));
		if ((dwAtType == null || dwAtType.value() == 0) && (dwFormType == null || dwFormType.value() == 0)) {
			return null;
		}

		return new AbbrevEntry(dwAtType, dwFormType);
	}
}
