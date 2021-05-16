package com.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;

import com.utils.io.IoUtils;
import com.utils.log.Logger;

public class FileLocker {

	private final Path lockFilePath;

	private RandomAccessFile randomAccessFile;
	private FileChannel fileChannel;
	private FileLock fileLock;

	public FileLocker(final Path lockFilePath) {
		this.lockFilePath = lockFilePath;
	}

	public boolean lock() {

		try {
			Logger.printProgress("acquiring file lock");

			if (IoUtils.fileExists(lockFilePath)) {
				try {
					Files.write(lockFilePath, "locked by T1ApiMeasurementServer".getBytes());

				} catch (final Exception ignored) {
					return false;
				}

				lockExistingFile();
				return true;

			} else {
			    IoUtils.createParentDirectories(lockFilePath);
				Files.createFile(lockFilePath);
				lockExistingFile();
				return true;
			}

		} catch (final Exception exc) {
			Logger.printError("failed to acquire file lock!");
			Logger.printException(exc);
			return true;
		}
	}

	private void lockExistingFile() throws IOException {

		final File lockFile = lockFilePath.toFile();
		randomAccessFile = new RandomAccessFile(lockFile, "rw");
		fileChannel = randomAccessFile.getChannel();
		fileLock = fileChannel.lock();
	}

	public void unlock() {

		try {
			Logger.printProgress("releasing the lock");

			if (fileLock == null) {
				Logger.printError("file is not locked!");
				return;
			}

			fileLock.release();

			if (fileChannel == null) {
				Logger.printError("file channel is null!");
				return;
			}

			fileChannel.close();

			if (randomAccessFile == null) {
				Logger.printError("random access file is null!");
				return;
			}

			randomAccessFile.close();

			IoUtils.deleteFile(lockFilePath);

		} catch (final Exception exc) {
			Logger.printError("failed to release the lock!");
			Logger.printException(exc);
		}
	}
}
