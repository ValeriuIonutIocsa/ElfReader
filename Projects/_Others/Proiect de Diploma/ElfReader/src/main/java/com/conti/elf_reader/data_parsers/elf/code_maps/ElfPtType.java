package com.conti.elf_reader.data_parsers.elf.code_maps;

public enum ElfPtType {

    PT_NULL(0),
    PT_LOAD(1),
    PT_DYNAMIC(2),
    PT_INTERP(3),
    PT_NOTE(4),
    PT_SHLIB(5),
    PT_PHDR(6),
    PT_LOPROC(0x70000000),
    PT_HIPROC(0x7fffffff);

    private final int value;

    ElfPtType(int value) {
        this.value = value;
    }

    public static ElfPtType byValue(int value) {

        for (ElfPtType elfPtType : ElfPtType.values()) {
            if (elfPtType.value == value)
                return elfPtType;
        }
        return null;
    }
}
