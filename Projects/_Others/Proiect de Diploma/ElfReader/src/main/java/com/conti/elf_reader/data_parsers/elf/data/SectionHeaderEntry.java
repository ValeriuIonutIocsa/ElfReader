package com.conti.elf_reader.data_parsers.elf.data;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoSections;
import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.data_parsers.elf.code_maps.ElfShType;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.conti.elf_reader.utils.data_types.HexString;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;

import javax.xml.stream.events.StartElement;
import java.nio.ByteBuffer;

public class SectionHeaderEntry extends DataElementTableViewRow {

    public static SectionHeaderEntry parse(ByteBuffer byteBuffer, ByteBuffer nameTableByteBuffer) {

        long nameOffset = byteBuffer.getInt() & 0xFFFFFFFFL;
        String name = "";
        if (nameTableByteBuffer != null) {
            nameTableByteBuffer.position((int) nameOffset);
            name = DataTypes.parseNullTerminatedString(nameTableByteBuffer);
        }
        ElfShType type = ElfShType.byValue(byteBuffer.getInt());
        long flags = byteBuffer.getInt() & 0xFFFFFFFFL;
        long virtualAddress = byteBuffer.getInt() & 0xFFFFFFFFL;
        long fileOffset = byteBuffer.getInt() & 0xFFFFFFFFL;
        long size = byteBuffer.getInt() & 0xFFFFFFFFL;
        byteBuffer.getInt();
        byteBuffer.getInt();
        long alignment = byteBuffer.getInt() & 0xFFFFFFFFL;
        byteBuffer.getInt();

        return new SectionHeaderEntry(name, type, flags, virtualAddress, fileOffset, size, alignment);
    }

    private final int rowIndex;

    private final String name;
    private final ElfShType type;
    private final HexString flags;
    private final HexString address;
    private final HexString fileOffset;
    private final long size;
    private final long alignment;

    private SectionHeaderEntry(
            String name, ElfShType type, long flags, long address, long fileOffset, long size, long alignment) {

        rowIndex = -1;
        this.name = name;
        this.type = type;
        this.flags = new HexString(flags);
        this.address = new HexString(address);
        this.fileOffset = new HexString(fileOffset);
        this.size = size;
        this.alignment = alignment;
    }

    public SectionHeaderEntry(XmlReader xmlReader, StartElement sectionElement, int rowIndex) {

        this.rowIndex = rowIndex;

        TableViewColumnInfo[] columnInfoArray = DataInfoSections.getInstance().getColumnInfoArray();
        name = xmlReader.getAttribute(sectionElement,
                columnInfoArray[1].getColumnTitleName());
        type = ElfShType.byValue(DataTypes.tryParseInteger(
                xmlReader.getAttribute(sectionElement,
                        columnInfoArray[2].getColumnTitleName())));
        flags = new HexString(
                DataTypes.tryParseHexString(xmlReader.getAttribute(sectionElement,
                        columnInfoArray[3].getColumnTitleName())));
        address = new HexString(
                DataTypes.tryParseHexString(xmlReader.getAttribute(sectionElement,
                        columnInfoArray[4].getColumnTitleName())));
        fileOffset = new HexString(
                DataTypes.tryParseHexString(xmlReader.getAttribute(sectionElement,
                        columnInfoArray[5].getColumnTitleName())));
        size = DataTypes.tryParseHexString(xmlReader.getAttribute(sectionElement,
                columnInfoArray[6].getColumnTitleName()));
        alignment = DataTypes.tryParseHexString(xmlReader.getAttribute(sectionElement,
                columnInfoArray[7].getColumnTitleName()));
    }

    public SectionHeaderEntry(String name) {

        rowIndex = -1;
        this.name = name;
        this.type = null;
        this.flags = null;
        this.address = null;
        this.fileOffset = null;
        this.size = -1;
        this.alignment = -1;
    }

    @Override
    public void createDataElement(XmlWriter xmlWriter) {

        TableViewColumnInfo[] columnInfoArray = DataInfoSections.getInstance().getColumnInfoArray();
        String dataElementTagName = DataInfoSections.getInstance().getDataElementTagName();
        xmlWriter.writeStartElement(dataElementTagName);
        xmlWriter.writeAttribute(columnInfoArray[1].getColumnTitleName(),
                name);
        xmlWriter.writeAttribute(columnInfoArray[2].getColumnTitleName(),
                type != null ? String.valueOf(type.getValue()) : "");
        xmlWriter.writeAttribute(columnInfoArray[3].getColumnTitleName(),
                flags != null ? flags.toString() : "");
        xmlWriter.writeAttribute(columnInfoArray[4].getColumnTitleName(),
                address != null ? address.toString() : "");
        xmlWriter.writeAttribute(columnInfoArray[5].getColumnTitleName(),
                fileOffset != null ? fileOffset.toString() : "");
        xmlWriter.writeAttribute(columnInfoArray[6].getColumnTitleName(),
                DataTypes.hexString(size));
        xmlWriter.writeAttribute(columnInfoArray[7].getColumnTitleName(),
                DataTypes.hexString(alignment));
        xmlWriter.writeEndElement(dataElementTagName);
    }

    public boolean isAssemblyCodeSection() {
        return ElfShType.SHT_PROGBITS.equals(type) && (flags.getValue() & 0x4) != 0;
    }

    @Override
    public Object[] getRowData() {
        return new Object[]{
                rowIndex,
                name,
                size >= 0 ? size : null,
                flags,
                address,
                fileOffset,
                alignment >= 0 ? alignment : null,
                type.name()
        };
    }

    @Override
    public String toString() {

        return String.format("%-25s", "type: " + type) +
                String.format("%-18s", "size: " + size) +
                String.format("%-22s", "flags: " + flags) +
                String.format("%-22s", "v_addr: " + address) +
                String.format("%-22s", "f_offs: " + fileOffset) +
                String.format("%-15s", "align: " + alignment) +
                name;
    }

    public String getName() {
        return name;
    }

    public HexString getAddress() {
        return address;
    }

    public long getAlignment() {
        return alignment;
    }

    public long getSize() {
        return size;
    }

    public HexString getFileOffset() {
        return fileOffset;
    }
}
