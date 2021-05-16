package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfo;
import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTree;
import com.utils.log.Logger;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;

import javax.xml.stream.events.StartElement;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class DataElementCallTree extends DataElementTableViewRow implements Comparable<DataElementCallTree> {

    public final static String indirectCallFunctionName = "indirect_call";

    private final int rowIndex;
    private final String functionName;
    private final DataInfoCallTree dataInfoCallTree;
    final CallTreeAdditionalInfo callTreeAdditionalInfo;
    private final List<String> calls;
    private final Set<String> calledBy;

    DataElementCallTree(
            String functionName, DataInfoCallTree dataInfoCallTree, CallTreeAdditionalInfo callTreeAdditionalInfo,
            List<String> calls, Set<String> calledBy) {

        rowIndex = -1;
        this.functionName = functionName;
        this.dataInfoCallTree = dataInfoCallTree;
        this.callTreeAdditionalInfo = callTreeAdditionalInfo;
        this.calls = calls;
        this.calledBy = calledBy;
    }

    DataElementCallTree(
            XmlReader xmlReader, StartElement startElement, int rowIndex,
            DataInfoCallTree dataInfoCallTree, CallTreeAdditionalInfo callTreeAdditionalInfo) {

        this.rowIndex = rowIndex;

        this.dataInfoCallTree = dataInfoCallTree;
        TableViewColumnInfo[] columnInfoArray = dataInfoCallTree.getColumnInfoArray();
        functionName = xmlReader.getAttribute(startElement, columnInfoArray[1].getColumnTitleName());

        this.callTreeAdditionalInfo = callTreeAdditionalInfo;
        int additionalTableRowDataLength = callTreeAdditionalInfo.getAdditionalTableRowData().length;

        String callsString = xmlReader.getAttribute(startElement,
                columnInfoArray[2 + additionalTableRowDataLength].getColumnTitleName());
        if (callsString != null) {
            calls = Arrays.asList(callsString.split(";", 0));
        } else {
            calls = null;
        }

        String calledByString = xmlReader.getAttribute(startElement,
                columnInfoArray[3 + additionalTableRowDataLength].getColumnTitleName());
        if (calledByString != null) {
            calledBy = new TreeSet<>(String::compareToIgnoreCase);
            calledBy.addAll(Arrays.asList(calledByString.split(";", 0)));
        } else {
            calledBy = null;
        }
    }

    @Override
    public void createDataElement(XmlWriter xmlWriter) {

        String dataElementTagName = dataInfoCallTree.getDataElementTagName();
        xmlWriter.writeStartElement(dataElementTagName);

        try {
            TableViewColumnInfo[] columnInfoArray = dataInfoCallTree.getColumnInfoArray();
            xmlWriter.writeAttribute(columnInfoArray[1].getColumnTitleName(), functionName);
            callTreeAdditionalInfo.writeDataElement(xmlWriter);
            int additionalTableRowDataLength = callTreeAdditionalInfo.getAdditionalTableRowData().length;
            xmlWriter.writeAttribute(columnInfoArray[2 + additionalTableRowDataLength].getColumnTitleName(),
                    calls != null ? String.join(";", calls) : "");
            xmlWriter.writeAttribute(columnInfoArray[3 + additionalTableRowDataLength].getColumnTitleName(),
                    calledBy != null ? String.join(";", calledBy) : "");

        } catch (Exception exc) {
            Logger.printException(exc);

        } finally {
            xmlWriter.writeEndElement(dataElementTagName);
        }
    }

    @Override
    public int compareTo(DataElementCallTree callTreeElement) {
        return functionName.compareToIgnoreCase(callTreeElement.functionName);
    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof DataElementCallTree))
            return false;

        String otherName = ((DataElementCallTree) other).functionName;
        return (functionName == null && otherName == null) || (functionName != null && functionName.equals(otherName));
    }

    @Override
    public String toString() {
        return functionName;
    }

    @Override
    public Object[] getRowData() {

        Object[] additionalTableRowData = callTreeAdditionalInfo.getAdditionalTableRowData();
        Object[] rowData = new Object[5 + additionalTableRowData.length];
        rowData[0] = rowIndex;
        rowData[1] = functionName;
        System.arraycopy(additionalTableRowData, 0, rowData, 2, additionalTableRowData.length);
        rowData[2 + additionalTableRowData.length] = calls != null ? calls.size() : 0;
        rowData[3 + additionalTableRowData.length] = calledBy != null ? calledBy.size() : 0;
        return rowData;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<String> getCalls() {
        return calls;
    }

    public Set<String> getCalledBy() {
        return calledBy;
    }
}
