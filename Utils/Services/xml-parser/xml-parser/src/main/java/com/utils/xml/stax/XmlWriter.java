package com.utils.xml.stax;

import java.io.BufferedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;

import com.utils.io.IoUtils;
import com.utils.log.Logger;

import javanet.staxutils.IndentingXMLEventWriter;

public abstract class XmlWriter {

	private static final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
	private static final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();

	private final Path xmlFilePath;
	private final BufferedOutputStream bufferedOutputStream;
	private final XMLEventWriter xmlEventWriter;

	protected XmlWriter(final Path xmlFilePath) {

		this(xmlFilePath, true);
	}

	protected XmlWriter(final Path xmlFilePath, final boolean isIndenting) {

		this.xmlFilePath = xmlFilePath;

		BufferedOutputStream bufferedOutputStream = null;
		XMLEventWriter xmlEventWriter = null;
		try {
			IoUtils.createParentDirectories(xmlFilePath);

			bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(xmlFilePath));
			xmlEventWriter = xmlOutputFactory.createXMLEventWriter(bufferedOutputStream, "UTF-8");
			if (isIndenting) {
				final IndentingXMLEventWriter indentingXMLEventWriter =
						new IndentingXMLEventWriter(xmlEventWriter);
				indentingXMLEventWriter.setIndent("    ");
				xmlEventWriter = indentingXMLEventWriter;
			}

		} catch (final Exception exc) {
			Logger.printException(exc);
			Logger.printError("failed to create the XML file:" +
					System.lineSeparator() + xmlFilePath.toAbsolutePath().toString());
		}
		this.bufferedOutputStream = bufferedOutputStream;
		this.xmlEventWriter = xmlEventWriter;
	}

	public final void writeXml() {

		try {
			write();

		} catch (final Exception exc) {
			Logger.printException(exc);
			Logger.printError("failed to write .xml file:" +
					System.lineSeparator() + xmlFilePath.toAbsolutePath());

		} finally {
			closeStreams();
		}
	}

	protected abstract void write();

	private void closeStreams() {

		try {
			xmlEventWriter.close();
			bufferedOutputStream.close();
		} catch (final Exception ignored) {
		}
	}

	public void writeStartDocument() {

		writeStartDocument(false);
	}

	public void writeStartDocument(final boolean standalone) {

		writeStartDocument(xmlEventFactory.createStartDocument("UTF-8", "1.0", standalone));
	}

	protected void writeStartDocument(final StartDocument startDocument) {

		if (startDocument.isStandalone()) {
			writePlainText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + System.lineSeparator());
		} else {
			writeXmlEvent(startDocument);
		}
	}

	private void writePlainText(final String text) {

		try {
			bufferedOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
		} catch (final Exception exc) {
			Logger.printError("failed to write plain text to the .xml file!");
			Logger.printException(exc);
		}
	}

	public void writeEndDocument() {

		writeXmlEvent(xmlEventFactory.createEndDocument());
	}

	public void writeStartElement(final String tagName) {

		if (tagName == null || tagName.isEmpty()) {
			return;
		}

		writeXmlEvent(xmlEventFactory.createStartElement("", null, tagName));
	}

	public void writeAttribute(final String name, final String value) {

		if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
			return;
		}

		writeXmlEvent(xmlEventFactory.createAttribute(name, value));
	}

	public void writeAttribute(final QName qName, final String value) {

		if (qName == null || value == null || value.isEmpty()) {
			return;
		}

		writeXmlEvent(xmlEventFactory.createAttribute(qName, value));
	}

	public void writeCharacters(final String data) {

		if (data == null || data.isEmpty()) {
			return;
		}

		writeXmlEvent(xmlEventFactory.createCharacters(data));
	}

	public void writeCData(final String data) {

		if (data == null || data.isEmpty()) {
			return;
		}

		writeXmlEvent(xmlEventFactory.createCData(data));
	}

	public void writeEndElement(final String tagName) {

		if (tagName == null || tagName.isEmpty()) {
			return;
		}

		writeXmlEvent(xmlEventFactory.createEndElement("", null, tagName));
	}

	public void writeXmlEvent(final XMLEvent xmlEvent) {

		try {
			xmlEventWriter.add(xmlEvent);
		} catch (final Exception exc) {
			Logger.printError("failed to add element to xml!");
			Logger.printException(exc);
		}
	}
}
