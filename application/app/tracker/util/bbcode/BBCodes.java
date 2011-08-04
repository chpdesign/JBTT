package tracker.util.bbcode;

import tracker.util.bbcode.codes.*;

public class BBCodes {
	protected static final AbstractCode[] BBCODES = { // TODO: load classes automatically
		new Align(),
		new Br(),
		new Color(),
		new Em(),
		new H1(),
		new H2(),
		new H3(),
		new Image(),
		new Strike(),
		new Strong(),
		new Underline(),
	};

	public static AbstractCode getByName(String name) {
		for (AbstractCode code : BBCODES) {
			for (String tagName : code.getTagNames()) {
				if (tagName.equalsIgnoreCase(name)) {
					return code;
				}
			}
		}
		return null;
	}
}
