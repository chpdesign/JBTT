package tracker.torrents;

import play.Logger;
import tracker.DatabaseFactory;
import tracker.cache.ICache;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class TorrentStats implements Serializable, ICache {
	protected Long torrentId = null;

	protected Integer seeds = 0;
	protected Integer peers = 0;
	protected Integer completed = 0;

	protected TorrentStats(Long torrentId) {
		this.torrentId = torrentId;
	}

	public Long getTorrentId() { return torrentId; }
	public void setTorrentId(Long torrentId) { this.torrentId = torrentId; }

	public Integer getSeeds() { return seeds; }
	public void setSeeds(Integer seeds) { this.seeds = (seeds == null) ? 0 : seeds; }

	public Integer getPeers() { return peers; }
	public void setPeers(Integer peers) { this.peers = (peers == null) ? 0 : peers; }

	public Integer getCompleted() { return completed; }
	public void setCompleted(Integer completed) { this.completed = (completed == null) ? 0 : completed; }

	public String[] getCacheKeys() {
		return new String[] { "torrentId-" + this.getTorrentId().toString() };
	}

	public String toString() {
		return String.format("%d (S: %d, P: %d, C: %d)",
				this.getTorrentId(), this.getSeeds(), this.getPeers(), this.getCompleted());
	}

	public static TorrentStats byTorrentId(Long torrentId) throws Throwable {
		List<Long> torrentIds = new ArrayList<Long>();
		torrentIds.add(torrentId);

		List<TorrentStats> torrentStatsList = byMultipleTorrentIds(torrentIds);

		if (torrentStatsList.size() == 0) {
			return null;
		}

		return torrentStatsList.get(0);
	}

	public static List<TorrentStats> byMultipleTorrentIds(List<Long> torrentIds) throws Throwable {
		if (torrentIds.size() == 0) {
			return new ArrayList<TorrentStats>();
		}

		String condition = String.format("IN %s", Arrays.toString(torrentIds.toArray()).replace("[", "(").replace("]", ")"));
		Logger.debug("Load torrent stats by `torrent_id` " + condition);

		Map<Long, TorrentStats> torrentStatsList = new HashMap<Long, TorrentStats>();
		for (Long torrentId : torrentIds) {
			torrentStatsList.put(torrentId, new TorrentStats(torrentId));
		}

		Connection connection = null;
		PreparedStatement seedStatement = null;
		PreparedStatement peerStatement = null;
		PreparedStatement completedStatement = null;

		try {
			connection = DatabaseFactory.getInstance().getConnection();

			// Seeds
			String seedQuery = "SELECT `torrent_id`, COUNT(*) AS `seeds_count` FROM `torrent_peers` WHERE `left` = 0 " +
					"AND `torrent_id` " + condition + " GROUP BY `torrent_id`";
			seedStatement = connection.prepareStatement(seedQuery);
			ResultSet seedResultSet = seedStatement.executeQuery();
			while (seedResultSet.next()) {
				Long torrentId = seedResultSet.getLong("torrent_id");
				torrentStatsList.get(torrentId).setSeeds(seedResultSet.getInt("seeds_count"));
			}

			// Peers
			String peerQuery = "SELECT `torrent_id`, COUNT(*) AS `peers_count` FROM `torrent_peers` WHERE `left` > 0 " +
					"AND `torrent_id` " + condition + " GROUP BY `torrent_id`";
			peerStatement = connection.prepareStatement(peerQuery);
			ResultSet peerResultSet = peerStatement.executeQuery();
			while (peerResultSet.next()) {
				Long torrentId = peerResultSet.getLong("torrent_id");
				torrentStatsList.get(torrentId).setPeers(peerResultSet.getInt("peers_count"));
			}

			// Completed
			String completedQuery = "SELECT `torrent_id`, COUNT(*) AS `completed_count` FROM `account_torrents` WHERE `left` = 0 " +
					"AND `torrent_id` " + condition + " GROUP BY `torrent_id`";
			completedStatement = connection.prepareStatement(completedQuery);
			ResultSet completedResultSet = completedStatement.executeQuery();
			while (completedResultSet.next()) {
				Long torrentId = completedResultSet.getLong("torrent_id");
				torrentStatsList.get(torrentId).setCompleted(completedResultSet.getInt("completed_count"));
			}
		} finally {
			DatabaseFactory.close(seedStatement);
			DatabaseFactory.close(peerStatement);
			DatabaseFactory.close(completedStatement);
			DatabaseFactory.close(connection);
		}

		return new ArrayList<TorrentStats>(torrentStatsList.values());
	}
}
