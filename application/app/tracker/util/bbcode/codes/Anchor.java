package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Anchor extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "a", "url" };
	}

	public String getOpenTagHtml(String attribute) {
		if (attribute == null || attribute.length() < 1) {
			return "<a>";
		} else {
//			if (!attribute.matches("(#[0-9a-fA-F]{1,6})|([a-zA-Z]+)|(inherit)") ) {
//				attribute = "inherit";
//			}

			return "<a href=\"%attribute%\">".replace("%attribute%", attribute);
		}
	}

	public String getCloseTagHtml() {
		return "</a>";
	}
}
