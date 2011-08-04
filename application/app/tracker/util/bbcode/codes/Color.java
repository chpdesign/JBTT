package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Color extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "color" };
	}

	public String getOpenTagHtml(String attribute) {
		if (attribute == null || attribute.length() < 1) {
			return "<span>";
		} else {
			if (!attribute.matches("(#[0-9a-fA-F]{1,6})|([a-zA-Z]+)|(inherit)") ) {
				attribute = "inherit";
			}

			return "<span style=\"color: %attribute%\">".replace("%attribute%", attribute);
		}
	}

	public String getCloseTagHtml() {
		return "</span>";
	}
}
