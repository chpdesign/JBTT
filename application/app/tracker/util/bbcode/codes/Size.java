package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class Size extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "size" };
	}

	public String getOpenTagHtml(String attribute) {
		if (attribute == null || attribute.length() < 1) {
			return "<span>";
		} else {
			if (!attribute.matches("(([0-9\\.]{1,6}+)(em|ex|pt|px|%))|(larger)|(smaller)|(xx-small)|(x-small)|(small)|(medium)|(large)|(x-large)|(xx-large)")) {
				attribute = "font-size: 100%";
			}

			// TODO: size limits like: 3px-100px, 1%-1000%, etc...

			return "<span style=\"font-size: %attribute%\">".replace("%attribute%", attribute);
		}
	}

	public String getCloseTagHtml() {
		return "</span>";
	}
}
