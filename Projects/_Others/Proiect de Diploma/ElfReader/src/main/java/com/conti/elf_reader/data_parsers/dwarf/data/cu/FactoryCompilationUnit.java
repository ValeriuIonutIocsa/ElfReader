package com.conti.elf_reader.data_parsers.dwarf.data.cu;

import java.nio.ByteBuffer;
import java.util.Map;

import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.Abbrev;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwTagType;

public class FactoryCompilationUnit {

	public static CompilationUnit parse(
			final int offset, final ByteBuffer byteBuffer,
			final Map<Integer, Map<Integer, Abbrev>> abbrevSequences) {

		int length = byteBuffer.getInt();
		byteBuffer.getShort();

		final int abbrevOffset = byteBuffer.getInt();
		final Map<Integer, Abbrev> abbrevSequence = abbrevSequences.get(abbrevOffset);

		final boolean imported = computeImported(abbrevSequence);

		byteBuffer.get();

		int initialLengthSize = 4;
		if (length == 0xffffffff) {
			length = (int) byteBuffer.getLong();
			initialLengthSize = 12;
		}

		final int rootDIEOffset = byteBuffer.position();

		final int nextCompilationUnitOffset = offset + length + initialLengthSize;
		byteBuffer.position(nextCompilationUnitOffset);

		return new CompilationUnit(offset, length, abbrevSequence, imported, rootDIEOffset);
	}

	private static boolean computeImported(final Map<Integer, Abbrev> abbrevSequence) {

		final Abbrev secondAbbrev = abbrevSequence.getOrDefault(2, null);
		return secondAbbrev.getDwTagType() == DwTagType.DW_TAG_imported_unit;
	}
}
