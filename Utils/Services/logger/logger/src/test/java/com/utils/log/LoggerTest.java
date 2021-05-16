package com.utils.log;

import java.time.Instant;

import org.junit.Test;

public class LoggerTest {

	@Test
	public void testPrintFinishMessage() {

		final Instant start = Instant.now();
		Logger.printFinishMessage(start);
	}
}
