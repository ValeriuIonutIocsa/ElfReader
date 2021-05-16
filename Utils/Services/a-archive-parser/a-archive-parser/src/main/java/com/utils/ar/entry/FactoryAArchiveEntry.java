package com.utils.ar.entry;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.utils.log.Logger;

public class FactoryAArchiveEntry {

	static final int HEADER_LEN = 60;
	private static final int NAME_IDX = 0;
	private static final int NAME_LEN = 16;
	private static final int MOD_TIME_IDX = 16;
	private static final int MOD_TIME_LEN = 12;
	private static final int UID_IDX = 28;
	private static final int UID_LEN = 6;
	private static final int GID_IDX = 34;
	private static final int GID_LEN = 6;
	private static final int MODE_IDX = 40;
	private static final int MODE_LEN = 8;
	private static final int SIZE_IDX = 48;
	private static final int SIZE_LEN = 10;
	private static final int MAGIC_IDX = 58;
	private static final int MAGIC_LEN = 2;

	private static final byte[] MAGIC_NUMBER = new byte[] { 0x60, 0x0A };

	public static final FactoryAArchiveEntry INSTANCE = new FactoryAArchiveEntry();

	private FactoryAArchiveEntry() {
	}

	public AArchiveEntry parse(
			final RandomAccessFile randomAccessFile,
			final long entryOffset, final Long stringTableOffset) throws Exception {

		final byte[] headerBytes = new byte[HEADER_LEN];

		randomAccessFile.readFully(headerBytes);

		final String entryName = new String(headerBytes, NAME_IDX, NAME_LEN).trim();
		final String fileName = computeFileName(randomAccessFile, entryName, stringTableOffset);
		final long size = Long.parseLong(new String(headerBytes, SIZE_IDX, SIZE_LEN).trim());

		final boolean special = AArchiveEntry.isFirstEntry(entryName) || AArchiveEntry.isStringTableEntry(entryName);
		final Long modificationTime;
		final Integer uid;
		final Integer gid;
		final Integer mode;
		if (!special) {
			modificationTime = Long.parseLong(new String(headerBytes, MOD_TIME_IDX, MOD_TIME_LEN).trim());
			uid = Integer.parseInt(new String(headerBytes, UID_IDX, UID_LEN).trim());
			gid = Integer.parseInt(new String(headerBytes, GID_IDX, GID_LEN).trim());
			mode = Integer.parseInt(new String(headerBytes, MODE_IDX, MODE_LEN).trim(), 8);

			final byte[] magicNumberBytes = Arrays.copyOfRange(headerBytes, MAGIC_IDX, MAGIC_IDX + MAGIC_LEN);
			if (!Arrays.equals(magicNumberBytes, MAGIC_NUMBER)) {
				Logger.printError("Not a valid AR archive! No file header magic number found.");
				throw new Exception();
			}

		} else {
			modificationTime = null;
			uid = null;
			gid = null;
			mode = null;
		}

		return new AArchiveEntry(entryOffset, fileName, size, special, modificationTime, uid, gid, mode);
	}

	private String computeFileName(
			final RandomAccessFile randomAccessFile,
			final String entryName, final Long stringTableOffset) throws Exception {

		String fileName = entryName;
		final boolean bsdArExtendedFileName = fileName.matches("^#1/\\d+$");
		if (bsdArExtendedFileName) {
			try {
				final int fileNameLength = Integer.parseInt(fileName.substring(3));
				if (fileNameLength > 0) {
					final byte[] entryNameBytes = new byte[fileNameLength];
					randomAccessFile.readFully(entryNameBytes);

					fileName = new String(entryNameBytes).trim();
				}

			} catch (final NumberFormatException exception) {
				Logger.printError("Invalid AR archive! (BSD) Extended filename invalid?!");
				throw new Exception();
			}
		}

		final boolean gnuArExtendedFileName = fileName.matches("^/\\d+$");
		if (gnuArExtendedFileName) {
			try {
				final long entryNameOffset = Long.parseLong(fileName.substring(1));
				fileName = parseNameFromStringTable(randomAccessFile, stringTableOffset, entryNameOffset);

			} catch (final NumberFormatException exception) {
				Logger.printError("Invalid AR archive! (GNU) Extended filename invalid?!");
				throw new Exception();
			}
		}

		final int len = fileName.length();
		if (len > 2 && fileName.charAt(len - 1) == '/') {
			fileName = fileName.substring(0, len - 1);
		}
		return fileName;
	}

	private static String parseNameFromStringTable(
			final RandomAccessFile randomAccessFile,
			final Long stringTableOffset, final long entryNameOffset) throws Exception {

		if (stringTableOffset == null) {
			Logger.printError("Invalid AR archive! The string table has not been read yet!");
			throw new Exception();
		}

		final long originalPos = randomAccessFile.getFilePointer();

		final StringBuilder name = new StringBuilder();
		try {
			randomAccessFile.seek(stringTableOffset + entryNameOffset);

			byte b;
			while ((b = randomAccessFile.readByte()) != '\n') {

				final char ch = (char) b;
				name.append(ch);
			}

		} finally {
			randomAccessFile.seek(originalPos);
		}

		return name.toString();
	}
}
