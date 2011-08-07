package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Li extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "li" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<li>";
	}

	public String getCloseTagHtml() {
		return "</li>";
	}

	public String[] getAllowedParentTags() {
		return new String[] { "ul" };
	}
}
