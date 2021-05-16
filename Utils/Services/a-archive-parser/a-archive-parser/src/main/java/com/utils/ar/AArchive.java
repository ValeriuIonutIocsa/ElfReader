package com.utils.ar;

import java.io.File;
import java.util.List;

import com.utils.ar.entry.AArchiveEntry;
import com.utils.string.StrUtils;

public class AArchive {

	private final File aFile;
	private final List<AArchiveEntry> entries;

	AArchive(final File aFile, final List<AArchiveEntry> entries) {

		this.aFile = aFile;
		this.entries = entries;
	}

	@Override
	public String toString() {

		return "AArchive{" +
				"path=" + aFile +
				", entries:" + System.lineSeparator() + StrUtils.prettyPrintCollection(entries) +
				'}';
	}

	public List<AArchiveEntry> getEntries() {
		return entries;
	}
}
