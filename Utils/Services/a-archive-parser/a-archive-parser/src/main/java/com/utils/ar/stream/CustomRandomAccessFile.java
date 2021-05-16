package com.utils.ar.stream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CustomRandomAccessFile extends RandomAccessFile {

	private final Long fileOffset;
	private final Long fileLength;

	public CustomRandomAccessFile(
			final File file, final String mode,
			final Long fileOffset, final Long fileLength) throws Exception {

		super(file, mode);

		this.fileOffset = fileOffset;
		this.fileLength = fileLength;
	}

	@Override
	public void seek(long pos) throws IOException {

		if (fileOffset != null) {
			pos += fileOffset;
		}
		super.seek(pos);
	}

	@Override
	public long getFilePointer() throws IOException {

		long filePointer = super.getFilePointer();
		if (fileOffset != null) {
			filePointer -= fileOffset;
		}
		return filePointer;
	}

	@Override
	public long length() throws IOException {

		return fileLength != null ?
				fileLength : super.length();
	}
}
