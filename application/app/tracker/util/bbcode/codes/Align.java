package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

import java.util.Arrays;

public class Align extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "align" };
	}

	public String getOpenTagHtml(String attribute) {
		if (attribute == null || attribute.length() < 1) {
			return "<span>";
		} else {
			if (!Arrays.asList("left", "center", "right", "justify", "inherit").contains(attribute)) { // TODO: check case
				attribute = "left";
			}

			return "<span style=\"text-align: %attribute%\">".replace("%attribute%", attribute);
		}
	}

	public String getCloseTagHtml() {
		return "</span>";
	}
}
