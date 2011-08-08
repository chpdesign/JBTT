package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class H6 extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "h6" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<h6>";
	}

	public String getCloseTagHtml() {
		return "</h6>";
	}
}
