package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class H2 extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "h2" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<h2>";
	}

	public String getCloseTagHtml() {
		return "</h2>";
	}
}
