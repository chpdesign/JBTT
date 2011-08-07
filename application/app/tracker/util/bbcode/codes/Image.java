package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Image extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "img" };
	}

	public String getOpenTagHtml(String attribute) {
		if (attribute == null || attribute.length() < 1) {
			return "<img />";
		} else {
			Pattern pattern = Pattern.compile("\\[img((=([0-9]+)x([0-9]+))|( width=\"([0-9]+)\" height=\"([0-9]+)\")|)\\]",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Matcher matcher = pattern.matcher("");

			if (!attribute.matches("[^\"]+")) { // TODO: check me
				return "<img />";
			}

			return "<img src=\"%attribute%\" />".replace("%attribute%", attribute);
		}
	}
}
