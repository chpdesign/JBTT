package tracker.util.bbcode;

import play.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBString {
	protected String source = null;
	protected String html = null;

	public BBString() {
		this("");
	}

	public BBString(String source) {
		this.source = source;
		this.parse();
	}

	protected void parse() {
		List<Tag> tags = new ArrayList<Tag>();
		List<Tag> openTags = new ArrayList<Tag>();

		String attributeRegex = "(\\s|)=(\\s|)(.*?)";
		String commonRegex = "\\[(|/)\\s*?(\\w+?)(|" + attributeRegex + ")\\s*?\\]";

		Pattern pattern = Pattern.compile(commonRegex);
		Matcher matcher = pattern.matcher(this.getSource());
		while (matcher.find()) {
			Tag.Type type = (matcher.group(1).length() == 0) ? Tag.Type.OPEN : Tag.Type.CLOSE;
			String name = matcher.group(2);

			String attribute = null;
			if (matcher.group(6) != null) {
				attribute = matcher.group(6).trim();
				if (attribute.startsWith("\"") || attribute.endsWith("\"")) {
					attribute = attribute.substring(1, attribute.length() - 1);
				}

				if (attribute.startsWith("'") || attribute.endsWith("'")) {
					attribute = attribute.substring(1, attribute.length() - 1);
				}
			}

			AbstractCode code = BBCodes.getByName(name);
			if (code == null) {
				continue;
			}

			Tag tag = new Tag();
			tag.setStartPosition(matcher.start());
			tag.setEndPosition(matcher.end());
			tag.setType(type);
			tag.setCode(code);
			tag.setAttribute(attribute);

			tags.add(tag);

			if (tag.getType() == Tag.Type.OPEN) {
				// Добавляем новый открытый тег.
				openTags.add(tag);
			} else {
				boolean hasPair = false;

				if (openTags.size() > 0) {
					for (int i = openTags.size() - 1; i >= 0; i--) {
						if (!openTags.get(i).getCode().equals(tag.getCode())) {
							// Это другой тег.
							continue;
						}

						if (openTags.get(i).getPair() != null) {
							// Этот тег уже закрыт.
							continue;
						}

						// Последний незакрытый тег.
						openTags.get(i).setPair(tag);
						hasPair = true;

						// Удаляем тег из открытых.
						openTags.remove(i);

						break;
					}

					if (!hasPair) {
						// Если у закрывающего тега нет пары, то удаляем его.
						tag.setRemoved(true);
					}
				} else {
					tag.setRemoved(true);
				}
			}
		}

		for (ListIterator iterator = openTags.listIterator(openTags.size()); iterator.hasPrevious();) {
			final Tag tag = (Tag)iterator.previous();
			if (tag.getPair() == null) {
				// Если у открывающего тега нет пары, то закрываем его в конце.
				Tag closeTag = new Tag();
				closeTag.setStartPosition(this.getSource().length());
				closeTag.setEndPosition(this.getSource().length());
				closeTag.setType(Tag.Type.CLOSE);
				closeTag.setCode(tag.getCode());

				tags.add(closeTag);
			}
		}

		// Делаем XHTML-валидный код.
		tags = this.fixMarkup(tags);

		List<Tag> tagsTree = this.buildTree(tags);

		// Применяем правила тегов.
		this.fixTags(tagsTree);

		// TODO: build HTML by tags tree
		this.buildHtml(tags);
	}

	// К этому моменту не должно быть незакрытых тегов.
	protected List<Tag> fixMarkup(List<Tag> sourceTags) {
		List<Tag> resultTags = new ArrayList<Tag>();

		for (int tagIndex = 0; tagIndex < sourceTags.size(); tagIndex++) {
			Tag tag = sourceTags.get(tagIndex);

			if (tag.isRemoved()) {
				continue;
			}

			List<Tag> closeTags = new ArrayList<Tag>();
			List<Tag> openTags = new ArrayList<Tag>();

			if (tag.getType() == Tag.Type.CLOSE) {
				int pairIndex = sourceTags.indexOf(tag.getPair());
				for (int checkTagIndex = tagIndex - 1; checkTagIndex > pairIndex; checkTagIndex--) {
					Tag checkTag = sourceTags.get(checkTagIndex);

					if (checkTag.isRemoved()) {
						continue;
					}

					int checkTagPairIndex = sourceTags.indexOf(checkTag.getPair());
					if (checkTag.getType() == Tag.Type.OPEN && checkTagPairIndex > tagIndex) {
						Tag closeTag = new Tag();
						closeTag.setStartPosition(tag.getStartPosition());
						closeTag.setEndPosition(tag.getStartPosition());
						closeTag.setType(Tag.Type.CLOSE);
						closeTag.setCode(checkTag.getCode());
						closeTags.add(closeTag);

						Tag openTag = new Tag();
						openTag.setAttribute(checkTag.getAttribute());
						openTag.setStartPosition(tag.getEndPosition());
						openTag.setEndPosition(tag.getEndPosition());
						openTag.setType(Tag.Type.OPEN);
						openTag.setCode(checkTag.getCode());
						openTags.add(openTag);
					}
				}
			}

			for (Tag closeTag : closeTags) {
				resultTags.add(closeTag);
			}

			resultTags.add(tag);

			for (ListIterator iterator = openTags.listIterator(openTags.size()); iterator.hasPrevious();) {
				final Tag openTag = (Tag)iterator.previous();
				resultTags.add(openTag);
			}
		}

		return resultTags;
	}

	protected List<Tag> buildTree(List<Tag> sourceTags) {
		List<Tag> resultTags = new ArrayList<Tag>();

		for (int tagIndex = 0; tagIndex < sourceTags.size(); tagIndex++) {
			Tag tag = sourceTags.get(tagIndex);

			if (tag.isRemoved()) {
				continue;
			}

			if (tag.getType() == Tag.Type.OPEN) {
				if (tag.getPair() != null) {
					tagIndex = sourceTags.indexOf(tag.getPair()) - 1;
				}
				continue;
			}

			List<Tag> subList = sourceTags.subList(
					sourceTags.indexOf(tag.getPair()) + 1,
					sourceTags.indexOf(tag)
			);

			tag.setChildren(this.buildTree(subList));

			resultTags.add(tag);
		}

		return resultTags;
	}

	protected List<Tag> fixTags(List<Tag> tagsTree) {
		for (Tag tag : tagsTree) {
			String[] allowedParentTags = tag.getCode().getAllowedParentTags();
			if (allowedParentTags != null) {
				if (tag.getParent() == null) {
					tag.setRemoved(true);
				} else if (!tag.getParent().getCode().equalsTagNames(allowedParentTags)) {
					tag.setRemoved(true);
				}
			}

			if (tag.getChildren() != null) {
				for (Tag childTag : tag.getChildren()) {
					String[] allowedChildTags = tag.getCode().getAllowedChildTags();
					if (allowedChildTags != null) {
						if (!childTag.getCode().equalsTagNames(allowedChildTags)) {
							childTag.setRemoved(true);
						}
					}
				}
			}

			if (tag.getChildren() != null) {
				this.fixTags(tag.getChildren());
			}
		}

		return tagsTree;
	}

	protected void buildHtml(List<Tag> tags) {
		if (tags.size() < 1) {
			this.html = this.getSource();
			return;
		}

		String html = this.getSource().substring(0, tags.get(0).getStartPosition());

		for (int i = 0; i < tags.size(); i++) {
			Tag tag = tags.get(i);
			Tag nextTag = (i < tags.size() - 1) ? tags.get(i + 1) : null;

			html += tag.getHtml();

			if (nextTag != null) {
				html += this.getSource().substring(tag.getEndPosition(), nextTag.getStartPosition());
			} else {
				html += this.getSource().substring(tag.getEndPosition());
			}
		}

		this.html = html;
	}

	public String getSource() {
		return this.source;
	}

	public String getHtml() {
		return this.html;
	}

	public String toString() {
		return this.getHtml();
	}
}
