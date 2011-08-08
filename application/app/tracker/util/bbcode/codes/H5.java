package tracker.util.bbcode.codes;

import tracker.util.bbcode.AbstractCode;

public class H5 extends AbstractCode {
	public String[] getTagNames() {
		return new String[] { "h5" };
	}

	public String getOpenTagHtml(String attribute) {
		return "<h5>";
	}

	public String getCloseTagHtml() {
		return "</h5>";
	}
}
