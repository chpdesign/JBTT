package tracker.cache;

import play.Logger;
import tracker.Config;
import tracker.DatabaseFactory;
import tracker.pagination.TorrentsPage;
import tracker.pagination.TorrentsPaginator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TorrentsPageSet extends AbstractPageSet<TorrentsPage> {
	protected Integer categoryId;
	protected Integer tagId;

	public TorrentsPageSet() {
		this.itemsPerPage = Config.getInt("pagination.torrents.perPage");
		this.firstPageUrl = Config.getString("pagination.torrents.firstPageUrl");
		this.baseUrl = Config.getString("pagination.torrents.baseUrl");
	}

	public TorrentsPageSet(Integer categoryId, Integer tagId) {
		this();
		this.categoryId = categoryId;
		this.tagId = tagId;
	}

	public TorrentsPage getByNumber(Integer pageNumber) throws Throwable {
		TorrentsPage value;
		if (this.isEnabled()) {
			String cacheKey = getPageCacheKey(this.getCategoryId(), this.getTagId(), pageNumber);
			try {
				value = (TorrentsPage)this.getCacheAccess().get(cacheKey);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				Logger.debug("Requesting page=" + pageNumber + ", categoryId=" + this.getCategoryId() + ", tagId=" + this.getTagId());
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

	protected TorrentsPage requestPage(Integer pageNumber) throws Throwable {
		TorrentsPage torrentsPage = new TorrentsPage(this);
		torrentsPage.setNumber(pageNumber);

		String condition = "";
		if (this.getCategoryId() != null) {
			condition += " `category_id` = ?";
		}

		if (this.getTagId() != null) {
			condition += " `id` IN (SELECT `torrent_id` FROM `torrent_tags` WHERE `tag_id` = ?)";
		}

		if (condition.length() > 0) {
			condition = " WHERE" + condition;
		}

		String query = "SELECT `id` FROM `torrents`" + condition + " ORDER BY creation_date DESC, id DESC LIMIT ?,?";
		PreparedStatement statement = DatabaseFactory.getInstance().getConnection().prepareStatement(query);

		int parameterIndex = 0;
		if (this.getCategoryId() != null) {
			statement.setLong(++parameterIndex, this.getCategoryId());
		}
		if (this.getTagId() != null) {
			statement.setLong(++parameterIndex, this.getTagId());
		}
		statement.setLong(++parameterIndex, this.getPageOffset(pageNumber));
		statement.setInt(++parameterIndex, this.getItemsPerPage());

		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			torrentsPage.getItemIds().add(resultSet.getLong("id"));
		}

		DatabaseFactory.close(statement.getConnection());

		return torrentsPage;
	}

	protected int requestItemsCount() throws Throwable {
		Logger.debug("Requesting items count for " + this);

		String condition = "";
		if (this.getCategoryId() != null) {
			condition += " `category_id` = ?";
		}

		if (this.getTagId() != null) {
			condition += " `id` IN (SELECT `torrent_id` FROM `torrent_tags` WHERE `tag_id` = ?)";
		}

		if (condition.length() > 0) {
			condition = " WHERE" + condition;
		}

		String query = "SELECT COUNT(*) FROM `torrents`" + condition;
		PreparedStatement statement = DatabaseFactory.getInstance().getConnection().prepareStatement(query);

		int parameterIndex = 0;
		if (this.getCategoryId() != null) {
			statement.setLong(++parameterIndex, this.getCategoryId());
		}
		if (this.getTagId() != null) {
			statement.setLong(++parameterIndex, this.getTagId());
		}

		ResultSet resultSet = statement.executeQuery();
		resultSet.next();

		int itemsCount = resultSet.getInt(1);

		DatabaseFactory.close(statement.getConnection());

		return itemsCount;
	}

	public Integer getCategoryId() {
		return this.categoryId;
	}

	public Integer getTagId() {
		return this.tagId;
	}

	public String toString() {
		return TorrentsPaginator.getPageSetCacheKey(this.getCategoryId(), this.getTagId());
	}

	public static String getPageCacheKey(Integer categoryId, Integer tagId, Integer pageNumber) {
		return String.format("%s-%s-%s-%s",
			"torrents",
			(categoryId == null) ? "" : categoryId,
			(tagId == null) ? "" : tagId,
			(pageNumber == null) ? "" : pageNumber
		);
	}
}
