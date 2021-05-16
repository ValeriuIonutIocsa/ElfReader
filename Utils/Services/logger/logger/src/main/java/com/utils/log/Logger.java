package com.utils.log;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.utils.string.StrUtils;

public class Logger {

	private static boolean debugMode = false;
	private static MessagePrinter messagePrinter = new MessagePrinterImpl();

	public static void printNewLine() {
		printLine("");
	}

	public static void printLine(final String format, final Object... values) {

		final String formattedMessage = String.format(format, values);
		printLine(formattedMessage);
	}

	public static void printLine(final Object object) {

		final String message = object.toString();
		printLine(message);
	}

	public static void printLine(final String message) {

		if (messagePrinter != null) {
			messagePrinter.printMessage(MessageLevel.INFO, message);
		}
	}

	public static void printProgress(final String format, final Object... values) {

		final String formattedMessage = String.format(format, values);
		printProgress(formattedMessage);
	}

	public static void printProgress(final Object object) {

		final String message = object.toString();
		printProgress(message);
	}

	public static void printProgress(String message) {

		message = "--> " + message;
		if (messagePrinter != null) {
			messagePrinter.printMessage(MessageLevel.PROGRESS, message);
		}
	}

	public static void printStatus(final String format, final Object... values) {

		final String formattedMessage = String.format(format, values);
		printStatus(formattedMessage);
	}

	public static void printStatus(final Object object) {

		final String message = object.toString();
		printStatus(message);
	}

	public static void printStatus(final String message) {

		if (messagePrinter != null) {
			messagePrinter.printMessage(MessageLevel.STATUS, message);
		}
	}

	public static void printWarning(final String format, final Object... values) {

		final String formattedMessage = String.format(format, values);
		printWarning(formattedMessage);
	}

	public static void printWarning(final Object object) {

		final String message = object.toString();
		printWarning(message);
	}

	public static void printWarning(String message) {

		message = "!!! " + message;
		if (messagePrinter != null) {
			messagePrinter.printMessage(MessageLevel.WARNING, message);
		}
	}

	public static void printError(final String format, final Object... values) {

		final String formattedMessage = String.format(format, values);
		printError(formattedMessage);
	}

	public static void printError(final Object object) {

		final String message = object.toString();
		printError(message);
	}

	public static void printError(String message) {

		message = System.lineSeparator() + "ERROR - " + message;
		if (messagePrinter != null) {
			messagePrinter.printMessage(MessageLevel.ERROR, message);
		}
	}

	public static void printException(final Exception exc) {

		final String message = exceptionToString(exc);
		if (messagePrinter != null) {
			messagePrinter.printMessage(MessageLevel.EXCEPTION, message);
		}
	}

	public static String exceptionToString(final Exception exc) {

		return System.lineSeparator() +
				"Exception of class \"" + exc.getClass().getSimpleName() + "\" has occurred!" +
				System.lineSeparator() + exc.getMessage() + System.lineSeparator() +
				Arrays.stream(exc.getStackTrace()).map(StackTraceElement::toString)
						.collect(Collectors.joining(System.lineSeparator()));
	}

	public static void printFinishMessage(final Instant start) {

		final Duration executionTime = Duration.between(start, Instant.now());
		printStatus("Done. Execution time: " + StrUtils.durationToString(executionTime));
	}

	public static void toBeImplemented(final String name) {

		printLine(System.lineSeparator() + name + " (to be implemented...)");
	}

	public static void setMessagePrinter(MessagePrinter messagePrinter) {

		if (messagePrinter == null) {
			messagePrinter = new MessagePrinterImpl();
		}
		Logger.messagePrinter = messagePrinter;
	}

	public static MessagePrinter getMessagePrinter() {
		return messagePrinter;
	}

	public static void setDebugMode(final boolean debugMode) {
		Logger.debugMode = debugMode;
	}

	public static boolean isDebugMode() {
		return debugMode;
	}
}
