package com.conti.elf_reader.data_parsers.elf.code_maps;

public enum ElfClass {

    ELFCLASSNONE(0),
    ELFCLASS32(1),
    ELFCLASS64(2);

    private final int value;

    ElfClass(int value) {
        this.value = value;
    }

    public static ElfClass byValue(int value) {

        for (ElfClass elfMachineType : ElfClass.values()) {
            if (elfMachineType.value == value)
                return elfMachineType;
        }
        return null;
    }
}
