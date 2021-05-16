package com.utils.ar.entry;

import org.apache.commons.lang3.StringUtils;

public class AArchiveEntry {

	private final long entryOffset;
	private final String fileName;
	private final long size;
	private final boolean special;
	private final Long modificationTime;
	private final Integer uid;
	private final Integer gid;
	private final Integer mode;

	AArchiveEntry(
			final long entryOffset, final String fileName, final long size, final boolean special,
			final Long modificationTime, final Integer uid, final Integer gid, final Integer mode) {

		this.entryOffset = entryOffset;
		this.fileName = fileName;
		this.size = size;
		this.special = special;
		this.modificationTime = modificationTime;
		this.uid = uid;
		this.gid = gid;
		this.mode = mode;
	}

	@Override
	public String toString() {

		return "AArchiveEntry{" +
				"entryOffset=" + entryOffset +
				", fileName='" + fileName + '\'' +
				", size=" + size +
				", special=" + special +
				", modificationTime=" + modificationTime +
				", uid=" + uid +
				", gid=" + gid +
				", mode=" + mode +
				'}';
	}

	public boolean isStringTableEntry() {

		return isStringTableEntry(fileName);
	}

	static boolean isFirstEntry(final String entryName) {
		return StringUtils.equals(entryName, "/");
	}

	static boolean isStringTableEntry(final String entryName) {
		return StringUtils.equals(entryName, "//");
	}

	public long getFileOffset() {

		return entryOffset + FactoryAArchiveEntry.HEADER_LEN;
	}

	public String getFileName() {
		return fileName;
	}

	public long getSize() {
		return size;
	}

	public boolean isSpecial() {
		return special;
	}
}
