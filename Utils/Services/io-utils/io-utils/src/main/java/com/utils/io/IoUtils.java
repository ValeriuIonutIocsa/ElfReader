package com.utils.io;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.utils.log.Logger;
import com.utils.string.StrUtils;

public class IoUtils {

	public static boolean fileExists(final Path path) {

		return path != null && !path.toString().isEmpty() && Files.exists(path);
	}

	public static boolean directoryExists(final Path path) {

		return path != null && !path.toString().isEmpty() && Files.isDirectory(path);
	}

	public static List<Path> listFiles(final Path dirPath) {
		return listFiles(dirPath, null);
	}

	public static List<Path> listFiles(final Path dirPath, final Predicate<Path> filterPredicate) {

		final ArrayList<Path> filePaths = new ArrayList<>();
		try (final Stream<Path> fileListStream = Files.list(dirPath)) {

			if (filterPredicate != null) {
				fileListStream.filter(filterPredicate).forEach(filePaths::add);
			} else {
				fileListStream.forEach(filePaths::add);
			}
			return filePaths;

		} catch (final Exception exc) {
			Logger.printError("failed to read the folder:" + System.lineSeparator() + dirPath);
			Logger.printException(exc);
			return filePaths;
		}
	}

	public static List<String> fileLinesMultipleEncodings(
			final Path path, final Charset[] charsets) {

		return fileLinesMultipleEncodings(path, charsets, null);
	}

	public static List<String> fileLinesMultipleEncodings(
			final Path path, final Charset[] charsets, final Predicate<String> filterPredicate) {

		for (final Charset charset : charsets) {

			final List<String> fileLines = fileLines(path, charset, filterPredicate, false);
			if (!fileLines.isEmpty()) {
				return fileLines;
			}
		}

		Logger.printWarning("failed to read any lines in any encoding");
		return new ArrayList<>();
	}

	public static List<String> fileLines(final Path path) {

		return fileLines(path, Charset.defaultCharset(), null);
	}

	public static List<String> fileLines(final Path path, final Charset charset) {

		return fileLines(path, charset, null);
	}

	public static List<String> fileLines(final Path path, final Predicate<String> filterPredicate) {

		return fileLines(path, Charset.defaultCharset(), filterPredicate);
	}

	public static List<String> fileLines(
			final Path path, final Charset charset, final Predicate<String> filterPredicate) {

		return fileLines(path, charset, filterPredicate, true);
	}

	public static List<String> fileLines(
			final Path path, final Charset charset, final Predicate<String> filterPredicate, final boolean verbose) {

		final ArrayList<String> lineList = new ArrayList<>();
		try (final Stream<String> fileLinesStream = Files.lines(path, charset)) {

			if (filterPredicate != null) {
				fileLinesStream.filter(filterPredicate).forEach(lineList::add);
			} else {
				fileLinesStream.forEach(lineList::add);
			}
			return lineList;

		} catch (final Exception exc) {
			if (verbose) {
				Logger.printException(exc);
			}
			return lineList;
		}
	}

	public static void createParentDirectories(final Path filePath) {

		if (filePath == null) {
			return;
		}

		final Path parentFile = filePath.getParent();
		createDirectories(parentFile);
	}

	public static void createDirectories(final Path directoryPath) {

		try {
			if (!fileExists(directoryPath)) {
				Files.createDirectories(directoryPath);
			}

		} catch (final Exception exc) {
			Logger.printError("failed to create directory:" + System.lineSeparator() + directoryPath);
			Logger.printException(exc);
		}
	}

	public static void deleteFile(final Path filePath) {

		try {
			if (fileExists(filePath)) {
				Files.delete(filePath);
			}

		} catch (final Exception exc) {
			Logger.printError("failed to delete file:" + System.lineSeparator() + filePath);
			Logger.printException(exc);
		}
	}

	public static void cleanDirectory(final Path directoryPath) {

		cleanDirectory(directoryPath.toFile());
	}

