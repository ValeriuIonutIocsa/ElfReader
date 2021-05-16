package com.conti.elf_reader.data_parsers.indirect_calls.data;

import com.conti.elf_reader.utils.data_types.DataTypes;

public class IndirectCallReplacement {

    public static IndirectCallReplacement parse(String line) {

        String[] lineSplit = line.split(",", 0);
        if (lineSplit.length != 3)
            return null;

        String pathInTree = lineSplit[0];
        int indexInCallsList = DataTypes.tryParseInteger(lineSplit[1]);
        if (indexInCallsList < 0)
            return null;

        String replacementValue = lineSplit[2];

        return new IndirectCallReplacement(pathInTree, indexInCallsList, replacementValue);
    }

    private final String pathInTree;
    private final int indexInCallsList;
    private final String replacementValue;

    public IndirectCallReplacement(String pathInTree, int indexInCallsList, String replacementValue) {

        this.pathInTree = pathInTree;
        this.indexInCallsList = indexInCallsList;
        this.replacementValue = replacementValue;
    }

    public String getPathInTree() {
        return pathInTree;
    }

    public int getIndexInCallsList() {
        return indexInCallsList;
    }

    public String getReplacementValue() {
        return replacementValue;
    }

    @Override
    public int hashCode() {
        return (pathInTree + indexInCallsList).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof IndirectCallReplacement))
            return false;

        IndirectCallReplacement other = (IndirectCallReplacement) obj;

        return pathInTree.equals(other.pathInTree) && indexInCallsList == other.indexInCallsList;
    }
}
