package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Underline extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "u" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<u>";
	}

	public String getCloseTagHtml() {
		return "</u>";
	}
}
