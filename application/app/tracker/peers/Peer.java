package tracker.peers;

import play.mvc.Http;
import tracker.DatabaseFactory;
import tracker.accounts.Account;
import tracker.announcer.AnnounceRequest;
import tracker.torrents.Torrent;
import tracker.util.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Peer {
	public static final String PEERS_TABLE_FIELDS = "`account_id`, `torrent_id`, `peer_id`, `ip`, `port`, " +
			"`user_agent`, `last_update`, `uploaded`, `downloaded`, `left`";

	private Long accountId;
	private Long torrentId;
	private byte[] peerId;
	private Long ipValue;
	private Integer port;
	private String userAgent;
	private Timestamp lastUpdate;
	private Long uploaded;
	private Long downloaded;
	private Long left;

	public void save() throws Throwable {
		String query = "REPLACE INTO `torrent_peers`" +
				"(" + PEERS_TABLE_FIELDS + ") " +
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement(query);

			statement.setLong(1, this.getAccountId());
			statement.setLong(2, this.getTorrentId());
			statement.setBytes(3, this.getPeerId());
			statement.setLong(4, this.getIpValue());
			statement.setInt(5, this.getPort());
			statement.setString(6, this.getUserAgent());
			statement.setTimestamp(7, this.getLastUpdate());
			statement.setLong(8, this.getUploaded());
			statement.setLong(9, this.getDownloaded());
			statement.setLong(10, this.getLeft());

			statement.execute();
		} finally {
			DatabaseFactory.close(connection, statement);
		}
	}

	public Long getAccountId() {
		return this.accountId;
	}

	public Peer setAccountId(Long accountId) {
		this.accountId = accountId;
		return this;
	}

	public Long getTorrentId() {
		return this.torrentId;
	}

	public Peer setTorrentId(Long torrentId) {
		this.torrentId = torrentId;
		return this;
	}

	public byte[] getPeerId() {
		return this.peerId;
	}

	public Peer setPeerId(byte[] peerId) {
		this.peerId = peerId;
		return this;
	}

	public Long getIpValue() {
		return this.ipValue;
	}

	public Peer setIpValue(Long ipValue) {
		this.ipValue = ipValue;
		return this;
	}

	public String getIp() {
		return Utils.longToIp(this.getIpValue());
	}

	public Peer setIp(String ip) {
		this.ipValue = Utils.ipToLong(ip);
		return this;
	}

	public Integer getPort() {
		return this.port;
	}

	public Peer setPort(Integer port) {
		this.port = port;
		return this;
	}

	public String getUserAgent() {
		return this.userAgent;
	}

	public Peer setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public Timestamp getLastUpdate() {
		return this.lastUpdate;
	}

	public Peer setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}

	public Long getUploaded() {
		return this.uploaded;
	}

	public Peer setUploaded(Long uploaded) {
		this.uploaded = uploaded;
		return this;
	}

	public Long getDownloaded() {
		return this.downloaded;
	}

	public Peer setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
		return this;
	}

	public Long getLeft() {
		return this.left;
	}

	public Peer setLeft(Long left) {
		this.left = left;
		return this;
	}

	public void remove() throws Throwable {
		removePeer(this.getAccountId(), this.getTorrentId(), this.getIpValue(), this.getPort());
	}

	public static void removePeer(long accountId, long torrentId, long ipValue, int port) throws Throwable {
		String query = "DELETE FROM `torrent_peers` WHERE " +
				"`account_id` = ? AND `torrent_id` = ? AND `ip` = ? AND `port` = ?";

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement(query);
			statement.setLong(1, accountId);
			statement.setLong(2, torrentId);
			statement.setLong(3, ipValue);
			statement.setInt(4, port);
			statement.execute();
		} finally {
			DatabaseFactory.close(connection, statement);
		}
	}

	public String toString() {
		return "Account ID: " + this.getAccountId() + ", Torrent ID: " + this.getTorrentId() +
				" [" + this.getIp() + ":" + this.getPort() + "]";
	}

	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (object.getClass() != this.getClass())
			return false;

		Peer account = ((Peer)object);
		return this.getAccountId().equals(account.getAccountId()) && this.getTorrentId().equals(account.getTorrentId());
	}

	public static List<Peer> getTorrentPeers(Torrent torrent, Integer numWant) throws Throwable {
		return Peer.getTorrentPeers(torrent.getId(), numWant);
	}

	public static List<Peer> getTorrentPeers(Long torrentId, Integer numWant) throws Throwable {
		List<Peer> peers = new ArrayList<Peer>();
		String query = "SELECT " + PEERS_TABLE_FIELDS + " FROM `torrent_peers` WHERE `torrent_id` = ? ORDER BY rand() LIMIT ?";

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement(query);
			statement.setLong(1, torrentId);
			statement.setInt(2, numWant);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Peer peer = new Peer();
				peer.setAccountId(resultSet.getLong("account_id"));
				peer.setTorrentId(resultSet.getLong("torrent_id"));
				peer.setPeerId(resultSet.getBytes("peer_id")); // NOTICE: Поле peer_id должно быть ровно 20 байт. Если больше, то будут проблемы при сравнении.
				peer.setIpValue(resultSet.getLong("ip"));
				peer.setPort(resultSet.getInt("port"));
				peer.setUserAgent(resultSet.getString("user_agent"));
				peer.setLastUpdate(resultSet.getTimestamp("last_update"));
				peer.setUploaded(resultSet.getLong("uploaded"));
				peer.setDownloaded(resultSet.getLong("downloaded"));
				peer.setLeft(resultSet.getLong("left"));

				peers.add(peer);
			}
		} finally {
			DatabaseFactory.close(connection, statement);
		}

		return peers;
	}

	public static Peer getCurrentPeer(Account account, Torrent torrent, AnnounceRequest announceRequest) throws Throwable {
		String query = "SELECT " + PEERS_TABLE_FIELDS + " FROM `torrent_peers` WHERE " +
				"`account_id` = ? AND `torrent_id` = ? AND `ip` = ? AND `port` = ?";

		Peer peer = null;

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement(query);
			statement.setLong(1, account.getId());
			statement.setLong(2, torrent.getId());
			statement.setLong(3, announceRequest.getIpValue());
			statement.setInt(4, announceRequest.getPort());

			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				return null;
			}

			peer = new Peer();
			peer.setAccountId(resultSet.getLong("account_id"));
			peer.setTorrentId(resultSet.getLong("torrent_id"));
			peer.setPeerId(resultSet.getBytes("peer_id"));
			peer.setIpValue(resultSet.getLong("ip"));
			peer.setPort(resultSet.getInt("port"));
			peer.setUserAgent(resultSet.getString("user_agent"));
			peer.setLastUpdate(resultSet.getTimestamp("last_update"));
			peer.setDownloaded(resultSet.getLong("downloaded"));
			peer.setUploaded(resultSet.getLong("uploaded"));
			peer.setLeft(resultSet.getLong("left"));
		} finally {
			DatabaseFactory.close(connection, statement);
		}

		return peer;
	}

	public static void saveCurrentPeer(Account account, Torrent torrent, AnnounceRequest announceRequest) throws Throwable {
		String userAgent = null;
		if (Http.Request.current().headers.containsKey("user-agent")) {
			userAgent = Http.Request.current().headers.get("user-agent").value();
		}

		Peer peer = new Peer();
		peer.setAccountId(account.getId());
		peer.setTorrentId(torrent.getId());
		peer.setPeerId(announceRequest.getPeerId());
		peer.setIp(announceRequest.getIp());
		peer.setPort(announceRequest.getPort());
		peer.setUserAgent(userAgent);
		peer.setLastUpdate(Utils.getCurrentTimestamp());
		peer.setUploaded(announceRequest.getUploaded());
		peer.setDownloaded(announceRequest.getDownloaded());
		peer.setLeft(announceRequest.getLeft());

		PeersManager.getInstance().add(peer);
	}
}
