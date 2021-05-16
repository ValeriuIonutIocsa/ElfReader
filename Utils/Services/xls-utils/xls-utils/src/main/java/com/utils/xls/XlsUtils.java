package com.utils.xls;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.ss.usermodel.Workbook;

public class XlsUtils {

	public static void saveWorkbook(final Workbook workbook, final Path path) throws Exception {

		try (final OutputStream outputStream =
				new BufferedOutputStream(Files.newOutputStream(path))) {

			workbook.write(outputStream);
		}
	}
}
