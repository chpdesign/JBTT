package tracker.cache;

import tracker.torrents.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagsList {
	private static Map<Integer, Tag> tagsIdMap = new HashMap<Integer, Tag>();
	private static Map<String, Tag> tagsKeyMap = new HashMap<String, Tag>();

	static {
		try {
			List<Tag> tags = Tag.byConditionMultiple("1");
			for (Tag tag : tags) {
				tagsIdMap.put(tag.getId(), tag);
				tagsKeyMap.put(tag.getKey(), tag);
			}
		} catch (Throwable exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	public static Tag getById(Integer id) {
		return tagsIdMap.get(id);
	}

	public static Tag getByKey(String key) {
		return tagsKeyMap.get(key);
	}
}
