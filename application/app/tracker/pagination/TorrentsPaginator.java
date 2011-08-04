package tracker.pagination;

import play.Logger;
import tracker.cache.TorrentsPageSet;

import java.util.HashMap;
import java.util.Map;

public class TorrentsPaginator {
	private final static Map<String, TorrentsPageSet> pageSets = new HashMap<String, TorrentsPageSet>();

	public static void reset() throws Throwable {
		for (TorrentsPageSet pageSet : pageSets.values()) {
			Logger.debug("Reset for " + pageSet);
			pageSet.reset();
		}
	}

	public static TorrentsPageSet getPageSet(Integer categoryId, Integer tagId) {
		String cacheKey = getPageSetCacheKey(categoryId, tagId);
		TorrentsPageSet value = pageSets.get(cacheKey);
		if (value == null) {
			value = new TorrentsPageSet(categoryId, tagId);
			pageSets.put(cacheKey, value);
		}
		return value;
	}

	public static TorrentsPage get(Integer categoryId, Integer tagId, Integer pageNumber) throws Throwable {
		TorrentsPageSet pageSet = getPageSet(categoryId, tagId);
		return pageSet.getByNumber(pageNumber);
	}

	public static String getPageSetCacheKey(Integer categoryId, Integer tagId) {
		return String.format("%s-%s-%s", "torrents", categoryId, tagId);
	}
}
