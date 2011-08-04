package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Strike extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "strike", "s" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<strike>";
	}

	public String getCloseTagHtml() {
		return "</strike>";
	}
}
