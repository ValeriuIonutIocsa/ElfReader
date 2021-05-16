package com.conti.elf_reader.data_parsers.core_architecture.data;

import com.conti.elf_reader.utils.data_types.HexString;

public class Memory {

    private final String name;
    private final HexString startAddress;
    private final HexString endAddress;

    public Memory(String name, HexString startAddress, HexString endAddress) {

        this.name = name;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }

    public String getName() {
        return name;
    }

    public boolean isInsideMemory(HexString address) {
        return startAddress.compareTo(address) < 0 && address.compareTo(endAddress) < 0;
    }
}
