package com.utils.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.utils.log.Logger;

public class FileLockerTest {

	private static final Path LOCK_FILE_PATH = Paths.get("D:\\test\\lock_file.txt");

	@Test
	public void testLock() throws Exception {

		Logger.setDebugMode(true);

		final FileLocker fileLocker = new FileLocker(LOCK_FILE_PATH);
		final boolean success = fileLocker.lock();
		Logger.printLine(success);

		Thread.sleep(10000);
	}

	@Test
	public void testLockSequence() {

		Logger.setDebugMode(true);

		final FileLocker fileLocker = new FileLocker(LOCK_FILE_PATH);

		final boolean firstTimeSuccess = fileLocker.lock();
		assertTrue(firstTimeSuccess);

		final boolean secondTimeSuccess = fileLocker.lock();
		assertFalse(secondTimeSuccess);

		fileLocker.unlock();

		final boolean thirdTimeSuccess = fileLocker.lock();
		assertTrue(thirdTimeSuccess);

		fileLocker.unlock();
	}
}
