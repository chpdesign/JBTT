package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class H3 extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "h3" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<h3>";
	}

	public String getCloseTagHtml() {
		return "</h3>";
	}
}
