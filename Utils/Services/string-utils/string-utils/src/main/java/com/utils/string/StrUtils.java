package com.utils.string;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class StrUtils {

	public static String repeat(final String str, final int repeatCount) {

		return new String(new char[repeatCount]).replace("\0", str);
	}

	public static String doubleToString(final Double n, final int digitsCount) {

		if (n == null) {
			return "";
		}

		final String format = "#." + StrUtils.repeat("#", digitsCount);
		return new DecimalFormat(format).format(n);
	}

	public static String doubleToPercentageString(final double n, final int digitsCount) {

		return String.format("%." + digitsCount + "f", n * 100) + "%";
	}

	public static String durationToString(final Duration duration) {

		return duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
	}

	public static String createDateTimeString() {

		return DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
				.withZone(ZoneId.systemDefault()).format(Instant.now());
	}

	public static <T> String prettyPrintCollectionField(final Collection<T> collection) {

		if (collection == null || collection.isEmpty()) {
			return "[]";
		}

		return String.format("[%n%s%n]", prettyPrintCollection(collection));
	}

	public static <T> String prettyPrintCollection(final Collection<T> collection) {

		if (collection == null) {
			return "";
		}

		return collection.stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator()));
	}

	public static <K, V> String prettyPrintMapField(final Map<K, V> map) {

		if (map == null || map.isEmpty()) {
			return "[]";
		}

		return String.format("[%n%s%n]", prettyPrintMap(map));
	}

	public static <K, V> String prettyPrintMap(final Map<K, V> map) {

		if (map == null) {
			return "";
		}

		return prettyPrintCollection(map.entrySet());
	}
}
