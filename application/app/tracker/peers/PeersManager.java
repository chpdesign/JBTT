package tracker.peers;

import play.Logger;
import play.exceptions.UnexpectedException;
import tracker.Config;
import tracker.DatabaseFactory;
import tracker.util.ThreadPoolManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PeersManager {
	protected long maxEntries;

	protected final DelayCheckRunnable delayCheckRunnable;
	protected final CleanPeersRunnable cleanPeersRunnable;

	protected final List<Peer> peers = new ArrayList<Peer>();

	public void add(Peer peer) throws SQLException {
		this.peers.add(peer);

		if (this.peers.size() >= this.getMaxEntries()) {
			this.store(false);
		}
	}

	public synchronized void store(boolean byDelay) throws SQLException {
		// В случае, если за время ожидания в очереди уже сохранили данные, уходим.
		if (this.peers.size() == 0) {
			return;
		}
		if ((this.peers.size() < this.getMaxEntries()) && !byDelay) {
			return;
		}

		String query = "INSERT INTO `torrent_peers` (" + Peer.PEERS_TABLE_FIELDS + ") VALUES ";

		for (long row = 0; row < this.peers.size(); row++) {
			query += "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			if (row < this.peers.size() - 1) {
				query += ",";
			}
		}

		query += " ON DUPLICATE KEY UPDATE " +
			"`account_id` = VALUES(`account_id`)," +
			"`torrent_id` = VALUES(`torrent_id`)," +
			"`peer_id` = VALUES(`peer_id`)," +
			"`ip` = VALUES(`ip`)," +
			"`port` = VALUES(`port`)," +
			"`user_agent` = VALUES(`user_agent`)," +
			"`last_update` = VALUES(`last_update`)," +
			"`uploaded` = VALUES(`uploaded`)," +
			"`downloaded` = VALUES(`downloaded`)," +
			"`left` = VALUES(`left`)";



		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement(query);

			for (int row = 0; row < this.peers.size(); row++) {
				int indexOffset = row * 10;
				Peer peer = this.peers.get(row);

				statement.setLong(indexOffset + 1, peer.getAccountId());
				statement.setLong(indexOffset + 2, peer.getTorrentId());
				statement.setBytes(indexOffset + 3, peer.getPeerId());
				statement.setLong(indexOffset + 4, peer.getIpValue());
				statement.setInt(indexOffset + 5, peer.getPort());
				statement.setString(indexOffset + 6, peer.getUserAgent());
				statement.setTimestamp(indexOffset + 7, peer.getLastUpdate());
				statement.setLong(indexOffset + 8, peer.getUploaded());
				statement.setLong(indexOffset + 9, peer.getDownloaded());
				statement.setLong(indexOffset + 10, peer.getLeft());
			}

			statement.execute();
		} finally {
			DatabaseFactory.close(connection, statement);
		}

		this.peers.clear();
		this.delayCheckRunnable.resetLastStoreTime();

		Logger.debug("Peers successfully stored. Count: " + this.peers.size());
	}

	protected long getMaxEntries() {
		return this.maxEntries;
	}

	protected static volatile PeersManager instance;

	public static PeersManager getInstance() {
		if (instance == null) {
			synchronized (PeersManager.class) {
				if (instance == null) {
					instance = new PeersManager();
				}
			}
		}
		return instance;
	}

	protected PeersManager() {
		this.maxEntries = Config.getLong("tracker.peers.maxEntries");

		this.delayCheckRunnable = new DelayCheckRunnable();
		ThreadPoolManager.getInstance().schedule(this.delayCheckRunnable, 0);

		this.cleanPeersRunnable = new CleanPeersRunnable();
		ThreadPoolManager.getInstance().schedule(this.cleanPeersRunnable, 0);
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public class DelayCheckRunnable implements Runnable {
		protected final long maxDelay;
		protected volatile long lastStoreTime;

		public DelayCheckRunnable() {
			this.maxDelay = Config.getLong("tracker.peers.maxDelay") * 1000;
			this.lastStoreTime = System.currentTimeMillis();
		}

		public void run() {
			try {
				if (this.isTimeToStore()) {
					this.resetLastStoreTime();

					try {
						PeersManager.getInstance().store(true);
					} catch (SQLException sqlException) {
						Logger.error(sqlException, "");
						throw new UnexpectedException(sqlException);
					}
				}
			} finally {
				ThreadPoolManager.getInstance().schedule(this, this.getNextCheckTime());
			}
		}

		protected boolean isTimeToStore() {
			return System.currentTimeMillis() - this.lastStoreTime > this.maxDelay;
		}

		protected long getNextCheckTime() { // TODO: check me
			if (this.isTimeToStore()) {
				return 0;
			}
			return this.maxDelay - (System.currentTimeMillis() - this.lastStoreTime);
		}

		public void resetLastStoreTime() {
			this.lastStoreTime = System.currentTimeMillis();
		}
	}

	public class CleanPeersRunnable implements Runnable {
		protected final long cleanInterval;
		protected final long expireTime;

		public CleanPeersRunnable() {
			this.cleanInterval = Config.getLong("tracker.peers.cleanInterval") * 1000;
			this.expireTime = Config.getLong("tracker.peers.expireTime") * 1000;
		}

		public void run() { // TODO: Сейчас все устаревшие сессии с неотправленным stop не сохраняются в общей статистике. Переделать?
			Logger.debug("Cleaning expired peer data...");

			String query = "DELETE FROM `torrent_peers` WHERE `last_update` < ?";

			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = DatabaseFactory.getInstance().getConnection();
				statement = connection.prepareStatement(query);
				statement.setTimestamp(1, new Timestamp(System.currentTimeMillis() - this.expireTime));
				statement.execute();
			} catch (SQLException sqlException) {
				Logger.error(sqlException, "");
				throw new UnexpectedException(sqlException);
			} finally {
				DatabaseFactory.close(connection, statement);
				ThreadPoolManager.getInstance().schedule(this, this.cleanInterval);
			}
		}
	}
}
