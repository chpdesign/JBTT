package tracker.pagination;

import tracker.cache.CommentsCache;
import tracker.cache.CommentsPageSet;
import tracker.torrents.Comment;

import java.io.Serializable;
import java.util.List;

public class CommentsPage extends Page<Comment> implements Serializable {
	protected Long torrentId;

	public CommentsPage(CommentsPageSet pageSet) {
		super(pageSet);
		this.torrentId = pageSet.getTorrentId();
	}

	public Long getTorrentId() { return this.torrentId; }

	public List<Comment> getItems() throws Throwable {
		return CommentsCache.getInstance().getByMultipleIds(this.getItemIds());
	}

	public CommentsPageSet getPageSet() {
		return (CommentsPageSet)super.getPageSet();
	}

	public String[] getCacheKeys() {
		String cacheKey = CommentsPageSet.getPageCacheKey(
				this.getPageSet().getTorrentId(),
				this.getNumber()
		);
		return new String[] { cacheKey };
	}
}
