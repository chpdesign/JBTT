package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Em extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "em", "i" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<em>";
	}

	public String getCloseTagHtml() {
		return "</em>";
	}
}
