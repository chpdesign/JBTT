package tracker.torrents;

import play.Logger;
import tracker.DatabaseFactory;
import tracker.accounts.Account;
import tracker.cache.AccountsCache;
import tracker.cache.ICache;
import tracker.util.DateUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Comment implements Serializable, ICache {
	private Long id;
	private Long torrentId;
	private Long accountId;
	private String content;
	private Boolean visible;
	private Timestamp postDate;

	public Comment() { }

	public Long getId() { return this.id; }
	public void setId(Long id) { this.id = id; }

	public Long getTorrentId() { return this.torrentId; }
	public void setTorrentId(Long torrentId) { this.torrentId = torrentId; }

	public Long getAccountId() { return this.accountId; }
	public void setAccountId(Long accountId) { this.accountId = accountId; }

	public Account getAccount() throws Throwable { return AccountsCache.getInstance().getById(this.getAccountId()); }

	public String getContent() { return this.content; }
	public void setContent(String content) { this.content = content; }

	public Boolean getVisible() { return this.visible; }
	public void setVisible(Boolean visible) { this.visible = visible; }

	public Timestamp getPostDate() { return this.postDate; }
	public void setPostDate(Timestamp postDate) { this.postDate = postDate; }

	public String getPostDateString() {
		if (this.getPostDate() == null) {
			return null;
		}
		return DateUtils.getDate(this.getPostDate());
	}

	public String[] getCacheKeys() { return new String[] { "id-" + this.getId().toString() }; }

	public static Comment byId(Long id) throws Throwable {
		if (id < 1) {
			return null;
		}

		String condition = String.format("`id` = %d", id);
		return byCondition(condition);
	}

	public static List<Comment> byMultipleIds(List<Long> ids) throws Throwable {
		if (ids.size() < 1) {
			return new ArrayList<Comment>();
		}

		String condition = String.format("`id` IN %s", Arrays.toString(ids.toArray()).replace("[", "(").replace("]", ")"));

		return byConditionMultiple(condition);
	}

	public static Comment byCondition(String condition) throws Throwable {
		List<Comment> comments = byConditionMultiple(condition);
		if (comments.size() < 1) {
			return null;
		}

		return comments.get(0);
	}

	public static List<Comment> byConditionMultiple(String condition) throws Throwable {
		Logger.debug("Load comments by condition " + condition);

		List<Comment> comments = new ArrayList<Comment>();

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			String query = "SELECT `id`, `torrent_id`, `account_id`, `content`, `visible`, `post_date` FROM `torrent_comments` WHERE " + condition;

			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement(query);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Comment comment = new Comment();
				comment.setId(resultSet.getLong("id"));
				comment.setTorrentId(resultSet.getLong("torrent_id"));
				comment.setAccountId(resultSet.getLong("account_id"));
				comment.setContent(resultSet.getString("content"));
				comment.setVisible(resultSet.getBoolean("visible"));
				comment.setPostDate(resultSet.getTimestamp("post_date"));

				comments.add(comment);
			}
		} finally {
			DatabaseFactory.close(connection, statement);
		}

		return comments;
	}
}
