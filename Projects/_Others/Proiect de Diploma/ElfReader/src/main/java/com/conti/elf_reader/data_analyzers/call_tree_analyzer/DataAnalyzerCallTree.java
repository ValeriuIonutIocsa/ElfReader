package com.conti.elf_reader.data_analyzers.call_tree_analyzer;

import java.util.*;

import com.conti.elf_reader.data_analyzers.DataAnalyzer;
import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info.CallTreeAdditionalInfo;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.powerpc_operations.OperationB;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.powerpc_operations.OperationBc;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.tricore_operations.*;
import com.conti.elf_reader.data_parsers.elf.ElfFile;
import com.conti.elf_reader.data_parsers.elf.code_maps.ElfMachineType;
import com.conti.elf_reader.data_parsers.elf.data.AssemblyCodeInstruction;
import com.conti.elf_reader.data_parsers.elf.data.AssemblyCodeSection;
import com.conti.elf_reader.settings.Settings;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.utils.log.Logger;

public abstract class DataAnalyzerCallTree extends DataAnalyzer {

	private static final StringBuilder stringBuilderDebug = new StringBuilder();

	public static StringBuilder getStringBuilderDebug() {
		return stringBuilderDebug;
	}

	public static String supportedOperationsToString(final ElfMachineType elfMachineType) {

		if (elfMachineType == null) {
			return "";
		}

		final List<OperationCallTree> supportedOperations = createSupportedOperations(elfMachineType);
		final StringBuilder supportedOperationsString = new StringBuilder(
				"Supported " + elfMachineType + " operations for call tree analysis:" + System.lineSeparator());
		for (final OperationCallTree operationCallTree : supportedOperations) {
			supportedOperationsString.append(operationCallTree.toString());
		}
		return supportedOperationsString.toString();
	}

	private static List<OperationCallTree> createSupportedOperations(final ElfMachineType elfMachineType) {

		final List<OperationCallTree> supportedOperations = new ArrayList<>();
		switch (elfMachineType) {

			case TriCore:
				supportedOperations.add(new OperationCall16());
				supportedOperations.add(new OperationJ16());
				supportedOperations.add(new OperationCall32());
				supportedOperations.add(new OperationCalla32());
				supportedOperations.add(new OperationFCall32());
				supportedOperations.add(new OperationFCalla32());
				supportedOperations.add(new OperationJ32());
				supportedOperations.add(new OperationJa32());
				supportedOperations.add(new OperationJl32());
				supportedOperations.add(new OperationJla32());
				supportedOperations.add(new OperationCalli());
				break;

			case PowerPC:
				supportedOperations.add(new OperationB());
				supportedOperations.add(new OperationBc());
				break;
		}
		return supportedOperations;
	}

	private final Set<DataElementCallTree> callTreeElements = new TreeSet<>();

	DataAnalyzerCallTree(final Settings settings, final ElfFile elfFile) {
		super(settings, elfFile);
	}

	@Override
	public void analyze(final boolean verbose) {

		try {
			Logger.printProgress("generating the call tree...");
			callTreeElements.clear();
			stringBuilderDebug.setLength(0);

			final List<AssemblyCodeSection> assemblyCodeSections = elfFile.getAssemblyCodeSections();
			final Map<Long, String> functionSymbolsAddressNameMap = elfFile.getFunctionSymbolsAddressNameMap();
			final Map<Long, List<Long>> copyTableSymbolAddressMap = elfFile.getCopyTableSymbolAddressMap();

			final Map<String, List<String>> callsMap = new LinkedHashMap<>();
			fillCallsMap(assemblyCodeSections, functionSymbolsAddressNameMap, copyTableSymbolAddressMap, callsMap);

			final Map<String, Set<String>> calledByMap = new HashMap<>();
			fillCalledByMap(callsMap, calledByMap);

			final Map<String, CallTreeAdditionalInfo> additionalInfoMap = new HashMap<>();
			fillAdditionalInfoMap(callsMap, calledByMap, additionalInfoMap);

			for (final String functionName : callsMap.keySet()) {

				final CallTreeAdditionalInfo callTreeAdditionalInfo = additionalInfoMap.getOrDefault(functionName,
						null);
				final List<String> calls = callsMap.get(functionName);
				final Set<String> calledBy = calledByMap.getOrDefault(functionName, null);
				callTreeElements.add(createDataElementCallTree(functionName, callTreeAdditionalInfo, calls, calledBy));
			}

			Logger.printStatus("Finished generating the call tree.");

		} catch (final Exception exc) {
			Logger.printError("failed to generate the call tree!");
			Logger.printException(exc);
		}
	}

