package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Strong extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "strong", "b" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<strong>";
	}

	public String getCloseTagHtml() {
		return "</strong>";
	}
}
