package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Code extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "code", "pre" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<pre class=\"code\">";
	}

	public String getCloseTagHtml() {
		return "</pre>";
	}
}
