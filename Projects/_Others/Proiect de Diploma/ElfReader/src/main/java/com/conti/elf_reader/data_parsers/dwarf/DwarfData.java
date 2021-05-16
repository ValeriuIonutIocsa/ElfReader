package com.conti.elf_reader.data_parsers.dwarf;

import java.util.List;

import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.dwarf.data.cu.CompilationUnit;

public class DwarfData {

	private final List<DwarfSymbol> dwarfSymbols;

	private final boolean dumpAll;
	private final List<CompilationUnit> compilationUnits;

	DwarfData(
			final List<DwarfSymbol> dwarfSymbols, final boolean dumpAll,
			final List<CompilationUnit> compilationUnits) {

		this.dwarfSymbols = dwarfSymbols;
		this.dumpAll = dumpAll;
		this.compilationUnits = compilationUnits;
	}

	public List<DwarfSymbol> getDwarfSymbols() {
		return dwarfSymbols;
	}

	public boolean isDumpAll() {
		return dumpAll;
	}

	public List<CompilationUnit> getCompilationUnits() {
		return compilationUnits;
	}
}
