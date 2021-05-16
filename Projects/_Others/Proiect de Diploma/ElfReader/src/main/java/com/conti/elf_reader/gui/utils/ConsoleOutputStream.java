package com.conti.elf_reader.gui.utils;

import java.io.OutputStream;

import javafx.scene.control.TextArea;

public class ConsoleOutputStream extends OutputStream {

	private final TextArea textArea;
	private static final StringBuilder stringBuilder = new StringBuilder();

	public ConsoleOutputStream(final TextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void write(final int b) {

		final char ch = (char) b;
		if (ch == '\n') {
			final String line = stringBuilder.toString();
			appendText(line + System.lineSeparator());
			stringBuilder.setLength(0);
			return;
		}
		if (ch == '\r') {
			return;
		}

		stringBuilder.append(ch);
	}

	private void appendText(final String line) {

		GuiUtils.runAndWait(() -> {
			textArea.appendText(line);
			textArea.setScrollTop(Double.MAX_VALUE);
			textArea.deselect();
		});
	}
}
