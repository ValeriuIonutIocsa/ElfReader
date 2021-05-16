package com.conti.elf_reader.utils.data_types;

public class HexString implements Comparable<HexString> {

    private long value = -1;

    public HexString(long value) {
        this.value = value;
    }

    public HexString(String addr) {

        try {
            addr = addr.substring(2);
            value = Long.parseLong(addr, 16);
        } catch (Exception ignored) {
        }
    }

    public long getValue() {
        return value;
    }

    @Override
    public int compareTo(HexString other) {

        if (other == null)
            return 1;
        return Long.compare(value, other.value);
    }

    @Override
    public String toString() {
        return value > 0 ? "0x" + Long.toHexString(value) : "";
    }
}
