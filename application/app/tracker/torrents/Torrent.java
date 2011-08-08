package tracker.torrents;

import play.Logger;
import tracker.Config;
import tracker.DatabaseFactory;
import tracker.accounts.Account;
import tracker.cache.*;
import tracker.pagination.CommentsPage;
import tracker.pagination.CommentsPaginator;
import tracker.pagination.TorrentsPaginator;
import tracker.util.DateUtils;
import tracker.util.FileUtils;
import tracker.util.HtmlUtils;
import tracker.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Torrent implements Serializable, ICache {
	public static final String TORRENTS_TABLE_FIELDS = "`id`, `category_id`, `author_id`, `title`, `description`, `description_html`, `creation_date`, " +
		"`edit_date`, `info_hash`, `views`, `hits`, `visible`, `torrent_size`, `torrent_creation_date`, `torrent_created_by`";

	protected Long id = null;
	protected Long categoryId = 0l;
	protected Long authorId = 0l;
	protected String title = null;
	protected String description = null;
	protected String descriptionHtml = null;
	protected Timestamp creationDate = null;
	protected Timestamp editDate = null;
	protected byte[] infoHash = null;
	protected Integer views = 0;
	protected Integer hits = 0;
	protected Boolean visible = false;
	protected Long torrentSize = 0l;
	protected Timestamp torrentCreationDate = null;
	protected String torrentCreatedBy = null;

	protected List<Tag> tags = null;

	protected Images images = null;

//	protected volatile TorrentStatistics statistics = null;

	public void save() throws Throwable {
		this.save(false);
	}

	public void save(boolean overwrite) throws Throwable {
		Connection connection = null;

		try {
			connection = DatabaseFactory.getInstance().getConnection();
			connection.setAutoCommit(false);

			if (!overwrite && this.getId() != null) {
				String uniqueCheckQuery = "SELECT COUNT(*) as cnt FROM `torrents` WHERE info_hash = ? AND id != ?";
				PreparedStatement uniqueCheckStatement = connection.prepareStatement(uniqueCheckQuery);
				uniqueCheckStatement.setBytes(1, this.getInfoHash());
				uniqueCheckStatement.setLong(2, this.getId());
				ResultSet uniqueCheckResultSet = uniqueCheckStatement.executeQuery();

				if (!uniqueCheckResultSet.next()) {
					throw new Exception();
				}

				if (uniqueCheckResultSet.getLong("cnt") > 0) {
					throw new Exception("This info_hash is already exists.");
				}
			}

			String query =
				"INSERT INTO torrents (" + TORRENTS_TABLE_FIELDS + ") " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
					"`category_id` = VALUES(`category_id`)," +
					"`author_id` = VALUES(`author_id`)," +
					"`title` = VALUES(`title`)," +
					"`description` = VALUES(`description`)," +
					"`description_html` = VALUES(`description_html`)," +
					"`creation_date` = VALUES(`creation_date`)," +
					"`edit_date` = VALUES(`edit_date`)," +
					"`info_hash` = VALUES(`info_hash`)," +
					"`views` = VALUES(`views`)," +
					"`hits` = VALUES(`hits`)," +
					"`visible` = VALUES(`visible`)," +
					"`torrent_size` = VALUES(`torrent_size`)," +
					"`torrent_creation_date` = VALUES(`torrent_creation_date`)," +
					"`torrent_created_by` = VALUES(`torrent_created_by`)";

			PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, (this.getId() != null) ? this.getId() : 0);
			statement.setLong(2, this.getCategoryId());
			statement.setLong(3, this.getAuthorId());
			statement.setString(4, this.getTitle());
			statement.setString(5, this.getDescription());
			statement.setString(6, this.getDescriptionHtml());
			statement.setTimestamp(7, this.getCreationDate());
			statement.setTimestamp(8, this.getEditDate());
			statement.setBytes(9, this.getInfoHash());
			statement.setInt(10, this.getViews());
			statement.setInt(11, this.getHits());
			statement.setBoolean(12, this.getVisible());
			statement.setLong(13, this.getTorrentSize());
			statement.setTimestamp(14, this.getTorrentCreationDate());
			statement.setString(15, this.getTorrentCreatedBy());
			statement.execute();

			if (this.getId() == null) {
				ResultSet keys = statement.getGeneratedKeys();
				if (keys.next()) {
					this.setId(keys.getLong(1));
					TorrentsPaginator.reset();
				}
			}

	// TODO: fixme
	//		if (this.getTags() != null) {
	//			String tagDeleteQuery = "DELETE FROM `torrent_tags` WHERE `torrent_id` = ?";
	//			PreparedStatement tagDeleteStatement = connection.prepareStatement(tagDeleteQuery);
	//			tagDeleteStatement.setLong(1, this.getId());
	//			tagDeleteStatement.execute();
	//
	//			String tagQuery = "INSERT INTO `torrent_tags`(`torrent_id`, `tag_id`) VALUES(?, ?)";
	//			PreparedStatement tagStatement = connection.prepareStatement(tagQuery);
	//			tagStatement.setLong(1, this.getId());
	//			for (Tag tag : this.getTags()) {
	//				tagStatement.setInt(2, tag.getId());
	//				tagStatement.execute();
	//			}
	//		}
		} catch (Exception exception) {
			if (connection != null) {
				connection.rollback();
			}
			throw exception;
		} finally {
			connection.commit();
			DatabaseFactory.close(connection);
		}
		TorrentsCache.getInstance().put(this);
	}

	public synchronized void delete() throws Throwable {
		Connection connection = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();

			String query = "DELETE FROM `torrents` WHERE `id` = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, this.getId());
			statement.execute();
			this.setId(null);

//			String peersQuery = "DELETE FROM `torrrent_peers` WHERE `torrent_id` = ?";
//			PreparedStatement peersStatement = connection.prepareStatement(peersQuery);
//			peersStatement.setLong(1, this.getId());
//			peersStatement.execute();
		} finally {
			DatabaseFactory.close(connection);
		}

		TorrentsCache.getInstance().remove(this);
		TorrentsPaginator.reset();

		try {
			this.getTorrentFile().delete();
		} catch (FileNotFoundException ignored) { }
	}

	public Long getId() {
		return this.id;
	}

	public Torrent setId(Long id) {
		this.id = id;

		if (id != null && id > 0) {
			this.images = new Images(this.id);
		}

		return this;
	}

	public Long getCategoryId() {
		return this.categoryId;
	}

	public Torrent setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public Long getAuthorId() {
		return this.authorId;
	}

	public Torrent setAuthorId(Long authorId) {
		this.authorId = authorId;
		return this;
	}

	public Account getAuthor() throws Throwable {
		return AccountsCache.getInstance().getById(this.getAuthorId());
	}

	public String getTitle() {
		return this.title;
	}

	public Torrent setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

	public Torrent setDescription(String description) {
		return this.setDescription(description, true);
	}

	public synchronized Torrent setDescription(String description, boolean update) {
		if (update) {
			this.setDescriptionHtml(HtmlUtils.parseBB(description));
		}
		this.description = description;
		return this;
	}

	public String getDescriptionHtml() {
		return this.descriptionHtml;
	}

	protected Torrent setDescriptionHtml(String descriptionHtml) {
		this.descriptionHtml = descriptionHtml;
		return this;
	}

	public Timestamp getCreationDate() {
		return this.creationDate;
	}

	public String getCreationDateString() {
		if (this.getCreationDate() == null) {
			return null;
		}
		return DateUtils.getDate(this.getCreationDate());
	}

	public Torrent setCreationDate() {
		return this.setCreationDate(new Timestamp(System.currentTimeMillis()));
	}

	public Torrent setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public Timestamp getEditDate() {
		return this.editDate;
	}

	public Torrent setEditDate(Timestamp editDate) {
		this.editDate = editDate;
		return this;
	}

	public byte[] getInfoHash() {
		return this.infoHash;
	}

