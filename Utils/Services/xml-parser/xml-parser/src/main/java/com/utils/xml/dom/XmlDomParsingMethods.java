package com.utils.xml.dom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.utils.io.IoUtils;
import com.utils.log.Logger;

public class XmlDomParsingMethods {

	public static Document createNewDocument() throws Exception {

		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.newDocument();
	}

	public static Document openDocument(final InputStream inputStream) throws Exception {

		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.parse(inputStream);
	}

	public static Document openDocument(final Path path) throws Exception {

		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.parse(path.toFile());
	}

	public static void saveXmlFile(final Document document, final String outputPathString) throws Exception {

		final Path outputPath = Paths.get(outputPathString);
		saveXmlFile(document, outputPath);
	}

	public static void saveXmlFile(final Document document, final Path outputPath) throws Exception {

		IoUtils.createParentDirectories(outputPath);
		final File file = outputPath.toFile();
		if (file.exists() && !file.canWrite()) {
			final boolean success = file.setWritable(true);
			if (!success) {
				Logger.printError("failed to make file writable:" + System.lineSeparator() + file);
				return;
			}
		}

		final StreamResult streamResult = new StreamResult(outputPath.toString());
		saveXml(document, streamResult);
	}

	public static String saveXmlFile(final Document document) throws Exception {

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final StreamResult streamResult = new StreamResult(byteArrayOutputStream);
		saveXml(document, streamResult);
		return byteArrayOutputStream.toString("UTF-8");
	}

	private static void saveXml(final Document document, final StreamResult streamResult) throws Exception {

		final TransformerFactory transformerFactory = TransformerFactory.newInstance();

		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		final DOMSource source = new DOMSource(document);
		transformer.transform(source, streamResult);
	}

	public static Element getFirstElementByTagName(final Element parentElement, final String tagName) {

		if (tagName == null) {
            return null;
        }

		final NodeList nodes = parentElement.getElementsByTagName(tagName);
		final int length = nodes.getLength();
		for (int i = 0; i < length; i++) {

			final Element element = (Element) nodes.item(i);
			if (tagName.equals(element.getTagName())) {
                return element;
            }
		}

		return null;
	}

	public static List<Element> getChildElementsByTagName(final Element element, final String tagName) {

		final List<Element> childElements = new ArrayList<>();
		if (tagName == null) {
            return childElements;
        }

		final NodeList childNodes = element.getChildNodes();
		final int childNodesLength = childNodes.getLength();
		for (int i = 0; i < childNodesLength; i++) {

			final Node childNode = childNodes.item(i);
			if (Node.ELEMENT_NODE == childNode.getNodeType()) {

				final Element childElement = (Element) childNode;
				final String elementTagName = childElement.getTagName();
				if (tagName.equals(elementTagName)) {
					childElements.add(childElement);
				}
			}
		}
		return childElements;
	}

	public static List<Element> getElementsByTagName(final Element parentElement, final String tagName) {

		final List<Element> elements = new ArrayList<>();
		final NodeList nodeList = parentElement.getElementsByTagName(tagName);
		final int nodeListLength = nodeList.getLength();
		for (int i = 0; i < nodeListLength; i++) {

			final Element element = (Element) nodeList.item(i);
			elements.add(element);
		}
		return elements;
	}

	public static void removeElementsByTagName(final Element parentElement, final String tagName) {

		final List<Element> elements = getElementsByTagName(parentElement, tagName);
		for (final Element element : elements) {
			parentElement.removeChild(element);
		}
	}
}
