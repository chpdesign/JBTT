package tracker.util.bbcode;

public abstract class AbstractCode {
	public abstract String[] getTagNames();

	public String getOpenTagHtml() {
		return this.getOpenTagHtml(null);
	}

	public abstract String getOpenTagHtml(String attribute);

	public String getCloseTagHtml() {
		return "";
	}

	public String toString() {
		return this.getTagNames()[0];
	}

	public boolean equalsTagName(String tagName) {
		for (String item : this.getTagNames()) {
			if (tagName.equalsIgnoreCase(item)) {
				return true;
			}
		}

		return false;
	}

	public boolean equalsTagNames(String[] tagNames) {
		for (String tagName : tagNames) {
			if (this.equalsTagName(tagName)) {
				return true;
			}
		}

		return false;
	}

	public boolean equals(AbstractCode code) {
		return this.getClass().getCanonicalName().equalsIgnoreCase(code.getClass().getCanonicalName());
	}

	public String[] getAllowedChildTags() {
		return null;
	}

	public String[] getAllowedParentTags() {
		return null;
	}

	public class IncorrectAttributeException extends Exception {
		public IncorrectAttributeException() { super(); }
		public IncorrectAttributeException(String message) { super(message); }
	}
}
