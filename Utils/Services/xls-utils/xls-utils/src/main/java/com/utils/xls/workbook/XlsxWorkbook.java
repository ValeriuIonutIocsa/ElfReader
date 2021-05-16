package com.utils.xls.workbook;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class XlsxWorkbook {

	private final Path path;

	protected XlsxWorkbook(final Path path) {

		this.path = path;
	}

	public void parse() {

		try (final InputStream inputStream = new BufferedInputStream(Files.newInputStream(this.path));
				final Workbook workbook = new XSSFWorkbook(inputStream)) {

			this.parseWorkbook(workbook);

		} catch (final Exception exc) {
			this.handleError(exc);
		}
	}

	protected abstract void parseWorkbook(Workbook workbook);

	protected void fillColumnTitleByIndexMap(final Row row, final Map<Integer, String> columnTitleByIndexMap) {

		final Iterator<Cell> cellIterator = row.cellIterator();
		int columnIndex = 0;
		while (cellIterator.hasNext()) {

			columnIndex++;
			final Cell cell = cellIterator.next();
			try {
				final String stringCellValue = cell.getStringCellValue().trim();
				columnTitleByIndexMap.put(columnIndex, stringCellValue);

			} catch (final Exception ignored) {
			}
		}
	}

	protected abstract void handleError(Exception exc);

	public static Workbook createNew() {

		return new SXSSFWorkbook();
	}
}
