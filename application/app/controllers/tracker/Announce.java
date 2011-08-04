package controllers.tracker;

import play.mvc.Controller;
import tracker.Config;
import tracker.accounts.Account;
import tracker.accounts.AccountTorrents;
import tracker.announcer.AnnounceRequest;
import tracker.announcer.Event;
import tracker.announcer.Response;
import tracker.cache.AccountsCache;
import tracker.cache.TorrentsCache;
import tracker.peers.Peer;
import tracker.torrents.Torrent;
import tracker.util.Numbers;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class Announce extends Controller {
	public static void index() throws Throwable {
		AnnounceRequest announceRequest = new AnnounceRequest(params);

		String passkey = announceRequest.getPassKey();
		if (passkey == null || passkey.length() != 32) {
			Response.error("Invalid passkey");
			return;
		}

		Account account = AccountsCache.getInstance().getByPasskey(passkey);
		if (account == null) {
			Response.error("Wrong passkey " + announceRequest.getPassKey());
			return;
		}

		Torrent torrent = TorrentsCache.getInstance().getByInfoHash(announceRequest.getInfoHash());
		if (torrent == null) {
			Response.error("Wrong info_hash " + announceRequest.getInfoHashHexString());
			return;
		}

		// Сохраняем данные сессии в общую статистику и удаляем остановленную сессию из базы.
		if (announceRequest.getEvent().equals(Event.STOPPED)) {
			AccountTorrents accountTorrents = new AccountTorrents(account.getId());
			accountTorrents.updateStatistics(torrent.getId(), announceRequest.getUploaded(), announceRequest.getDownloaded(), announceRequest.getLeft());
			Peer.removePeer(account.getId(), torrent.getId(), announceRequest.getIpValue(), announceRequest.getPort());
			return;
		}

		Peer.saveCurrentPeer(account, torrent, announceRequest);

		TreeMap<String, Object> response = new TreeMap<String, Object>();
		List<Peer> torrentPeers = Peer.getTorrentPeers(torrent, announceRequest.getNumWant());

		if (announceRequest.isCompact()) {
			ByteArrayOutputStream peersBytes = new ByteArrayOutputStream();
			for (Peer torrentPeer : torrentPeers) {
				if (Arrays.equals(torrentPeer.getPeerId(), announceRequest.getPeerId())) {
					continue;
				}

//				byte[] tmp1 = Utils.longToNetworkBytes(torrentPeer.getIpValue());
//				byte[] tmp2 = Utils.longToNetworkBytes(torrentPeer.getPort());
//				byte[] ip = { tmp1[4], tmp1[5], tmp1[6], tmp1[7] };
//				byte[] port = { tmp2[6], tmp2[7] };

				byte[] ip = Numbers.longToNetworkIp(torrentPeer.getIpValue());
				byte[] port = Numbers.longToNetworkPort(torrentPeer.getPort());

				peersBytes.write(ip);
				peersBytes.write(port);
			}
			response.put("peers", peersBytes);
		} else {
			List<Map> peersList = new ArrayList<Map>();
			for (Peer torrentPeer : torrentPeers) {
				if (Arrays.equals(torrentPeer.getPeerId(), announceRequest.getPeerId())) {
					continue;
				}

				TreeMap<String, Object> peerMap = new TreeMap<String, Object>();
				peerMap.put("ip", torrentPeer.getIp());
				peerMap.put("port", torrentPeer.getPort());

				if (!announceRequest.isNoNeedPeerId()) {
					peerMap.put("peer id", torrentPeer.getPeerId());
				}

				peersList.add(peerMap);
			}
			response.put("peers", peersList);
		}

		// TODO: get from config && statistics
		response.put("interval", Config.get("tracker.announce.interval.common"));
		response.put("min_interval", Config.get("tracker.announce.interval.min"));
		response.put("complete", 0);
		response.put("incomplete", 0);
		response.put("downloaded", 0);

		Response.send(response);
	}
}
