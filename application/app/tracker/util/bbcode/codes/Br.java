package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Br extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "br" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<br />";
	}

	public String getCloseTagHtml() {
		return "";
	}
}
