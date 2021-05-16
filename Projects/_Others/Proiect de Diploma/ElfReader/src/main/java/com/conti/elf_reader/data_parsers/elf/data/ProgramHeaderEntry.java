package com.conti.elf_reader.data_parsers.elf.data;

import com.conti.elf_reader.data_parsers.elf.code_maps.ElfClass;
import com.conti.elf_reader.data_parsers.elf.code_maps.ElfPtType;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProgramHeaderEntry {

    private final ElfPtType type;
    private final int flags;
    private final long offset;
    private final long virtualAddress;
    private final long physicalAddress;
    private final long segmentFileSize;
    private final long segmentMemorySize;
    private final long segmentAlignment;

    public ProgramHeaderEntry(ElfClass elfClass, ByteBuffer buf) throws IOException {

        type = ElfPtType.byValue(buf.getInt());

        switch (elfClass) {

            case ELFCLASS32:
                offset = buf.getInt() & 0xFFFFFFFFL;
                virtualAddress = buf.getInt() & 0xFFFFFFFFL;
                physicalAddress = buf.getInt() & 0xFFFFFFFFL;
                segmentFileSize = buf.getInt() & 0xFFFFFFFFL;
                segmentMemorySize = buf.getInt() & 0xFFFFFFFFL;
                flags = (int) (buf.getInt() & 0xFFFFFFFFL);
                segmentAlignment = buf.getInt() & 0xFFFFFFFFL;
                break;

            case ELFCLASS64:
                flags = (int) (buf.getInt() & 0xFFFFFFFFL);
                offset = buf.getLong();
                virtualAddress = buf.getLong();
                physicalAddress = buf.getLong();
                segmentFileSize = buf.getLong();
                segmentMemorySize = buf.getLong();
                segmentAlignment = buf.getLong();
                break;

            default:
                throw new IOException("ERROR - unknown elf class");
        }
    }

    @Override
    public String toString() {

        return String.format("%-25s", "type: " + type) +
                String.format("%-22s", "f_offs: 0x" + Long.toHexString(offset)) +
                String.format("%-22s", "v_addr: 0x" + Long.toHexString(virtualAddress)) +
                String.format("%-22s", "p_addr: 0x" + Long.toHexString(physicalAddress)) +
                String.format("%-15s", "align: 0x" + Long.toHexString(segmentAlignment)) +
                String.format("%-18s", "f_size: 0x" + Long.toHexString(segmentFileSize)) +
                String.format("%-18s", "m_size: 0x" + Long.toHexString(segmentMemorySize)) +
                String.format("%-18s", "flags: " +
                        ((flags & 0x04) != 0 ? "r" : "-") +
                        ((flags & 0x02) != 0 ? "w" : "-") +
                        ((flags & 0x01) != 0 ? "x" : "-"));
    }
}