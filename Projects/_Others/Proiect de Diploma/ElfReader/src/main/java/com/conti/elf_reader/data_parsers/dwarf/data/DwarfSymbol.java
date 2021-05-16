package com.conti.elf_reader.data_parsers.dwarf.data;

import com.conti.elf_reader.utils.data_types.DataTypes;

public class DwarfSymbol {

	private final String name;
	private final long startAddress;
	private final int size;
	private final String file;
	private boolean mappedToElfSymbol;

	public DwarfSymbol(final String name, final long startAddress, final int size, final String file) {

		this.name = name;
		this.startAddress = startAddress;
		this.size = size;
		this.file = file;
	}

	@Override
	public String toString() {

		String str = "";
		if (name != null) {
			str += "     name: " + name;
		}
		if (startAddress >= 0) {
			str += "     address: " + DataTypes.hexString(startAddress);
		}
		if (size >= 0) {
			str += "     size: " + size;
		}
		if (file != null) {
			str += "     file: " + file;
		}
		return str;
	}

	public String getName() {
		return name;
	}

	public long getStartAddress() {
		return startAddress;
	}

	public int getSize() {
		return size;
	}

	public String getFile() {
		return file;
	}

	public void setMappedToElfSymbol() {
		this.mappedToElfSymbol = true;
	}

	public boolean isMappedToElfSymbol() {
		return mappedToElfSymbol;
	}
}
