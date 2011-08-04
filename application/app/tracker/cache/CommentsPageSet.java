package tracker.cache;

import play.Logger;
import tracker.Config;
import tracker.DatabaseFactory;
import tracker.pagination.CommentsPage;
import tracker.pagination.CommentsPaginator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CommentsPageSet extends AbstractPageSet<CommentsPage> {
	protected Long torrentId;

	public CommentsPageSet() {
		this.itemsPerPage = Config.getInt("pagination.comments.perPage");
		this.firstPageUrl = Config.getString("pagination.comments.firstPageUrl");
		this.baseUrl = Config.getString("pagination.comments.baseUrl");
	}

	public CommentsPageSet(Long torrentId) {
		this();
		this.torrentId = torrentId;
	}

	public CommentsPage getByNumber(Integer pageNumber) throws Throwable {
		CommentsPage value;
		if (this.isEnabled()) {
			String cacheKey = getPageCacheKey(this.getTorrentId(), pageNumber);
			try {
				value = (CommentsPage)this.getCacheAccess().get(cacheKey);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				Logger.debug("Requesting comments page=" + pageNumber + ", torrentId=" + this.getTorrentId());
				value = this.requestPage(pageNumber);

				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = this.requestPage(pageNumber);
		}
		return value;
	}

	protected CommentsPage requestPage(Integer pageNumber) throws Throwable {
		CommentsPage commentsPage = new CommentsPage(this);
		commentsPage.setNumber(pageNumber);

		String query = "SELECT `id` FROM `torrent_comments` WHERE `torrent_id` = ? AND `visible` > 0 ORDER BY `post_date` ASC LIMIT ?,?";
		PreparedStatement statement = DatabaseFactory.getInstance().getConnection().prepareStatement(query);

		statement.setLong(1, this.getTorrentId());
		statement.setLong(2, this.getPageOffset(pageNumber));
		statement.setInt(3, this.getItemsPerPage());

		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			commentsPage.getItemIds().add(resultSet.getLong("id"));
		}

		DatabaseFactory.close(statement.getConnection());

		return commentsPage;
	}

	protected int requestItemsCount() throws Throwable {
		Logger.debug("Requesting items count for " + this);

		String query = "SELECT COUNT(*) FROM `torrent_comments` WHERE `torrent_id` = ? AND `visible` > 0";
		PreparedStatement statement = DatabaseFactory.getInstance().getConnection().prepareStatement(query);

		statement.setLong(1, this.getTorrentId());

		ResultSet resultSet = statement.executeQuery();
		resultSet.next();

		int itemsCount = resultSet.getInt(1);

		DatabaseFactory.close(statement.getConnection());

		return itemsCount;
	}

	public Long getTorrentId() {
		return this.torrentId;
	}

	public String toString() {
		return CommentsPaginator.getPageSetCacheKey(this.getTorrentId());
	}

	public static String getPageCacheKey(Long torrentId, Integer pageNumber) {
		return String.format("%s-%s-%s",
			"comments",
			(torrentId == null) ? "" : torrentId,
			(pageNumber == null) ? "" : pageNumber
		);
	}
}
