package com.utils.string;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class StrUtilsTest {

	@Test
	public void testDurationToStringShortTime() {

		final Duration duration = Duration.ofMillis(754321);
		final String durationString = StrUtils.durationToString(duration);
		assertEquals("12m 34.321s", durationString);
	}

	@Test
	public void testDurationToStringLongTime() {

		final Duration duration = Duration.ofSeconds(754321);
		final String durationString = StrUtils.durationToString(duration);
		assertEquals("209h 32m 1s", durationString);
	}

	@Test
	public void testPrettyPrintMap() {

		final Map<String, Integer> map = new HashMap<>();
		map.put("abc", 12);
		map.put("dbce", 155);
		map.put("yrsgdsf", 124213);
		System.out.println(StrUtils.prettyPrintMap(map));
	}
}
