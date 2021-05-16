package com.conti.elf_reader.data_parsers.elf.data;

public class AssemblyCodeInstruction implements Comparable<AssemblyCodeInstruction> {

    private final long address;
    private final String label;
    private final byte[] bytes;

    AssemblyCodeInstruction(long address, String label, byte[] bytes) {

        this.address = address;
        this.label = label;
        this.bytes = bytes;
    }

    public String getLabel() {
        return label;
    }

    public long getAddress() {
        return address;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public int compareTo(AssemblyCodeInstruction other) {
        return Long.compare(address, other.address);
    }

    @Override
    public String toString() {

        String message = printWithoutLabel();

        if (label != null && !label.isEmpty())
            message += String.format("   label: <%s>", label);

        return message;
    }

    public String printWithoutLabel() {

        String message = String.format("0x%08x   ", address);

        StringBuilder bytesToString = new StringBuilder();
        for (byte b : bytes) {
            bytesToString.append(String.format("%02x ", b));
        }
        message += String.format("%-12s", bytesToString.toString());
        return message;
    }
}
