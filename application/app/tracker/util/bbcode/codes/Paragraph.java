package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Paragraph extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "p" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<p>";
	}

	public String getCloseTagHtml() {
		return "</p>";
	}
}
