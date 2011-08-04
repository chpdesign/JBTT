package tracker.torrents.upload;

import tracker.DatabaseFactory;
import tracker.pagination.TorrentsPaginator;

import java.sql.*;

public class TorrentUpload {
	protected Long id;
	protected Long accountId;
	protected Timestamp uploadDate;
	protected byte[] infoHash;

	public void save() throws Throwable {
		Connection connection = null;

		try {
			connection = DatabaseFactory.getInstance().getConnection();

			String query =
				"INSERT INTO `torrent_uploads` (`id`, `account_id`, `upload_date`, `info_hash`) " +
				"VALUES (?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
					"`account_id` = VALUES(`account_id`)," +
					"`upload_date` = VALUES(`upload_date`)," +
					"`info_hash` = VALUES(`info_hash`)";

			PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, (this.getId() != null) ? this.getId() : 0);
			statement.setLong(2, this.getAccountId());
			statement.setTimestamp(3, this.getUploadDate());
			statement.setBytes(4, this.getInfoHash());

			if (this.getId() != null) {
				statement.setLong(14, this.getId());
			}
			statement.execute();

			if (this.getId() == null) {
				ResultSet keys = statement.getGeneratedKeys();
				if (keys.next()) {
					this.setId(keys.getLong(1));
					TorrentsPaginator.reset();
				}
			}
		} finally {
			DatabaseFactory.close(connection);
		}
	}

	public void delete() throws Throwable { // TODO: fixme
		Connection connection = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();
			String query = "DELETE FROM `torrent_uploads` WHERE `id` = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, this.getId());
			statement.execute();

			this.setId(null);
		} finally {
			DatabaseFactory.close(connection);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Timestamp getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Timestamp uploadDate) {
		this.uploadDate = uploadDate;
	}

	public byte[] getInfoHash() {
		return infoHash;
	}

	public void setInfoHash(byte[] infoHash) {
		this.infoHash = infoHash;
	}

	public static TorrentUpload byId(Long id) throws Throwable {
		TorrentUpload torrentUpload = null;
		Connection connection = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();

			String query = "SELECT `id`, `account_id`, `upload_date`, `info_hash` FROM `torrent_uploads` WHERE `id` = ?";

			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, id);

			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				return null;
			}

			torrentUpload = new TorrentUpload();
			torrentUpload.setId(resultSet.getLong("id"));
			torrentUpload.setAccountId(resultSet.getLong("account_id"));
			torrentUpload.setUploadDate(resultSet.getTimestamp("upload_date"));
			torrentUpload.setInfoHash(resultSet.getBytes("info_hash"));
		} finally {
			DatabaseFactory.close(connection);
		}

		return torrentUpload;
	}
}
