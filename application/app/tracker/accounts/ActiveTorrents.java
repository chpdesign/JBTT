package tracker.accounts;

import play.Logger;
import tracker.DatabaseFactory;
import tracker.cache.ICache;
import tracker.torrents.TorrentData;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ActiveTorrents implements Serializable, ICache {
	protected Long accountId = null;
	protected List<TorrentData> torrents = new ArrayList<TorrentData>();

	public Long getAccountId() { return this.accountId; }
	public void setAccountId(Long accountId) { this.accountId = accountId; }

	public List<TorrentData> getTorrents() { return this.torrents; }

	public boolean equals(ActiveTorrents activeTorrents) {
		return this.getAccountId().equals(activeTorrents.getAccountId());
	}

	public String[] getCacheKeys() { return new String[] { "accountId-" + this.getAccountId().toString() }; }

	public static ActiveTorrents byAccountId(Long accountId) throws Throwable {
		Logger.debug("Load active torrents by account ID = " + accountId);

		ActiveTorrents activeTorrents = new ActiveTorrents();
		activeTorrents.setAccountId(accountId);

		Connection connection = null;

		PreparedStatement torrentPeersStatement = null;
		PreparedStatement accountTorrentsStatement = null;
		PreparedStatement torrentsStatement = null;

		try {
			connection = DatabaseFactory.getInstance().getConnection();

			String torrentPeersQuery = "SELECT `torrent_id`, `uploaded`, `downloaded`, `left`, `last_update` FROM `torrent_peers` " +
					"WHERE `account_id` = ? ORDER BY `last_update` DESC";
			torrentPeersStatement = connection.prepareStatement(torrentPeersQuery);
			torrentPeersStatement.setLong(1, accountId);
			ResultSet torrentPeersResultSet = torrentPeersStatement.executeQuery();
			while (torrentPeersResultSet.next()) {
				TorrentData torrent = new TorrentData();
				torrent.setAccountId(accountId);
				torrent.setTorrentId(torrentPeersResultSet.getLong("torrent_id"));
				torrent.setUploadedSession(torrentPeersResultSet.getLong("uploaded"));
				torrent.setDownloadedSession(torrentPeersResultSet.getLong("downloaded"));
				torrent.setLeftSession(torrentPeersResultSet.getLong("left"));
				torrent.setLastUpdate(torrentPeersResultSet.getTimestamp("last_update"));

				activeTorrents.getTorrents().add(torrent);
			}

			String torrentIds = "";
			for (TorrentData torrent : activeTorrents.getTorrents()) {
				torrentIds += "," + torrent.getTorrentId();
			}
			torrentIds = torrentIds.substring(1);

			String accountTorrentsQuery = "SELECT `torrent_id`, `uploaded`, `downloaded`, `left` FROM `account_torrents` WHERE `torrent_id` IN(" + torrentIds + ")";
			accountTorrentsStatement = connection.prepareStatement(accountTorrentsQuery);
			ResultSet accountTorrentsResultSet = accountTorrentsStatement.executeQuery();
			while (accountTorrentsResultSet.next()) {
				Long torrentId = accountTorrentsResultSet.getLong("torrent_id");
				for (TorrentData torrent : activeTorrents.getTorrents()) {
					if (torrent.getTorrentId().equals(torrentId)) {
						torrent.setUploaded(accountTorrentsResultSet.getLong("uploaded"));
						torrent.setDownloaded(accountTorrentsResultSet.getLong("downloaded"));
						torrent.setLeft(accountTorrentsResultSet.getLong("left"));
						break;
					}
				}
			}

			String torrentsQuery = "SELECT `id`, `title`, `creation_date`, `info_hash`, `torrent_size` FROM `torrents` WHERE `id` IN(" + torrentIds + ")";
			torrentsStatement = connection.prepareStatement(torrentsQuery);
			ResultSet torrentsResultSet = torrentsStatement.executeQuery();
			while (torrentsResultSet.next()) {
				Long torrentId = torrentsResultSet.getLong("id");
				for (TorrentData torrent : activeTorrents.getTorrents()) {
					if (torrent.getTorrentId().equals(torrentId)) {
						torrent.setTitle(torrentsResultSet.getString("title"));
						torrent.setCreationDate(torrentsResultSet.getTimestamp("creation_date"));
						torrent.setInfoHash(torrentsResultSet.getBytes("info_hash"));
						torrent.setSize(torrentsResultSet.getLong("torrent_size"));
						break;
					}
				}
			}
		} finally {
			DatabaseFactory.close(torrentPeersStatement);
			DatabaseFactory.close(accountTorrentsStatement);
			DatabaseFactory.close(torrentsStatement);
			DatabaseFactory.close(connection);
		}

		return activeTorrents;
	}
}
