package com.utils.io;

import java.io.OutputStream;

public abstract class CustomOutputStream extends OutputStream {

	private final StringBuilder stringBuilder = new StringBuilder();

	@Override
	public void write(final int i) {

		final char ch = (char) i;
		if (ch == '\r') {
			return;
		}

		if (ch == '\n') {
			final String line = stringBuilder.toString();
			processLine(line);

			stringBuilder.setLength(0);
			return;
		}

		stringBuilder.append(ch);
	}

	protected abstract void processLine(String line);
}
