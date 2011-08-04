package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class H1 extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "h1" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<h1>";
	}

	public String getCloseTagHtml() {
		return "</h1>";
	}
}
