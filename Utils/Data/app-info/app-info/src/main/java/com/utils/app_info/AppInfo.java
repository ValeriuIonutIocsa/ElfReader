package com.utils.app_info;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class AppInfo {

	private static String appTitleDefault;
	private static String appVersionDefault;

	public static void initialize(final String appTitleDefault, final String appVersionDefault) {

		AppInfo.appTitleDefault = appTitleDefault;
		AppInfo.appVersionDefault = appVersionDefault;
	}

	public static String getAppTitleAndVersion(final Class<?> cl) {

		return AppInfo.getAppTitle(cl) + " v" + AppInfo.getAppVersion(cl);
	}

	public static String getAppTitle(final Class<?> cl) {

		final Package classPackage = cl.getPackage();
		String appTitle = classPackage.getImplementationTitle();
		appTitle = StringUtils.isNotBlank(appTitle) ? appTitle : AppInfo.appTitleDefault;
		return AppInfo.formatTitle(appTitle);
	}

	static String formatTitle(final String appTitle) {

		final String alphanumericTitle = appTitle.replaceAll("[^A-Za-z0-9]", " ");
		return Arrays.stream(alphanumericTitle.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
				.map(String::trim).collect(Collectors.joining(" "));
	}

	public static String getAppVersion(final Class<?> cl) {

		final Package classPackage = cl.getPackage();
		final String appVersion = classPackage.getImplementationVersion();
		return StringUtils.isNotBlank(appVersion) ? appVersion : AppInfo.appVersionDefault;
	}
}
