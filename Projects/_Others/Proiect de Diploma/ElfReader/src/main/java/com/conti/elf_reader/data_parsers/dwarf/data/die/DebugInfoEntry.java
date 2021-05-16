package com.conti.elf_reader.data_parsers.dwarf.data.die;

import java.util.List;

import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwTagType;

public class DebugInfoEntry {

	private final int offset;
	private final DwTagType dwTagType;
	private final DwarfSymbol dwarfSymbol;
	private final List<DebugInfoEntry> children;

	DebugInfoEntry(
			final int offset, final DwTagType dwTagType, final DwarfSymbol dwarfSymbol,
			final List<DebugInfoEntry> children) {

		this.offset = offset;
		this.dwTagType = dwTagType;
		this.dwarfSymbol = dwarfSymbol;
		this.children = children;
	}

	public String toString(final String indent) {

		final StringBuilder debugInfoEntryToString = new StringBuilder();
		debugInfoEntryToString.append(System.lineSeparator()).append(indent).append("Debugging Information Entry");

		debugInfoEntryToString.append("     offset: ").append(offset);

		if (dwTagType != null) {
			debugInfoEntryToString.append("     tag: ").append(dwTagType);
		}

		if (dwarfSymbol != null) {
			debugInfoEntryToString.append(dwarfSymbol);
		}

		for (final DebugInfoEntry child : children) {
			debugInfoEntryToString.append(child.toString(indent + "  "));
		}

		return debugInfoEntryToString.toString();
	}
}