	private void fillCallsMap(
			final List<AssemblyCodeSection> assemblyCodeSections, final Map<Long, String> functionSymbolsAddressNameMap,
			final Map<Long, List<Long>> copyTableSymbolAddressMap, final Map<String, List<String>> callsMap) {

		final List<OperationCallTree> supportedOperations = createSupportedOperations(
				elfFile.getElfHeader().getMachineType());
		for (final AssemblyCodeSection assemblyCodeSection : assemblyCodeSections) {

			stringBuilderDebug.append(System.lineSeparator()).append("----> Assembly code section: ")
					.append(assemblyCodeSection.getSectionName()).append(System.lineSeparator());
			List<String> calls = null;
			final Set<AssemblyCodeInstruction> assemblyCodeInstructions = assemblyCodeSection.getInstructions();
			for (final AssemblyCodeInstruction instruction : assemblyCodeInstructions) {

				final List<String> tempCalls = searchForFunction(callsMap, instruction);
				if (tempCalls != null) {
					calls = tempCalls;
				}
				searchForCalls(instruction,
						supportedOperations, functionSymbolsAddressNameMap, copyTableSymbolAddressMap, calls);
			}
		}

		final Set<String> callsWithoutLabels = new HashSet<>();
		for (final List<String> calls : callsMap.values()) {
			for (final String calledFunctionName : calls) {
				if (!callsMap.containsKey(calledFunctionName)) {
					callsWithoutLabels.add(calledFunctionName);
				}
			}
		}
		for (final String callsWithoutLabel : callsWithoutLabels) {
			callsMap.putIfAbsent(callsWithoutLabel, null);
		}
	}

	private List<String> searchForFunction(final Map<String, List<String>> callsMap,
			final AssemblyCodeInstruction instruction) {

		final String label = instruction.getLabel();
		final long address = instruction.getAddress();
		if (label == null || label.isEmpty()) {
			return null;
		}

		stringBuilderDebug.append(System.lineSeparator()).append("--> Found label: ").append(label)
				.append(" at address ").append(DataTypes.hexString(address)).append(System.lineSeparator());
		final List<String> calls = new ArrayList<>();
		callsMap.put(label, calls);
		return calls;
	}

	private void searchForCalls(
			final AssemblyCodeInstruction instruction, final List<OperationCallTree> supportedOperations,
			final Map<Long, String> functionSymbolsAddressNameMap,
			final Map<Long, List<Long>> copyTableSymbolAddressMap,
			final List<String> calls) {

		final long address = instruction.getAddress();
		final byte[] bytes = instruction.getBytes();

		for (final OperationCallTree operation : supportedOperations) {

			if (operation.doesNotMatch(bytes)) {
				continue;
			}

			final long calledFunctionAddress = operation.getCalledFunctionAddress(address, bytes);
			String calledFunctionName = findSymbolNameByAddress(calledFunctionAddress,
					functionSymbolsAddressNameMap, copyTableSymbolAddressMap);
			if (operation instanceof OperationCalli) {
				calledFunctionName = DataElementCallTree.indirectCallFunctionName;
			}
			stringBuilderDebug.append("search_for_calls")
					.append("    instr: ").append(instruction.printWithoutLabel())
					.append("    called_addr: ").append(
							String.format("%-10s", DataTypes.hexString(calledFunctionAddress)))
					.append("    called_name: ").append(calledFunctionName)
					.append("    op_type: ").append(operation.getName())
					.append(System.lineSeparator());
			if (calls != null && calledFunctionName != null && !calledFunctionName.isEmpty()) {
				calls.add(calledFunctionName);
			}
		}
	}

	public static String findSymbolNameByAddress(
			final long address, final Map<Long, String> functionSymbolsAddressNameMap,
			final Map<Long, List<Long>> copyTableSymbolAddressMap) {

		String functionSymbolName = functionSymbolsAddressNameMap.getOrDefault(address, "");

		if (!functionSymbolName.isEmpty()) {
			return functionSymbolName;
		}

		final List<Long> copiedSymbolAddressList = copyTableSymbolAddressMap.getOrDefault(address, null);
		if (copiedSymbolAddressList == null) {
			return "";
		}

		for (final Long copiedSymbolAddress : copiedSymbolAddressList) {
			functionSymbolName = functionSymbolsAddressNameMap.getOrDefault(copiedSymbolAddress, "");
			if (!functionSymbolName.isEmpty()) {
				return functionSymbolName;
			}
		}
		return "";
	}

	private void fillCalledByMap(final Map<String, List<String>> callsMap, final Map<String, Set<String>> calledByMap) {

		for (final String functionName : callsMap.keySet()) {

			final List<String> calls = callsMap.get(functionName);
			if (calls == null) {
				continue;
			}

			for (final String call : calls) {

				Set<String> calledBy = calledByMap.getOrDefault(call, null);
				if (calledBy == null) {
					calledBy = new TreeSet<>(String::compareToIgnoreCase);
					calledByMap.put(call, calledBy);
				}
				calledBy.add(functionName);
			}
		}
	}

	protected abstract void fillAdditionalInfoMap(
			Map<String, List<String>> callsMap, Map<String, Set<String>> calledByMap,
			Map<String, CallTreeAdditionalInfo> additionalInfoMap);

	protected abstract DataElementCallTree createDataElementCallTree(
			String functionName, CallTreeAdditionalInfo callTreeAdditionalInfo,
			List<String> calls, Set<String> calledBy);

	@Override
	public Collection<? extends DataElementTableViewRow> getDataElementTableViewRowList() {
		return callTreeElements;
	}
}
