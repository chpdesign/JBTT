package tracker.accounts;

import tracker.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AccountTorrents {
	protected Long accountId;

	public AccountTorrents(long accountId) {
		this.accountId = accountId;
	}

	public void updateStatistics(long torrentId, long sessionUploaded, long sessionDownloaded, long left) throws SQLException {
		String query = "INSERT INTO `account_torrents`(`account_id`, `torrent_id`, `uploaded`, `downloaded`, `left`, `last_update`) " +
				"VALUES (?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"`uploaded` = `uploaded` + VALUES(`uploaded`)," +
				"`downloaded` = `downloaded` + VALUES(`downloaded`)," +
				"`left` = VALUES(`left`)," +
				"`last_update` = VALUES(`last_update`)";

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement(query);
			statement.setLong(1, this.getAccountId());
			statement.setLong(2, torrentId);
			statement.setLong(3, sessionUploaded);
			statement.setLong(4, sessionDownloaded);
			statement.setLong(5, left);
			statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			statement.execute();
		} finally {
			DatabaseFactory.close(connection, statement);
		}
	}

	public Long getAccountId() {
		return this.accountId;
	}
}
