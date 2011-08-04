package tracker.pagination;

import play.Logger;
import tracker.cache.CommentsPageSet;

import java.util.HashMap;
import java.util.Map;

public class CommentsPaginator {
	private final static Map<String, CommentsPageSet> pageSets = new HashMap<String, CommentsPageSet>();

	public static void reset() throws Throwable {
		for (CommentsPageSet pageSet : pageSets.values()) {
			Logger.debug("Reset for " + pageSet);
			pageSet.reset();
		}
	}

	public static CommentsPageSet getPageSet(Long torrentId) {
		String cacheKey = getPageSetCacheKey(torrentId);
		CommentsPageSet value = pageSets.get(cacheKey);
		if (value == null) {
			value = new CommentsPageSet(torrentId);
			pageSets.put(cacheKey, value);
		}
		return value;
	}

	public static CommentsPage get(Long torrentId, Integer pageNumber) throws Throwable {
		CommentsPageSet pageSet = getPageSet(torrentId);
		return pageSet.getByNumber(pageNumber);
	}

	public static String getPageSetCacheKey(Long torrentId) {
		return String.format("comments-%s", torrentId);
	}
}
