package com.conti.elf_reader.data_parsers.elf.code_maps;

public enum ElfMachineType {

    PowerPC(20),
    TriCore(44);

    private final int value;

    ElfMachineType(int value) {
        this.value = value;
    }

    public static ElfMachineType byValue(int value) {

        for (ElfMachineType elfMachineType : ElfMachineType.values()) {
            if (elfMachineType.value == value)
                return elfMachineType;
        }
        return null;
    }
}
