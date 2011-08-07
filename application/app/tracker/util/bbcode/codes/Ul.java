package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Ul extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "ul" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<ul>";
	}

	public String getCloseTagHtml() {
		return "</ul>";
	}

	public String[] getAllowedChildTags() {
//		return null;
		return new String[] { "li" };
	}
}
