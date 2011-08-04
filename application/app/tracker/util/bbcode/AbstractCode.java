package tracker.util.bbcode;

public abstract class AbstractCode {
	public abstract String[] getTagNames();

	public String getOpenTagHtml() {
		return this.getOpenTagHtml(null);
	}

	public abstract String getOpenTagHtml(String attribute);

	public abstract String getCloseTagHtml();

	public String toString() {
		return this.getTagNames()[0];
	}

	public boolean equals(AbstractCode code) {
		return this.getClass().getCanonicalName().equalsIgnoreCase(code.getClass().getCanonicalName());
	}

	public class IncorrectAttributeException extends Exception {
		public IncorrectAttributeException() { super(); }
		public IncorrectAttributeException(String message) { super(message); }
	}
}
