package com.conti.elf_reader.data_analyzers.size_analyzer.data;

import javax.xml.stream.events.StartElement;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoSize;
import com.conti.elf_reader.data_info.tables.TableViewColumnInfo;
import com.conti.elf_reader.data_parsers.core_architecture.ParserCoreArchitectureFile;
import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.elf.data.SectionHeaderEntry;
import com.conti.elf_reader.data_parsers.elf.data.SymbolTableEntryRow;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.conti.elf_reader.utils.data_types.HexString;
import com.conti.elf_reader.utils.data_types.SymbolInfo;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;

public class DataElementSize extends DataElementTableViewRow implements Comparable<DataElementSize> {

	private int rowIndex;

	private String sectionName;
	private HexString sectionAddress;
	private long sectionAlignment;
	private long sectionSize;

	private String symbolName;
	private HexString symbolAddress;
	private String symbolMemory;
	private String symbolFile;
	private long symbolSize;
	private SymbolInfo symbolInfo;

	public DataElementSize(final SymbolTableEntryRow symbolTableEntry, final DwarfSymbol dwSymbol) {

		if (dwSymbol != null) {
			symbolName = dwSymbol.getName();
			symbolAddress = new HexString(dwSymbol.getStartAddress());
			symbolMemory = ParserCoreArchitectureFile.computeMemoryName(symbolAddress);
			symbolFile = dwSymbol.getFile();
			symbolInfo = new SymbolInfo(100);
		}

		if (symbolTableEntry != null) {
			symbolName = symbolTableEntry.getName();
			symbolAddress = symbolTableEntry.getAddress();
			symbolMemory = ParserCoreArchitectureFile.computeMemoryName(symbolAddress);
			symbolSize = symbolTableEntry.getSize();
			symbolInfo = symbolTableEntry.getInfo();
		}
	}

	public DataElementSize(final SymbolTableEntryRow symbolTableEntry, final String objectFileName) {

		symbolName = symbolTableEntry.getName();
		symbolAddress = symbolTableEntry.getAddress();
		symbolMemory = ParserCoreArchitectureFile.computeMemoryName(symbolAddress);
		symbolSize = symbolTableEntry.getSize();
		symbolInfo = symbolTableEntry.getInfo();
		symbolFile = objectFileName;
	}

	public void setSectionProperties(final SectionHeaderEntry sectionHeaderEntry) {

		sectionName = sectionHeaderEntry.getName();
		sectionAddress = sectionHeaderEntry.getAddress();
		sectionAlignment = sectionHeaderEntry.getAlignment();
		sectionSize = sectionHeaderEntry.getSize();
		if (symbolInfo.getValue() == 100) {
			symbolSize = sectionSize;
		}
		if ("unknown".equals(sectionName) && symbolSize == 0) {
            symbolInfo = new SymbolInfo(16);
        }
	}

	public DataElementSize(final XmlReader xmlReader, final StartElement startElement, final int rowIndex) {

		this.rowIndex = rowIndex;

		final TableViewColumnInfo[] columnInfoArray = DataInfoSize.getInstance().getColumnInfoArray();
		sectionName = xmlReader.getAttribute(startElement,
				columnInfoArray[1].getColumnTitleName());
		sectionAddress = new HexString(xmlReader.getAttribute(startElement,
				columnInfoArray[2].getColumnTitleName()));
		sectionAlignment = DataTypes.tryParseLong(xmlReader.getAttribute(startElement,
				columnInfoArray[3].getColumnTitleName()));
		sectionSize = DataTypes.tryParseLong(xmlReader.getAttribute(startElement,
				columnInfoArray[4].getColumnTitleName()));

		symbolName = xmlReader.getAttribute(startElement,
				columnInfoArray[5].getColumnTitleName());
		symbolAddress = new HexString(xmlReader.getAttribute(startElement,
				columnInfoArray[6].getColumnTitleName()));
		symbolMemory = xmlReader.getAttribute(startElement,
				columnInfoArray[7].getColumnTitleName());
		if ("labels".equals(sectionName)) {
			symbolMemory = "N/A";
		}
		symbolFile = xmlReader.getAttribute(startElement,
				columnInfoArray[8].getColumnTitleName());
		symbolSize = DataTypes.tryParseLong(xmlReader.getAttribute(startElement,
				columnInfoArray[9].getColumnTitleName()));
		symbolInfo = new SymbolInfo(xmlReader.getAttribute(startElement,
				columnInfoArray[10].getColumnTitleName()));
	}

	@Override
	public void createDataElement(final XmlWriter xmlWriter) {

		final TableViewColumnInfo[] columnInfoArray = DataInfoSize.getInstance().getColumnInfoArray();
		final String dataElementTagName = DataInfoSize.getInstance().getDataElementTagName();
		xmlWriter.writeStartElement(dataElementTagName);
		xmlWriter.writeAttribute(columnInfoArray[1].getColumnTitleName(),
				sectionName != null ? sectionName : "");
		xmlWriter.writeAttribute(columnInfoArray[2].getColumnTitleName(),
				sectionAddress != null ? sectionAddress.toString() : "");
		xmlWriter.writeAttribute(columnInfoArray[3].getColumnTitleName(),
				sectionAlignment >= 0 ? String.valueOf(sectionAlignment) : "");
		xmlWriter.writeAttribute(columnInfoArray[4].getColumnTitleName(),
				sectionSize >= 0 ? String.valueOf(sectionSize) : "");
		xmlWriter.writeAttribute(columnInfoArray[5].getColumnTitleName(),
				symbolName);
		xmlWriter.writeAttribute(columnInfoArray[6].getColumnTitleName(),
				symbolAddress != null ? symbolAddress.toString() : "");
		xmlWriter.writeAttribute(columnInfoArray[7].getColumnTitleName(),
				symbolMemory != null ? symbolMemory : "");
		xmlWriter.writeAttribute(columnInfoArray[8].getColumnTitleName(),
				symbolFile != null ? symbolFile : "");
		xmlWriter.writeAttribute(columnInfoArray[9].getColumnTitleName(),
				String.valueOf(symbolSize));
		xmlWriter.writeAttribute(columnInfoArray[10].getColumnTitleName(),
				symbolInfo != null ? symbolInfo.toString() : "");
		xmlWriter.writeEndElement(dataElementTagName);
	}

	@Override
    public Object[] getRowData() {
		return new Object[] {
				rowIndex,
				sectionName,
				sectionAddress,
				sectionAlignment >= 0 ? sectionAlignment : null,
				sectionSize >= 0 ? sectionSize : null,
				symbolName,
				symbolAddress,
				symbolMemory,
				symbolFile,
				symbolSize >= 0 ? symbolSize : null,
				symbolInfo };
	}

	@Override
	public int compareTo(final DataElementSize sizeElement) {
		return Long.compare(symbolAddress.getValue(), sizeElement.symbolAddress.getValue());
	}

	public String getSymbolName() {
		return symbolName;
	}

	public HexString getSymbolAddress() {
		return symbolAddress;
	}

	public String getSymbolFile() {
		return symbolFile;
	}

	public String getSectionName() {
		return sectionName;
	}
}
