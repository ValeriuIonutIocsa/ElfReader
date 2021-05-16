package com.utils.app_info;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.utils.log.Logger;

public class AppInfoTest {

	@Test
	public void testFormatTitle1() {

		final String title = "T1AccessFrequencyMeasurement";
		final String formattedTitle = AppInfo.formatTitle(title);
		Logger.printLine(formattedTitle);
		assertEquals("T1 Access Frequency Measurement", formattedTitle);
	}

	@Test
	public void testFormatTitle2() {

		final String title = "PTU_CNF_SEL";
		final String formattedTitle = AppInfo.formatTitle(title);
		Logger.printLine(formattedTitle);
		assertEquals("PTU CNF SEL", formattedTitle);
	}

	@Test
	public void testFormatTitle3() {

		final String title = "AllocCtrl_T1AccessFrequencyMeasurement_ProjectAnalyzer";
		final String formattedTitle = AppInfo.formatTitle(title);
		Logger.printLine(formattedTitle);
		assertEquals("Alloc Ctrl T1 Access Frequency Measurement Project Analyzer", formattedTitle);
	}
}
