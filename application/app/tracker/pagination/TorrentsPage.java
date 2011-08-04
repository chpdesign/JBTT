package tracker.pagination;

import tracker.cache.TorrentsCache;
import tracker.cache.TorrentsPageSet;
import tracker.torrents.Torrent;

import java.io.Serializable;
import java.util.List;

public class TorrentsPage extends Page<Torrent> implements Serializable {
	public TorrentsPage(TorrentsPageSet pageSet) {
		super(pageSet);
	}

	public List<Torrent> getItems() throws Throwable {
		return TorrentsCache.getInstance().getByMultipleIds(this.getItemIds());
	}

	public TorrentsPageSet getPageSet() {
		return (TorrentsPageSet)super.getPageSet();
	}

	public String[] getCacheKeys() {
		String cacheKey = TorrentsPageSet.getPageCacheKey(
				this.getPageSet().getCategoryId(),
				this.getPageSet().getTagId(),
				this.getNumber()
		);
		return new String[] { cacheKey };
	}
}
