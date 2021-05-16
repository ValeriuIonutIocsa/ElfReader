package com.conti.elf_reader.data_analyzers.size_analyzer;

import com.conti.elf_reader.data_analyzers.DataAnalyzer;
import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.size_analyzer.data.DataElementSize;
import com.conti.elf_reader.data_info.tables.DataInfoSize;
import com.conti.elf_reader.data_parsers.core_architecture.ParserCoreArchitectureFile;
import com.conti.elf_reader.data_parsers.dwarf.data.DwarfSymbol;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_parsers.elf.data.SectionHeaderEntry;
import com.conti.elf_reader.data_parsers.elf.data.SymbolTableEntryRow;
import com.conti.elf_reader.gui.WindowMain;
import com.conti.elf_reader.gui.tables.PaneTabTableSize;
import com.conti.elf_reader.settings.SettingNames;
import com.conti.elf_reader.settings.Settings;
import com.utils.log.Logger;
import com.utils.io.IoUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataAnalyzerSize extends DataAnalyzer {

    private static final SectionHeaderEntry sectionLabels = new SectionHeaderEntry("labels");
    private static final SectionHeaderEntry sectionUnknown = new SectionHeaderEntry("unknown");

    private Set<DataElementSize> sizeElements;

    public DataAnalyzerSize(Settings settings, ElfFile elfFile) {
        super(settings, elfFile);
    }

    public void analyze(boolean verbose) {

        if (verbose) {
            Logger.printProgress("analyzing size");
        }
        sizeElements = new LinkedHashSet<>();

        String elfFileName = elfFile.getElfFilePath().getFileName().toString();
        List<SymbolTableEntryRow> symbolTableEntries = elfFile.getSymbolTableEntries();
        List<DwarfSymbol> dwSymbols = elfFileName.endsWith(".elf") ? elfFile.getDwarfData().getDwarfSymbols() : null;
        String objectFileName = elfFileName.endsWith(".elf") ? "" : elfFileName;

        Map<Long, SectionHeaderEntry> sectionsByIndexMap = new TreeMap<>();
        fillSectionsByIndexMap(sectionsByIndexMap);

        parseCoreArchitectureFile();

        if (dwSymbols != null) {
            List<DataElementSize> sizeElementsWithoutSection = new ArrayList<>();
            fillSizeElementsWithoutSectionList(
                    symbolTableEntries, sectionsByIndexMap, dwSymbols, sizeElementsWithoutSection);

            Map<Long, SectionHeaderEntry> sectionsByStartingAddressMap = new TreeMap<>();
            for (SectionHeaderEntry sectionHeaderEntry : elfFile.getSectionHeaderEntries()) {

                long sectionStartAddress = sectionHeaderEntry.getAddress().getValue();
                sectionsByStartingAddressMap.put(sectionStartAddress, sectionHeaderEntry);
            }

            int sizeElementsWithoutSectionCount = sizeElementsWithoutSection.size();
            int sizeElementsWithoutSectionIndex = 0;
            for (long sectionStartAddress : sectionsByStartingAddressMap.keySet()) {

                SectionHeaderEntry sectionHeaderEntry = sectionsByStartingAddressMap.get(sectionStartAddress);
                long sectionSize = sectionsByStartingAddressMap.get(sectionStartAddress).getSize();

                List<DataElementSize> symbolElements = new ArrayList<>();
                while (sizeElementsWithoutSectionIndex < sizeElementsWithoutSectionCount) {

                    DataElementSize sizeElementWithoutSection =
                            sizeElementsWithoutSection.get(sizeElementsWithoutSectionIndex);

                    long symbolAddress = sizeElementWithoutSection.getSymbolAddress().getValue();
                    if (symbolAddress < sectionStartAddress) {
                        sizeElementsWithoutSectionIndex++;
                        continue;
                    }

                    if (symbolAddress >= sectionStartAddress + sectionSize)
                        break;

                    symbolElements.add(sizeElementWithoutSection);
                    sizeElementsWithoutSectionIndex++;
                }

                for (DataElementSize symbolElement : symbolElements) {
                    addSizeElement(symbolElement, sectionHeaderEntry);
                }
            }

        } else {

            for (SymbolTableEntryRow symbolTableEntry : symbolTableEntries) {

                DataElementSize sizeElement = new DataElementSize(symbolTableEntry, objectFileName);
                addSizeElementWithoutSection(
                        sizeElement, sectionsByIndexMap, symbolTableEntry, null);
            }
        }
        if (verbose) {
            Logger.printStatus("Finished analyzing size.");
        }
    }

    private void fillSectionsByIndexMap(Map<Long, SectionHeaderEntry> sectionsByIndexMap) {

        int sectionHeaderEntriesSize = elfFile.getSectionHeaderEntries().size();
        for (int index = 0; index < sectionHeaderEntriesSize; index++) {

            SectionHeaderEntry sectionHeaderEntry = elfFile.getSectionHeaderEntries().get(index);
            sectionsByIndexMap.put((long) index, sectionHeaderEntry);
        }
    }

    private void parseCoreArchitectureFile() {

        Path coreArchitectureFilePath = null;
        if (settings.isGui()) {
            PaneTabTableSize paneTabTableSize = (PaneTabTableSize)
                    WindowMain.getPaneTabTableByName(DataInfoSize.getInstance().getTabName());
            coreArchitectureFilePath = paneTabTableSize.getCoreArchitectureFilePath();
        }
        if (!IoUtils.fileExists(coreArchitectureFilePath)) {
            coreArchitectureFilePath = Paths.get(settings.get(SettingNames.core_architecture_file_path));
        }
        if (!IoUtils.fileExists(coreArchitectureFilePath)) {
            ParserCoreArchitectureFile.getMemoriesByNameMap().clear();
            return;
        }

        ParserCoreArchitectureFile.parse(coreArchitectureFilePath);
    }

    private void fillSizeElementsWithoutSectionList(
            List<SymbolTableEntryRow> symbolTableEntries, Map<Long, SectionHeaderEntry> sectionsByIndexMap,
            List<DwarfSymbol> dwSymbols, List<DataElementSize> sizeElementsWithoutSection) {

        Map<String, List<SymbolTableEntryRow>> symbolTableEntriesByName = new HashMap<>();

        for (SymbolTableEntryRow symbolTableEntry : symbolTableEntries) {

            String symbolTableEntryName = symbolTableEntry.getName();

            if (symbolTableEntriesByName.containsKey(symbolTableEntryName)) {
                symbolTableEntriesByName.get(symbolTableEntryName).add(symbolTableEntry);
            } else {
                List<SymbolTableEntryRow> symbolTableEntryList = new ArrayList<>();
                symbolTableEntryList.add(symbolTableEntry);
                symbolTableEntriesByName.put(symbolTableEntryName, symbolTableEntryList);
            }
        }

        Map<String, List<DwarfSymbol>> dwSymbolsByName = new HashMap<>();
        for (DwarfSymbol dwSymbol : dwSymbols) {

            String dwSymbolName = dwSymbol.getName();

            if (dwSymbolsByName.containsKey(dwSymbolName)) {
                dwSymbolsByName.get(dwSymbolName).add(dwSymbol);
            } else {
                List<DwarfSymbol> dwSymbolsList = new ArrayList<>();
                dwSymbolsList.add(dwSymbol);
                dwSymbolsByName.put(dwSymbolName, dwSymbolsList);
            }
        }

        for (String symbolName : symbolTableEntriesByName.keySet()) {

            List<SymbolTableEntryRow> symbolTableEntryList = symbolTableEntriesByName.get(symbolName);
            List<DwarfSymbol> dwSymbolList = dwSymbolsByName.getOrDefault(symbolName, null);

            for (SymbolTableEntryRow symbolTableEntry : symbolTableEntryList) {

                if (dwSymbolList == null) {
                    DataElementSize sizeElement = new DataElementSize(symbolTableEntry, "");
                    addSizeElementWithoutSection(
                            sizeElement, sectionsByIndexMap, symbolTableEntry, sizeElementsWithoutSection);
                    continue;
                }

                int count = 0;
                for (DwarfSymbol dwSymbol : dwSymbolList) {

                    long symbolTableEntryAddress = symbolTableEntry.getAddress().getValue();
                    long dwSymbolAddress = dwSymbol.getStartAddress();
                    if (symbolTableEntryAddress == dwSymbolAddress) {
                        DataElementSize sizeElement = new DataElementSize(symbolTableEntry, dwSymbol);
                        addSizeElementWithoutSection(
                                sizeElement, sectionsByIndexMap, symbolTableEntry, sizeElementsWithoutSection);
                        count++;
                    }
                }
                if (count == 0) {
                    DataElementSize sizeElement = new DataElementSize(symbolTableEntry, "");
                    addSizeElementWithoutSection(
                            sizeElement, sectionsByIndexMap, symbolTableEntry, sizeElementsWithoutSection);
                }
            }
        }

        for (String symbolName : dwSymbolsByName.keySet()) {

            if (!symbolTableEntriesByName.containsKey(symbolName)) {
                List<DwarfSymbol> dwSymbolList = dwSymbolsByName.get(symbolName);
                for (DwarfSymbol dwSymbol : dwSymbolList) {
                    sizeElementsWithoutSection.add(new DataElementSize(null, dwSymbol));
                }
            }
        }

        Collections.sort(sizeElementsWithoutSection);
    }

    private void addSizeElementWithoutSection(
            DataElementSize sizeElement, Map<Long, SectionHeaderEntry> sectionsByIndexMap,
            SymbolTableEntryRow symbolTableEntry, List<DataElementSize> sizeElementsWithoutSection) {

        int infoValue = symbolTableEntry.getInfo().getValue();
        if (!(infoValue == 1 || infoValue == 2 || infoValue == 17 || infoValue == 18)) {

            if (infoValue == 16) {
                addSizeElement(sizeElement, sectionLabels);
            }
            return;
        }

        long sectionIndex = symbolTableEntry.getSectionIndex();

        if (sizeElementsWithoutSection != null && sectionIndex >= 0xffff) {
            sizeElementsWithoutSection.add(sizeElement);
            return;
        }

        SectionHeaderEntry sectionHeaderEntry = sectionsByIndexMap.getOrDefault(sectionIndex, sectionUnknown);
        addSizeElement(sizeElement, sectionHeaderEntry);
    }

    private void addSizeElement(DataElementSize sizeElement, SectionHeaderEntry sectionHeaderEntry) {

        sizeElement.setSectionProperties(sectionHeaderEntry);
        sizeElements.add(sizeElement);
    }

    @Override
    public Collection<? extends DataElementTableViewRow> getDataElementTableViewRowList() {
        return sizeElements;
    }
}
