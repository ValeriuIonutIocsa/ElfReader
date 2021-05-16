package com.conti.elf_reader.data_writers.writers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_info.tables.DataInfoTable;

public class DataFileWriterCsv extends DataFileWriter {

	private final Collection<? extends DataElementTableViewRow> tableViewItems;

	public DataFileWriterCsv(
			final Path outputPath, final DataInfoTable dataInfoTable,
			final Collection<? extends DataElementTableViewRow> tableViewItems) {

		super(outputPath, dataInfoTable);
		this.tableViewItems = tableViewItems;
	}

	@Override
	void writeData() throws Exception {

		final StringBuilder stringBuilder = new StringBuilder();

		final String headerLine = Arrays.stream(dataInfoTable.getColumnInfoArray())
				.map(String::valueOf)
				.collect(Collectors.joining(","));
		stringBuilder.append(headerLine).append(System.lineSeparator());

		for (final DataElementTableViewRow tableViewItem : tableViewItems) {

			final String csvRow = Arrays.stream(tableViewItem.getRowData())
					.map(String::valueOf).collect(Collectors.joining(","));
			stringBuilder.append(csvRow).append(System.lineSeparator());
		}

		Files.write(outputPath, stringBuilder.toString().getBytes());
	}
}
