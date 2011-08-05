package tracker.cache;

import org.apache.jcs.access.exception.CacheException;
import tracker.torrents.Comment;

import java.util.*;

public class CommentsCache extends AbstractCache<Comment> {
	public String getRegion() { return "comments"; }

	public Comment getById(Long id) throws Throwable {
		Comment value;
		if (this.isEnabled()) {
			try {
				value = (Comment)this.getCacheAccess().get("id-" + id);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				value = Comment.byId(id);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = Comment.byId(id);
		}
		return value;
	}

	public List<Comment> getByMultipleIds(List<Long> itemIds) throws Throwable {
		List<Comment> values;

		if (this.isEnabled()) {
			Map<Integer, Comment> valuesMap = new TreeMap<Integer, Comment>();

			Map<Long, Integer> requestItemIds = new HashMap<Long, Integer>();

			for (Integer pos = 0; pos < itemIds.size(); pos++) {
				Long itemId = itemIds.get(pos);

				Comment value;

				try {
					value = (Comment)this.getCacheAccess().get("id-" + itemId);
				} catch (ClassCastException classCastException) {
					value = null;
				}

				if (value != null) {
					valuesMap.put(pos, value);
				} else {
					requestItemIds.put(itemId, pos);
				}
			}

			List<Comment> requestedValues = Comment.byMultipleIds(
					new ArrayList<Long>(requestItemIds.keySet())
			);

			for (Comment requestedValue : requestedValues) {
				this.put(requestedValue);

				Integer pos = requestItemIds.get(requestedValue.getId());
				valuesMap.put(pos, requestedValue);
			}

			values = new ArrayList<Comment>(valuesMap.values());
		} else {
			values = Comment.byMultipleIds(itemIds);
		}

		return values;
	}

	private static volatile CommentsCache instance;

	public static CommentsCache getInstance() throws CacheException {
		if (instance == null) {
			synchronized (CommentsCache.class) {
				if (instance == null) {
					instance = new CommentsCache();
				}
			}
		}
		return instance;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
