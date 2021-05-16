package com.utils.ar;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.utils.ar.entry.AArchiveEntry;
import com.utils.ar.entry.FactoryAArchiveEntry;
import com.utils.ar.stream.CustomRandomAccessFile;
import com.utils.log.Logger;

public class FactoryAArchive {

	private static final byte[] A_ARCHIVE_HEADER = new byte[] { '!', '<', 'a', 'r', 'c', 'h', '>' };

	public static final FactoryAArchive INSTANCE = new FactoryAArchive();

	private FactoryAArchive() {
	}

	public AArchive parse(final Path aFilePath) {

		return parse(aFilePath, null, null);
	}

	public AArchive parse(final Path aFilePath, final Long fileOffsetParam, final Long fileLengthParam) {

		if (aFilePath == null) {
			return parse((File) null);
		}

		return parse(aFilePath.toFile(), fileOffsetParam, fileLengthParam);
	}

	public AArchive parse(final File aFile) {

		return parse(aFile, null, null);
	}

	public AArchive parse(final File aFile, final Long fileOffsetParam, final Long fileLengthParam) {

		try {
			if (aFile == null) {
				return null;
			}

			try (final RandomAccessFile randomAccessFile =
                         new CustomRandomAccessFile(aFile, "r", fileOffsetParam, fileLengthParam)) {

                randomAccessFile.seek(0);

				final byte[] headerBytes = new byte[7];
				randomAccessFile.readFully(headerBytes);

				final boolean aArchiveHeader = isAArchiveHeader(headerBytes);
				if (!aArchiveHeader) {
					Logger.printError("Invalid AR archive! No header found.");
					return null;
				}

				randomAccessFile.readLine();

				final List<AArchiveEntry> aArchiveEntries = new ArrayList<>();
				final long fileLength = randomAccessFile.length();
				long entryOffset = randomAccessFile.getFilePointer();
				Long stringTableOffset = null;
				while (entryOffset < fileLength) {

					final AArchiveEntry aArchiveEntry = FactoryAArchiveEntry.INSTANCE
							.parse(randomAccessFile, entryOffset, stringTableOffset);

					final boolean special = aArchiveEntry.isSpecial();
					if (!special) {
						aArchiveEntries.add(aArchiveEntry);
					}

					entryOffset = randomAccessFile.getFilePointer();

					final boolean stringTableEntry = aArchiveEntry.isStringTableEntry();
					if (stringTableEntry) {
						stringTableOffset = entryOffset;
					}

					entryOffset += aArchiveEntry.getSize();
					if ((entryOffset % 2) != 0) {
						entryOffset++;
					}

					randomAccessFile.seek(entryOffset);
				}

				if(fileOffsetParam != null) {
				    System.out.printf("");
                }

				return new AArchive(aFile, aArchiveEntries);
			}

		} catch (final Exception exc) {
			Logger.printError("failed to parse the A file:%n%s", aFile);
			Logger.printException(exc);
			return null;
		}
	}

	private static boolean isAArchiveHeader(final byte[] bytes) {

		return Arrays.equals(bytes, A_ARCHIVE_HEADER);
	}
}