	public static void cleanDirectory(final File directory) {

		try {
			if (!directory.exists()) {
				return;
			}

			FileUtils.cleanDirectory(directory);

		} catch (final Exception exc) {
			if (directory.exists() && !directory.isDirectory()) {
				Logger.printError("file is not a directory:" +
						System.lineSeparator() + directory);
			} else {
				Logger.printError("failed to clean directory:" +
						System.lineSeparator() + directory);
			}
			Logger.printException(exc);
		}
	}

	public static void deleteDirectory(final Path directoryPath) {

		deleteDirectory(directoryPath.toFile());
	}

	public static void deleteDirectory(final File directory) {

		try {
			if (!directory.exists()) {
				return;
			}

			FileUtils.deleteDirectory(directory);

		} catch (final Exception exc) {
			if (directory.exists() && !directory.isDirectory()) {
				Logger.printError("file is not a directory:" +
						System.lineSeparator() + directory);
			} else {
				Logger.printError("failed to delete directory:" +
						System.lineSeparator() + directory);
			}
			Logger.printException(exc);
		}
	}

	public static void clearReadOnlyFlags(final Path path) {

		try {
			Logger.printNewLine();
			Logger.printProgress("clearing the readonly flags inside folder:" +
					System.lineSeparator() + path);

			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						final Path dir, final BasicFileAttributes attr) throws IOException {
					Files.setAttribute(dir, "dos:readonly", false);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						final Path file, final BasicFileAttributes attr) throws IOException {
					Files.setAttribute(file, "dos:readonly", false);
					return FileVisitResult.CONTINUE;
				}
			});

		} catch (final Exception exc) {
			Logger.printError("failed to clear the readonly flags!");
			Logger.printException(exc);
		}
	}

	public static String resourceFileToString(final String resourceFileRelativePathString) {

		try {
			final InputStream inputStreamMockResponse = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(resourceFileRelativePathString);
			return inputStreamToString(inputStreamMockResponse);

		} catch (final Exception exc) {
			Logger.printException(exc);
			return "";
		}
	}

	public static String fileToString(final Path filePath) throws Exception {

		return fileToString(filePath, StandardCharsets.UTF_8);
	}

	public static String fileToString(final Path filePath, final Charset charset) throws Exception {

		try (final InputStream inputStream = Files.newInputStream(filePath)) {
			return inputStreamToString(inputStream, charset.name());
		}
	}

	public static String inputStreamToString(final InputStream inputStream) throws Exception {

		return inputStreamToString(inputStream, StandardCharsets.UTF_8.name());
	}

	public static String inputStreamToString(final InputStream inputStream, final String encoding) throws Exception {

		return IOUtils.toString(inputStream, encoding);
	}

	public static File createTemporaryFile(final InputStream inputStream) throws IOException {

		final File tempFile = File.createTempFile(StrUtils.createDateTimeString(), ".tmp");
		tempFile.deleteOnExit();
		try (final OutputStream outputStream = new FileOutputStream(tempFile)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return tempFile;
	}

	public static void writeStringToFile(
			final Path path, final String string, final Charset charset) throws Exception {

		writeStringToFile(path.toFile(), string, charset);
	}

	public static void writeStringToFile(
			final File file, final String string, final Charset charset) throws Exception {

		try (final BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), charset))) {
			bufferedWriter.write(string);
		}
	}

	public static String getFileExtension(final Path path) {

		return getFileExtension(path.toString());
	}

	public static String getFileExtension(final String pathString) {

		return FilenameUtils.getExtension(pathString);
	}

	public static String getFileNameWithoutExtension(final Path path) {

		if (path == null) {
            return null;
        }

		return getFileNameWithoutExtension(path.toString());
	}

	public static String getFileNameWithoutExtension(final String pathString) {

		return FilenameUtils.removeExtension(pathString);
	}
}
