package com.utils.xml.stax;

import com.utils.log.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;

public abstract class XmlReader {

    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    private final Path xmlFilePath;
    private final BufferedInputStream bufferedInputStream;
    private final XMLEventReader xmlEventReader;

    protected XmlReader(Path xmlFilePath) {

        this.xmlFilePath = xmlFilePath;

        BufferedInputStream bufferedInputStream = null;
        XMLEventReader xmlEventReader = null;
        try {
            bufferedInputStream = new BufferedInputStream(Files.newInputStream(xmlFilePath));
            xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
            xmlEventReader = xmlInputFactory.createXMLEventReader(bufferedInputStream);

        } catch (final Exception exc) {
            Logger.printError("failed to open the .xml file:" +
                    System.lineSeparator() + xmlFilePath.toAbsolutePath().toString());
            Logger.printException(exc);
        }
        this.bufferedInputStream = bufferedInputStream;
        this.xmlEventReader = xmlEventReader;
    }

    public final boolean readXml() {

        try {
            final Stack<String> pathInXml = new Stack<>();
            while (xmlEventReader.hasNext()) {

                final XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    final StartElement startElement = xmlEvent.asStartElement();
                    final String localPart = startElement.getName().getLocalPart();
                    pathInXml.push(localPart);
                    parseXmlEvent(pathInXml, startElement);

                } else if (xmlEvent.isEndElement()) {
                    parseXmlEvent(pathInXml, xmlEvent);
                    pathInXml.pop();

                } else {
                    parseXmlEvent(pathInXml, xmlEvent);
                }
            }

        } catch (final Exception exc) {
            Logger.printError("failed to read the .xml file:" + System.lineSeparator() + xmlFilePath);
            Logger.printException(exc);
            return false;

        } finally {
            closeStreams();
        }

        return true;
    }

    protected abstract void parseXmlEvent(Stack<String> pathInXml, XMLEvent xmlEvent);

    private void closeStreams() {

        try {
            xmlEventReader.close();
            bufferedInputStream.close();
        } catch (final Exception ignored) {
        }
    }

    public String getAttribute(StartElement startElement, String attributeName) {

        final Attribute attribute = startElement.getAttributeByName(new QName(attributeName));
        if (attribute != null)
            return attribute.getValue();

        return null;
    }
}
