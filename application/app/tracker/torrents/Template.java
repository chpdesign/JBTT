package tracker.torrents;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tracker.torrents.templates.Field;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

public class Template {
	protected String templateName;

	protected String title;
	protected List<Field> fields;

	public Template(String templateName) throws Throwable {
		this.templateName = templateName;

		String templateFilename = String.format("application/conf/templates/%s.xml", templateName);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(templateFilename);

		Element rootNode = document.getDocumentElement();

		this.title = rootNode.getElementsByTagName("title").item(0).getTextContent();

		NodeList fieldsNodeList = rootNode.getElementsByTagName("field");

		for (int i = 0; i < fieldsNodeList.getLength(); i++) {
			Node fieldNode = fieldsNodeList.item(i);


		}
	}
}
