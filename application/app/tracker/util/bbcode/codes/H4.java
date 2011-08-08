package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class H4 extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "h4" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<h4>";
	}

	public String getCloseTagHtml() {
		return "</h4>";
	}
}
