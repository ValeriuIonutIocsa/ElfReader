package com.conti.elf_reader.utils.data_types;

public class SymbolInfo implements Comparable<SymbolInfo> {

    private int value = -1;

    public SymbolInfo(int value) {
        this.value = value;
    }

    public SymbolInfo(String info) {

        if (info == null)
            return;

        if (info.contains("DWARF")) {
            value = 100;

        } else {
            String infoString = info.split("-", 0)[0];
            value = DataTypes.tryParseInteger(infoString);
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(SymbolInfo other) {

        if (other == null)
            return 1;
        return Integer.compare(value, other.value);
    }

    @Override
    public String toString() {

        if (value == 100)
            return "DWARF";

        String text = "unknown";
        switch (value) {
            case 0:
                text = "local_no_type";
                break;
            case 1:
                text = "local_data";
                break;
            case 2:
                text = "local_function";
                break;
            case 3:
                text = "local_section";
                break;
            case 4:
                text = "local_file";
                break;
            case 16:
                text = "global_no_type";
                break;
            case 17:
                text = "global_data";
                break;
            case 18:
                text = "global_function";
                break;
        }
        return value != -1 ? value + "-" + text : "";
    }
}