//	public Torrent setInfoHash() throws Throwable {
//		byte[] infoHash = null;
//
//		if (true) throw new Exception("Not implemented");
//
//		return this.setInfoHash(infoHash);
//	}

	public Torrent setInfoHash(byte[] infoHash) {
		this.infoHash = infoHash;
		return this;
	}

	public String getInfoHashHexString() {
		return Utils.getHexString(this.getInfoHash());
	}

	public Integer getViews() {
		return this.views;
	}

	public Torrent setViews(Integer views) {
		this.views = views;
		return this;
	}

	public Torrent addView() throws Throwable {
		this.views++;
		this.save();
		return this;
	}

	public Integer getHits() {
		return this.hits;
	}

	public Torrent setHits(Integer hits) {
		this.hits = hits;
		return this;
	}

	public Torrent addHit() throws Throwable {
		this.hits++;
		this.save();
		return this;
	}

	public Boolean getVisible() {
		return this.visible;
	}

	public Torrent setVisible(Boolean visible) {
		this.visible = visible;
		return this;
	}

	public Long getTorrentSize() {
		return this.torrentSize;
	}

	public String getTorrentSizeFormatted() {
		return FileUtils.getSizeString(this.getTorrentSize());
	}

	public void setTorrentSize(Long torrentSize) {
		this.torrentSize = torrentSize;
	}

	public Timestamp getTorrentCreationDate() {
		return this.torrentCreationDate;
	}

	public void setTorrentCreationDate(Timestamp torrentCreationDate) {
		this.torrentCreationDate = torrentCreationDate;
	}

	public String getTorrentCreatedBy() {
		return this.torrentCreatedBy;
	}

	public void setTorrentCreatedBy(String torrentCreatedBy) {
		this.torrentCreatedBy = torrentCreatedBy;
	}

	public List<Tag> getTags() {
		return this.tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getTagsString() {
		if (this.getTags() == null) {
			return "";
		}

		String tagsString = "";
		for (Tag tag : this.getTags()) {
			tagsString += "," + tag.getId();
		}
		return tagsString.substring(1);
	}

	public void setTagsString(String tagsString) throws Throwable {
		List<Tag> tags = new ArrayList<Tag>();
		for (String tagString : tagsString.split(",")) {
			if (tagsString.length() == 0) {
				continue;
			}
			tags.add(TagsList.getById(Integer.parseInt(tagString)));
		}
		this.setTags(tags);
	}

	public String[] getCacheKeys() {
		return new String[] {
			"id-" + this.getId().toString(),
			"infoHash-" + this.getInfoHashHexString(),
		};
	}

	public File getTorrentFile() throws FileNotFoundException {
		File torrentFile = new File(Config.getString("uploads.paths.torrent") + "/" + this.getInfoHashHexString());
		if (!torrentFile.exists()) {
			throw new FileNotFoundException();
		}
		return torrentFile;
	}

	// TODO: Перенести все, касающееся комментариев, в соответствующий класс.

	public CommentsPage getComments(Integer pageNumber) throws Throwable {
		return CommentsPaginator.get(this.getId(), pageNumber);
	}

	public int getCommentsCount() throws Throwable {
		return CommentsPaginator.getPageSet(this.getId()).getItemsCount();
	}

	public int getCommentsPages() throws Throwable {
		CommentsPageSet pageSet = CommentsPaginator.getPageSet(this.getId());

		int count = this.getCommentsCount() / pageSet.getItemsPerPage();
		if (this.getCommentsLastPageCount() > 0) {
			count++;
		}

		return count;
	}

	public int getCommentsLastPageCount() throws Throwable {
		return this.getCommentsCount() % CommentsPaginator.getPageSet(this.getId()).getItemsPerPage();
	}

	public int getCommentsNextPage() throws Throwable {
		int page = this.getCommentsPages();

		// Если добавление комментария вызовет создание новой страницы, то увеличиваем на единицу.
		if (this.getCommentsLastPageCount() >= CommentsPaginator.getPageSet(this.getId()).getItemsPerPage()) {
			page++;
		}

		return page;
	}

	public Images getImages() {
		return this.images;
	}

	public TorrentStats getStatistics() throws Throwable {
		return TorrentStatsCache.getInstance().getByTorrentId(this.getId());
	}

	public boolean equals(Torrent torrent) {
		return this.getId().equals(torrent.getId());
	}

	public static Torrent byId(Long id) throws Throwable {
		if (id < 1) {
			return null;
		}

		String condition = String.format("id = %d", id);
		return byCondition(condition);
	}

	public static List<Torrent> byMultipleIds(List<Long> ids) throws Throwable {
		if (ids.size() < 1) {
			return new ArrayList<Torrent>();
		}

		String condition = String.format("`id` IN %s", Arrays.toString(ids.toArray()).replace("[", "(").replace("]", ")"));

		return byConditionMultiple(condition);
	}

	public static Torrent byInfoHash(byte[] infoHash) throws Throwable {
		Connection connection = DatabaseFactory.getInstance().getConnection();

		String query = "SELECT " + TORRENTS_TABLE_FIELDS + " FROM `torrents` WHERE `info_hash` = ?";

		PreparedStatement statement = connection.prepareStatement(query);
		statement.setBytes(1, infoHash);
		ResultSet resultSet = statement.executeQuery();
		List<Torrent> torrents = getRequestResults(resultSet);
		DatabaseFactory.close(connection);

		if (torrents.size() < 1) {
			return null;
		}

		return torrents.get(0);
	}

	public static List<Torrent> byInfoHashMultiple(List<byte[]> infoHashes) throws Throwable {
		String infoHashesStr = "";
		for (byte[] infoHash : infoHashes) {
			infoHashesStr += String.format(",\"%s\"", Utils.getHexString(infoHash));
		}
		String condition = String.format("`info_hash` IN(%s)", infoHashesStr.substring(1));
		return byConditionMultiple(condition);
	}

	public static Torrent byCondition(String condition) throws Throwable {
		List<Torrent> torrents = byConditionMultiple(condition);
		if (torrents.size() < 1) {
			return null;
		}

		return torrents.get(0);
	}

	public static List<Torrent> byConditionMultiple(String condition) throws Throwable {
		Logger.debug("Load torrents by condition " + condition);

		Connection connection = DatabaseFactory.getInstance().getConnection();

		String query = "SELECT " + TORRENTS_TABLE_FIELDS + " FROM `torrents` WHERE " + condition;

		PreparedStatement statement = connection.prepareStatement(query);
		ResultSet resultSet = statement.executeQuery();
		List<Torrent> torrents = getRequestResults(resultSet);
		DatabaseFactory.close(connection);

		return torrents;
	}

	protected static List<Torrent> getRequestResults(ResultSet resultSet) throws Throwable {
		List<Torrent> torrents = new ArrayList<Torrent>();

		while (resultSet.next()) {
			Torrent torrent = new Torrent();
			torrent.setId(resultSet.getLong("id"));
			torrent.setCategoryId(resultSet.getLong("category_id"));
			torrent.setAuthorId(resultSet.getLong("author_id"));
			torrent.setTitle(resultSet.getString("title"));
			torrent.setDescription(resultSet.getString("description"), false);
			torrent.setDescriptionHtml(resultSet.getString("description_html"));
			torrent.setCreationDate(resultSet.getTimestamp("creation_date"));
			torrent.setEditDate(resultSet.getTimestamp("edit_date"));
			torrent.setInfoHash(resultSet.getBytes("info_hash"));
			torrent.setViews(resultSet.getInt("views"));
			torrent.setHits(resultSet.getInt("hits"));
			torrent.setVisible(resultSet.getBoolean("visible"));
			torrent.setTorrentSize(resultSet.getLong("torrent_size"));
			torrent.setTorrentCreatedBy(resultSet.getString("torrent_created_by"));
			torrent.setTorrentCreationDate(resultSet.getTimestamp("torrent_creation_date"));

// TODO: fixme
//			List<Tag> tags = new ArrayList<Tag>();
//			String tagsQuery = "SELECT `tag_id` FROM `torrent_tags` WHERE `torrent_id` = ?";
//			PreparedStatement tagsStatement = connection.prepareStatement(tagsQuery);
//			tagsStatement.setLong(1, torrent.getId());
//			ResultSet tagsResultSet = tagsStatement.executeQuery();
//			while (tagsResultSet.next()) {
//				tags.add(TagsList.getById(tagsResultSet.getInt("tag_id")));
//			}
//			torrent.setTags(tags);

			torrents.add(torrent);
		}

		return torrents;
	}
}
