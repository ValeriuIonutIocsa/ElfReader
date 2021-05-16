package com.conti.elf_reader.data_analyzers.self_stack_analyzer;

import com.conti.elf_reader.data_analyzers.DataAnalyzer;
import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.data.DataElementSelfStack;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.tricore_operations.OperationLeaLongOffset;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.tricore_operations.OperationLeaShortOffset;
import com.conti.elf_reader.data_analyzers.self_stack_analyzer.tricore_operations.OperationSuba;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_parsers.elf.code_maps.ElfMachineType;
import com.conti.elf_reader.data_parsers.elf.data.AssemblyCodeInstruction;
import com.conti.elf_reader.data_parsers.elf.data.AssemblyCodeSection;
import com.conti.elf_reader.settings.Settings;
import com.utils.log.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataAnalyzerSelfStack extends DataAnalyzer {

    private final List<DataElementSelfStack> selfStackElements = new ArrayList<>();

    public DataAnalyzerSelfStack(Settings settings,ElfFile elfFile) {
        super(settings, elfFile);
    }

    public static String supportedOperationsToString(ElfMachineType elfMachineType) {

        List<OperationSelfStack> supportedOperations = getSupportedOperations(elfMachineType);
        StringBuilder supportedOperationsString = new StringBuilder(
                "Supported " + elfMachineType + " operations for self stack analysis:\n");
        for (OperationSelfStack operation : supportedOperations) {
            supportedOperationsString.append(operation.toString());
        }
        return supportedOperationsString.toString();
    }

    private static List<OperationSelfStack> getSupportedOperations(ElfMachineType elfMachineType) {

        List<OperationSelfStack> supportedOperations = new ArrayList<>();
        switch (elfMachineType) {

            case TriCore:
                supportedOperations.add(new OperationSuba());
                supportedOperations.add(new OperationLeaShortOffset());
                supportedOperations.add(new OperationLeaLongOffset());
                break;

            case PowerPC:
                break;
        }
        return supportedOperations;
    }

    public void analyze(boolean verbose) {

        Logger.printProgress("analyzing the self stack...");

        List<AssemblyCodeSection> assemblyCodeSections = elfFile.getAssemblyCodeSections();
        for (AssemblyCodeSection assemblyCodeSection : assemblyCodeSections) {

            String label = "";
            int selfStack = 0;

            for (AssemblyCodeInstruction instruction : assemblyCodeSection.getInstructions()) {

                String newLabel = instruction.getLabel();

                if (newLabel != null && !newLabel.isEmpty()) {

                    if (!label.isEmpty()) {
                        selfStackElements.add(new DataElementSelfStack(label, selfStack));
                    }
                    selfStack = 0;
                    label = newLabel;
                }

                byte[] bytes = instruction.getBytes();
                selfStack += searchForSelfStack(bytes);
            }

            if (!label.isEmpty()) {
                selfStackElements.add(new DataElementSelfStack(label, selfStack));
            }
        }
        Logger.printStatus("Finished analyzing the self stack.");
    }

    private int searchForSelfStack(byte[] bytes) {

        for (OperationSelfStack operation : getSupportedOperations(elfFile.getElfHeader().getMachineType())) {

            if (operation.doesNotMatch(bytes))
                continue;

            int selfStack = operation.getSelfStack(bytes);
            if (selfStack > 0)
                return selfStack;
        }
        return 0;
    }

    @Override
    public Collection<? extends DataElementTableViewRow> getDataElementTableViewRowList() {
        return selfStackElements;
    }
}
