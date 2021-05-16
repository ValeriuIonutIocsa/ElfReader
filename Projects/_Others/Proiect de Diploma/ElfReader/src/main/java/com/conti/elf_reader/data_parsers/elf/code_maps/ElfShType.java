package com.conti.elf_reader.data_parsers.elf.code_maps;

public enum ElfShType {

    SHT_NULL(0),
    SHT_PROGBITS(1),
    SHT_SYMTAB(2),
    SHT_STRTAB(3),
    SHT_RELA(4),
    SHT_HASH(5),
    SHT_DYNAMIC(6),
    SHT_NOTE(7),
    SHT_NOBITS(8),
    SHT_REL(9),
    SHT_SHLIB(10),
    SHT_DYNSYM(11),
    SHT_LOPROC(0x70000000),
    SHT_HIPROC(0x7fffffff),
    SHT_LOUSER(0x80000000),
    SHT_HIUSER(0xffffffff);

    private final int value;

    ElfShType(int value) {
        this.value = value;
    }

    public static ElfShType byValue(int value) {

        for (ElfShType elfShType : ElfShType.values()) {
            if (elfShType.value == value)
                return elfShType;
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
