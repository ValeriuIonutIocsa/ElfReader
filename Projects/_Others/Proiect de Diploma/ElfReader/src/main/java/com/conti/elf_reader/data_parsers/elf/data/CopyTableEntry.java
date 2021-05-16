package com.conti.elf_reader.data_parsers.elf.data;

import com.conti.elf_reader.utils.data_types.DataTypes;
import com.utils.log.Logger;

import java.nio.ByteBuffer;

public class CopyTableEntry {

    public static CopyTableEntry parse(ByteBuffer byteBuffer) {

        try {
            long action = byteBuffer.getInt() & 0xFFFFFFFFL;
            long destinationAddress = byteBuffer.getInt() & 0xFFFFFFFFL;
            long cloningAddress = computeCloningAddress(destinationAddress);
            long sourceAddress = byteBuffer.getInt() & 0xFFFFFFFFL;
            long length = byteBuffer.getInt() & 0xFFFFFFFFL;
            return new CopyTableEntry(action, destinationAddress, cloningAddress, sourceAddress, length);

        } catch (Exception ignored) {
            Logger.printError("failed to parse a copy table entry!");
        }
        return null;
    }

    private static long computeCloningAddress(long address) {

        long[] clonedAddressPrefixes = {0x50100000, 0x60100000, 0x70100000};
        for (long clonedAddressPrefix : clonedAddressPrefixes) {

            if ((address & clonedAddressPrefix) == clonedAddressPrefix)
                return ((address & 0x000FFFFF) + 0xC0000000) & 0xFFFFFFFFL;
        }
        return -1;
    }

    private final long action;
    private final long destinationAddress;
    private final long cloningDestinationAddress;
    private final long sourceAddress;
    private final long length;

    private CopyTableEntry(
            long action, long destinationAddress, long cloningDestinationAddress, long sourceAddress, long length) {

        this.action = action;
        this.destinationAddress = destinationAddress;
        this.cloningDestinationAddress = cloningDestinationAddress;
        this.sourceAddress = sourceAddress;
        this.length = length;
    }

    @Override
    public String toString() {

        String actionString;
        if (action == 0) {
            actionString = "0 (END)";
        } else if (action == 1) {
            actionString = "1 (COPY)";
        } else if (action == 2) {
            actionString = "2 (FILL)";
        } else {
            actionString = "unknown";
        }

        return "copy_table_entry     "
                + "action: " + String.format("%-12s", actionString)
                + "dest: " + String.format("%-15s", DataTypes.hexString(destinationAddress))
                + "src: " + String.format("%-15s", DataTypes.hexString(sourceAddress))
                + "length: " + String.format("%-10s", String.valueOf(length))
                + (cloningDestinationAddress != -1 ? "cloning dest addr: " +
                String.format("%-15s", DataTypes.hexString(cloningDestinationAddress)) : "");
    }

    public long getAction() {
        return action;
    }

    public long getDestinationAddress() {
        return destinationAddress;
    }

    public long getCloningDestinationAddress() {
        return cloningDestinationAddress;
    }

    public long getSourceAddress() {
        return sourceAddress;
    }

    public long getLength() {
        return length;
    }
}
