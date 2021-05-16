package com.conti.elf_reader.data_parsers.elf.data;

import java.nio.ByteBuffer;

import javax.xml.stream.events.StartElement;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoSymbols;
import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.conti.elf_reader.utils.data_types.HexString;
import com.conti.elf_reader.utils.data_types.SymbolInfo;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;

public class SymbolTableEntryRow extends DataElementTableViewRow {

	public static SymbolTableEntryRow parse(final ByteBuffer byteBuffer, final ByteBuffer strTabByteBuffer) {

		final long nameOffset = byteBuffer.getInt() & 0xFFFFFFFFL;
		strTabByteBuffer.position((int) nameOffset);
		final String name = DataTypes.parseNullTerminatedString(strTabByteBuffer);

		final long address = byteBuffer.getInt() & 0xFFFFFFFFL;
		final long size = byteBuffer.getInt() & 0xFFFFFFFFL;

		final byte info = byteBuffer.get();
		final byte other = byteBuffer.get();

		final long sectionIndex = byteBuffer.getShort() & 0xFFFFL;

		return new SymbolTableEntryRow(name, address, size, info, other, sectionIndex);
	}

	private final int rowIndex;

	private final String name;
	private final HexString address;
	private final long size;
	private final SymbolInfo info;
	private final HexString other;
	private final long sectionIndex;

	private SymbolTableEntryRow(final String name, final long address, final long size, final byte info,
			final byte other, final long sectionIndex) {

		rowIndex = -1;
		this.name = name;
		this.address = new HexString(address);
		this.size = size;
		this.info = new SymbolInfo(info);
		this.other = new HexString(other);
		this.sectionIndex = sectionIndex;
	}

	public SymbolTableEntryRow(final XmlReader xmlReader, final StartElement symbolElement, final int rowIndex) {

		this.rowIndex = rowIndex;

		final TableViewColumnInfo[] columnInfoArray = DataInfoSymbols.getInstance().getColumnInfoArray();
		name = xmlReader.getAttribute(symbolElement,
				columnInfoArray[1].getColumnTitleName());
		address = new HexString(
				DataTypes.tryParseLong(xmlReader.getAttribute(symbolElement,
						columnInfoArray[2].getColumnTitleName())));
		size = DataTypes.tryParseLong(xmlReader.getAttribute(symbolElement,
				columnInfoArray[3].getColumnTitleName()));
		info = new SymbolInfo(
				DataTypes.tryParseByte(xmlReader.getAttribute(symbolElement,
						columnInfoArray[4].getColumnTitleName())));
		other = new HexString(
				DataTypes.tryParseByte(xmlReader.getAttribute(symbolElement,
						columnInfoArray[5].getColumnTitleName())));
		sectionIndex = DataTypes.tryParseLong(xmlReader.getAttribute(symbolElement,
				columnInfoArray[6].getColumnTitleName()));
	}

	@Override
	public void createDataElement(final XmlWriter xmlWriter) {

		final TableViewColumnInfo[] columnInfoArray = DataInfoSymbols.getInstance().getColumnInfoArray();
		final String dataElementTagName = DataInfoSymbols.getInstance().getDataElementTagName();
		xmlWriter.writeStartElement(dataElementTagName);
		xmlWriter.writeAttribute(columnInfoArray[1].getColumnTitleName(),
				name);
		xmlWriter.writeAttribute(columnInfoArray[2].getColumnTitleName(),
				String.valueOf(address.getValue()));
		xmlWriter.writeAttribute(columnInfoArray[3].getColumnTitleName(),
				String.valueOf(size));
		xmlWriter.writeAttribute(columnInfoArray[4].getColumnTitleName(),
				String.valueOf(info.getValue()));
		xmlWriter.writeAttribute(columnInfoArray[5].getColumnTitleName(),
				String.valueOf(other.getValue()));
		xmlWriter.writeAttribute(columnInfoArray[6].getColumnTitleName(),
				String.valueOf(sectionIndex));
		xmlWriter.writeEndElement(dataElementTagName);
	}

	@Override
	public Object[] getRowData() {
		return new Object[] {
				rowIndex,
				name,
				address,
				size >= 0 ? size : null,
				info,
				other,
				sectionIndex >= 0 ? sectionIndex : null
		};
	}

	@Override
	public String toString() {

		return String.format("%-22s", "address: " + address) +
				String.format("%-20s", "size: " + size) +
				String.format("%-20s", "info: " + info.getValue()) +
				String.format("%-20s", "other: " + other.getValue()) +
				String.format("%-25s", "sectionIndex: " + sectionIndex) +
				name;
	}

	public boolean isCompilerLabel() {

		return name.startsWith("L_") || name.startsWith("F") ||
				name.matches("\\.L[0-9].*") || name.matches("_[0-9].*");
	}

	public String getName() {
		return name;
	}

	public HexString getAddress() {
		return address;
	}

	public long getSize() {
		return size;
	}

	public SymbolInfo getInfo() {
		return info;
	}

	public long getSectionIndex() {
		return sectionIndex;
	}
}
