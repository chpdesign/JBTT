package tracker.accounts;

import play.Logger;
import tracker.DatabaseFactory;
import tracker.cache.ICache;
import tracker.util.FileUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountStats implements Serializable, ICache {
	protected Long accountId;

	protected Long uploaded;
	protected Long downloaded;
	protected Integer torrentsCount;

	public Long getAccountId() { return this.accountId; }
	public void setAccountId(Long accountId) { this.accountId = accountId; }

	public Long getUploaded() { return uploaded; }
	public void setUploaded(Long uploaded) { this.uploaded = uploaded; }

	public Long getDownloaded() { return downloaded; }
	public void setDownloaded(Long downloaded) { this.downloaded = downloaded; }

	public Integer getTorrentsCount() { return torrentsCount; }
	public void setTorrentsCount(Integer torrentsCount) { this.torrentsCount = torrentsCount; }

	public String getUploadedStr() { return FileUtils.getSizeString(this.getUploaded()); }
	public String getDownloadedStr() { return FileUtils.getSizeString(this.getDownloaded()); }

	public String getUploadRatio() {
		Float ratio = 0f;

		if (this.getUploaded() != 0) {
			ratio = this.getUploaded().floatValue() / this.getDownloaded().floatValue();
		}

		return String.format("%.2f", ratio);
	}

	public String[] getCacheKeys() { return new String[] { "accountId-" + this.getAccountId() }; }

	public static AccountStats byAccountId(Long accountId) throws Throwable {
		Logger.debug("Load account stats by ID = " + accountId);
		String query = "SELECT " +
				"SUM(`uploaded`) AS `totalUploaded`, SUM(`downloaded`) AS `totalDownloaded`, COUNT(*) AS `torrentsCount` " +
				"FROM `account_torrents` " +
				"WHERE `account_id` = ?";

		AccountStats accountStats = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement(query);

			statement.setLong(1, accountId);

			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				return null;
			}

			accountStats = new AccountStats();
			accountStats.setAccountId(accountId);
			accountStats.setUploaded(resultSet.getLong("totalUploaded"));
			accountStats.setDownloaded(resultSet.getLong("totalDownloaded"));
			accountStats.setTorrentsCount(resultSet.getInt("torrentsCount"));

			statement.close();
		} finally {
			DatabaseFactory.close(connection, statement);
		}

		return accountStats;
	}
}
