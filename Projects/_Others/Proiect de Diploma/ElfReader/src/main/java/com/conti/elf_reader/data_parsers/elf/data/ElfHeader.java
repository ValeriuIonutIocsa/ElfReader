package com.conti.elf_reader.data_parsers.elf.data;

import com.conti.elf_reader.data_parsers.elf.code_maps.ElfClass;
import com.conti.elf_reader.data_parsers.elf.code_maps.ElfMachineType;
import com.conti.elf_reader.data_parsers.elf.code_maps.ElfType;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.utils.log.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

public class ElfHeader {

    private int magicNumberOffset;

    private final ElfClass elfClass;
    private final ByteOrder elfByteOrder;

    private final ElfType elfType;
    private final ElfMachineType machineType;
    private final int elfVersion;
    private final long entryPoint;

    private final long programHeaderOffset;
    private final long sectionHeaderOffset;

    private int programHeaderEntrySize;
    private int programHeaderEntryCount;
    private int sectionHeaderEntrySize;
    private int sectionHeaderEntryCount;
    private int sectionNameTableSectionIndex;

    public ElfHeader(ReadableByteChannel channel) throws Exception {

        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.limit(1);

        LinkedList<Byte> magicNumberBytes = new LinkedList<>();
        magicNumberBytes.addLast((byte) 0);
        DataTypes.readFully(channel, byteBuffer);
        magicNumberBytes.addLast(byteBuffer.get());
        DataTypes.readFully(channel, byteBuffer);
        magicNumberBytes.addLast(byteBuffer.get());
        DataTypes.readFully(channel, byteBuffer);
        magicNumberBytes.addLast(byteBuffer.get());

        magicNumberOffset = -1;
        do {
            magicNumberOffset++;
            magicNumberBytes.removeFirst();
            DataTypes.readFully(channel, byteBuffer);
            magicNumberBytes.addLast(byteBuffer.get());
        }
        while (!isElfMagicNumber(magicNumberBytes));

        byteBuffer.limit(12);
        DataTypes.readFully(channel, byteBuffer);
        byte[] elfHeader = byteBuffer.array();
        elfClass = ElfClass.byValue(elfHeader[1]);
        if (elfClass == null || elfClass.equals(ElfClass.ELFCLASSNONE)) {
            Logger.printError("unknown elf class!");
            throw new Exception();
        }

        int byteOrder = elfHeader[2];
        elfByteOrder = byteOrder == 1 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        byteBuffer.order(elfByteOrder);

        byteBuffer.limit(8);
        DataTypes.readFully(channel, byteBuffer);

        elfType = ElfType.byValue(byteBuffer.getShort());
        machineType = ElfMachineType.byValue(byteBuffer.getShort());
        elfVersion = byteBuffer.getInt();

        switch (elfClass) {

            case ELFCLASS32:
                byteBuffer.limit(12);
                DataTypes.readFully(channel, byteBuffer);
                entryPoint = byteBuffer.getInt() & 0xFFFFFFFFL;
                programHeaderOffset = byteBuffer.getInt() & 0xFFFFFFFFL;
                sectionHeaderOffset = byteBuffer.getInt() & 0xFFFFFFFFL;
                break;

            case ELFCLASS64:
                byteBuffer.limit(24);
                DataTypes.readFully(channel, byteBuffer);
                entryPoint = byteBuffer.getLong();
                programHeaderOffset = byteBuffer.getLong();
                sectionHeaderOffset = byteBuffer.getLong();
                break;

            default:
                Logger.printError("unknown elf class!");
                throw new Exception();
        }

        byteBuffer.limit(6);
        DataTypes.readFully(channel, byteBuffer);

        byteBuffer.getInt();
        byteBuffer.getShort();

        byteBuffer.limit(10);
        DataTypes.readFully(channel, byteBuffer);

        programHeaderEntrySize = Short.toUnsignedInt(byteBuffer.getShort());
        programHeaderEntryCount = Short.toUnsignedInt(byteBuffer.getShort());
        sectionHeaderEntrySize = Short.toUnsignedInt(byteBuffer.getShort());
        sectionHeaderEntryCount = Short.toUnsignedInt(byteBuffer.getShort());
        sectionNameTableSectionIndex = Short.toUnsignedInt(byteBuffer.getShort());
    }

    private boolean isElfMagicNumber(List<Byte> byteArray) {

        for (int i = 0, byteArraySize = byteArray.size(); i < byteArraySize; i++) {

            byte b = byteArray.get(i);
            if (i == 0 && b != 0x7f)
                return false;
            else if (i == 1 && b != 'E')
                return false;
            else if (i == 2 && b != 'L')
                return false;
            else if (i == 3 && b != 'F')
                return false;
        }
        return true;
    }

    public int getMagicNumberOffset() {
        return magicNumberOffset;
    }

    public int getProgramHeaderEntrySize() {
        return programHeaderEntrySize;
    }

    public int getProgramHeaderEntryCount() {
        return programHeaderEntryCount;
    }

    public int getSectionHeaderEntrySize() {
        return sectionHeaderEntrySize;
    }

    public int getSectionHeaderEntryCount() {
        return sectionHeaderEntryCount;
    }

    public int getNameTableSectionIndex() {
        return sectionNameTableSectionIndex;
    }

    public ElfMachineType getMachineType() {
        return machineType;
    }

    public ByteOrder getElfByteOrder() {
        return elfByteOrder;
    }

    public long getProgramHeaderOffset() {
        return programHeaderOffset;
    }

    public long getSectionHeaderOffset() {
        return sectionHeaderOffset;
    }

    public ElfClass getElfClass() {
        return elfClass;
    }

    @Override
    public String toString() {
        return "ELF" +
                System.lineSeparator() + "class: " + elfClass +
                System.lineSeparator() + "encoding: " + elfByteOrder +
                System.lineSeparator() + "machine_type: " + machineType +
                System.lineSeparator() + "elf_type: " + elfType +
                System.lineSeparator() + "elf_version: " + elfVersion +
                System.lineSeparator() + "entry_point: " + DataTypes.hexString(entryPoint);
    }
}
