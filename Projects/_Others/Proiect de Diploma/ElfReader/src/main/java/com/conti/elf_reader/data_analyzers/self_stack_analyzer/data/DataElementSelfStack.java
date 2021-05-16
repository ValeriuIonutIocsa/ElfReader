package com.conti.elf_reader.data_analyzers.self_stack_analyzer.data;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoSelfStack;
import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;

import javax.xml.stream.events.StartElement;

public class DataElementSelfStack extends DataElementTableViewRow {

    private final int rowIndex;

    private final String name;
    private final int selfStack;

    public DataElementSelfStack(String name, int selfStack) {

        rowIndex = -1;
        this.name = name;
        this.selfStack = selfStack;
    }

    public DataElementSelfStack(XmlReader xmlReader, StartElement selfStackElement, int rowIndex) {

        this.rowIndex = rowIndex;

        TableViewColumnInfo[] columnInfoArray = DataInfoSelfStack.getInstance().getColumnInfoArray();
        name = xmlReader.getAttribute(selfStackElement,
                columnInfoArray[1].getColumnTitleName());
        selfStack = DataTypes.tryParseInteger(xmlReader.getAttribute(selfStackElement,
                columnInfoArray[2].getColumnTitleName()));
    }

    @Override
    public void createDataElement(XmlWriter xmlWriter) {

        TableViewColumnInfo[] columnInfoArray = DataInfoSelfStack.getInstance().getColumnInfoArray();
        String dataElementTagName = DataInfoSelfStack.getInstance().getDataElementTagName();
        xmlWriter.writeStartElement(dataElementTagName);
        xmlWriter.writeAttribute(columnInfoArray[1].getColumnTitleName(),
                name);
        xmlWriter.writeAttribute(columnInfoArray[2].getColumnTitleName(),
                String.valueOf(selfStack));
        xmlWriter.writeEndElement(dataElementTagName);
    }

    public Object[] getRowData() {
        return new Object[]{
                rowIndex,
                name,
                selfStack >= 0 ? selfStack : null
        };
    }

    public String getName() {
        return name;
    }

    public int getSelfStack() {
        return selfStack;
    }
}
