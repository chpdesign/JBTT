package controllers.site;

import controllers.CommonController;
import tracker.cache.TagsList;
import tracker.cache.TorrentStatsCache;
import tracker.pagination.PageNavigation;
import tracker.pagination.TorrentsPage;
import tracker.pagination.TorrentsPaginator;
import tracker.torrents.Tag;

public class Torrents extends CommonController {
	public static void page(Integer categoryId, String tagKey, Integer pageId) throws Throwable {
		Integer tagId = null;
		if (tagKey != null) {
			Tag tag = TagsList.getByKey(tagKey);
			if (tag != null) {
				tagId = tag.getId();
			}
		}

		if (pageId == null) {
			pageId = 1;
		}

		TorrentsPage torrentsPage = TorrentsPaginator.get(categoryId, tagId, pageId);
		PageNavigation pageNavigation = new PageNavigation(torrentsPage.getPageSet(), pageId);

		// Precache torrent stats.
		TorrentStatsCache.getInstance().getByMultipleIds(torrentsPage.getItemIds());

		// TODO: Precache comment stats

		render(torrentsPage, pageNavigation);
	}
}
