package com.conti.elf_reader.data_parsers.dwarf.data.abbrev;

import java.util.List;

import com.conti.elf_reader.data_parsers.dwarf.data.abbrev.entries.AbbrevEntry;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwTagType;

public class Abbrev {

	private final int number;
	private final boolean hasChildren;
	private final DwTagType dwTagType;
	private final List<AbbrevEntry> abbrevEntries;

	Abbrev(
			final int number, final boolean hasChildren, final DwTagType dwTagType,
			final List<AbbrevEntry> abbrevEntries) {

		this.number = number;
		this.hasChildren = hasChildren;
		this.dwTagType = dwTagType;
		this.abbrevEntries = abbrevEntries;
	}

	@Override
	public String toString() {
		return dwTagType.name();
	}

	public DwTagType getDwTagType() {
		return dwTagType;
	}

	public int getNumber() {
		return number;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public List<AbbrevEntry> getAbbrevEntries() {
		return abbrevEntries;
	}
}
