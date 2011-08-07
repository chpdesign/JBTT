package tracker.util.bbcode;

import java.util.List;

public class Tag {
	protected Integer startPosition = null;
	protected Integer endPosition = null;
	protected AbstractCode code = null;
	protected String attribute = null;
	protected Type type = null;
	protected Tag pair = null;
	protected boolean removed = false;

	protected Tag parent = null;
	protected List<Tag> children = null;

	public Tag() { }

	public Tag(Integer startPosition, Integer endPosition, AbstractCode code, Type type, Tag pair, boolean isRemoved) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.code = code;
		this.type = type;
		this.pair = pair;
		this.removed = isRemoved;
	}

	public Integer getStartPosition() { return this.startPosition; }
	public void setStartPosition(Integer startPosition) { this.startPosition = startPosition; }

	public Integer getEndPosition() { return endPosition; }
	public void setEndPosition(Integer endPosition) { this.endPosition = endPosition; }

	public AbstractCode getCode() { return this.code; }
	public void setCode(AbstractCode code) { this.code = code; }

	public String getAttribute() { return this.attribute; }
	public void setAttribute(String attribute) { this.attribute = attribute; }

	public Type getType() { return this.type; }
	public void setType(Type type) { this.type = type; }

	public Tag getPair() { return this.pair; }
	public void setPair(Tag pair) {
		this.pair = pair;

		if (pair.getPair() == null || !pair.getPair().equals(this)) {
			pair.setPair(this);
		}
	}

	public boolean isRemoved() { return this.removed; }
	public void setRemoved(boolean removed) {
		this.removed = removed;

		if (this.getPair() != null && !this.getPair().isRemoved()) {
			this.getPair().setRemoved(true);
		}

		if (this.getChildren() != null) {
			for (Tag child : this.getChildren()) {
				child.setRemoved(true);
			}
		}
	}

	public Tag getParent() { return this.parent; }
	public void setParent(Tag parent) { this.parent = parent; }

	public List<Tag> getChildren() { return this.children; }
	public void setChildren(List<Tag> children) {
		this.children = children;
		for (Tag child : this.children) {
			child.setParent(this);
		}
	}

	public String getHtml() {
		String html = "";

		if (this.isRemoved()) {
			return html;
		}

		if (this.getType() == Tag.Type.OPEN) {
			html += this.getCode().getOpenTagHtml(this.getAttribute());
		} else {
			html += this.getCode().getCloseTagHtml();
		}

		return html;
	}

	public String toString() {
		try {
			return this.getHtml();
		} catch (Exception ignored) {
			return "";
		}
	}

	public enum Type {
		OPEN,
		CLOSE,
	}
}
