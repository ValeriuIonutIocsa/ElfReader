package com.conti.elf_reader.data_parsers.elf.code_maps;

public enum ElfType {

    ET_NONE(0),
    ET_REL(1),
    ET_EXEC(2),
    ET_DYN(3),
    ET_CORE(4),
    ET_LOPROC(0xff00),
    ET_HIPROC(0xffff);

    private final int value;

    ElfType(int value) {
        this.value = value;
    }

    public static ElfType byValue(int value) {

        for (ElfType elfType : ElfType.values()) {
            if (elfType.value == value)
                return elfType;
        }
        return null;
    }
}
