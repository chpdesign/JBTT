package tracker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
	protected static String profileName = null;
	protected static HashMap<String, Object> parameters = null;

	protected static List<String> loadedProfiles = null;

	public static Object set(String key, Object value) {
		return Config.parameters.put(key, value);
	}

	public static Object get(String key) {
		Object value = Config.parameters.get(key);
		if (value == null) {
			throw new UnexpectedException(key + " is not set");
		}
		return value;
	}

	public static Long getLong(String key) {
		Object value = Config.get(key);
		if (value instanceof Long) {
			return (Long)value;
		} else {
			throw new UnexpectedException(key + " is not a Long");
		}
	}

	public static Integer getInt(String key) {
		return Config.getLong(key).intValue();
	}

	public static Boolean getBoolean(String key) {
		Object value = Config.get(key);
		if (value instanceof Boolean) {
			return (Boolean)value;
		} else {
			throw new UnexpectedException(key + " is not a Boolean");
		}
	}

	public static String getString(String key) {
		Object value = Config.get(key);
		if (value instanceof String) {
			return (String)value;
		} else {
			throw new UnexpectedException(key + " is not a String");
		}
	}

	public static List<Object> getList(String key) {
		Object value = Config.get(key);

		if (value instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> listValue = (List<Object>)value;
			return listValue;
		} else {
			throw new UnexpectedException(key + " is not a List");
		}
	}

	public static void reload() throws Throwable {
		Config.load(Config.profileName);
	}

	public static void load() throws Throwable {
		Config.load(Play.configuration.getProperty("environment", "production")); // set in application.conf
	}

	public static void load(String profileName) throws Throwable {
		Config.load(profileName, true);
	}

	protected static void load(String profileName, boolean rootConfig) throws Throwable {
		if (rootConfig) {
			Config.profileName = profileName;
			Config.parameters = new HashMap<String, Object>();
			Config.loadedProfiles = new ArrayList<String>();
		}

		if (Config.loadedProfiles.contains(profileName)) {
			return;
		}
		Config.loadedProfiles.add(profileName);

		String profileFilename = String.format("application/conf/%s.xml", profileName);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(profileFilename);

		Element rootNode = document.getDocumentElement();
		if (rootNode.hasAttribute("overrides")) {
			Config.load(rootNode.getAttribute("overrides"), false);
		}

		Config.parseNode(rootNode);
	}

	protected static boolean parseNode(Element rootNode) throws Throwable {
		boolean foundChildElements = false;
		NodeList nodeList = rootNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			foundChildElements = true;

			Element element = (Element)nodeList.item(i);

			String typeName = element.getAttribute("type");
			if (typeName.equalsIgnoreCase("List")) {
				String path = Config.getElementPath(element);
				Object value = Config.parseListNode(element);
				Config.parameters.put(path, value);
				continue;
			}

			if (!Config.parseNode(element)) {
				String path = Config.getElementPath(element);

				Object value;
				if (typeName.length() > 0) {
					if (typeName.equalsIgnoreCase("Long")) {
						value = Long.parseLong(element.getTextContent());
					} else if (typeName.equalsIgnoreCase("Boolean")) {
						value = Boolean.parseBoolean(element.getTextContent());
					} else if (typeName.equalsIgnoreCase("String")) {
						value = element.getTextContent();
					} else {
						value = null;
						Logger.warn(path + " value type not implemented.");
					}
				} else {
					value = element.getTextContent();
				}

				Config.parameters.put(path, value);
			}
		}

		return foundChildElements;
	}

	protected static List<Object> parseListNode(Element rootNode) throws Throwable {
		List<Object> list = new ArrayList<Object>();

		NodeList nodeList = rootNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element element = (Element)nodeList.item(i);

			String typeName = element.getAttribute("type");
			Object value;
			if (typeName.length() > 0) {
				if (typeName.equalsIgnoreCase("Long")) {
					value = Long.parseLong(element.getTextContent());
				} else if (typeName.equalsIgnoreCase("Boolean")) {
					value = Boolean.parseBoolean(element.getTextContent());
				} else if (typeName.equalsIgnoreCase("String")) {
					value = element.getTextContent();
				} else if (typeName.equalsIgnoreCase("List")) {
					value = Config.parseListNode(element);
				} else {
					value = null;
					Logger.warn("Value type not implemented.");
				}
			} else {
				value = element.getTextContent();
			}

			list.add(value);
		}

		return list;
	}

	protected static String getElementPath(Element element) {
		String path = "";
		Node breadcrumbNode = element;
		while (breadcrumbNode != null) {
			if (breadcrumbNode.getNodeName().equalsIgnoreCase("config")) {
				break;
			}

			path = "." + breadcrumbNode.getNodeName() + path;
			breadcrumbNode = breadcrumbNode.getParentNode();
		}
		return path.substring(1);
	}
}
