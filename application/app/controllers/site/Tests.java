package controllers.site;

import controllers.CommonController;
import play.Logger;
import tracker.cache.TorrentStatsCache;
import tracker.torrents.TorrentStats;

import java.util.ArrayList;
import java.util.List;

public class Tests extends CommonController {
	public static void index() throws Throwable {
		List<Long> torrentIds = new ArrayList<Long>();
		torrentIds.add(24L);
		torrentIds.add(25L);
//		List<TorrentStats> torrentStatsList = TorrentStats.byMultipleTorrentIds(torrentIds);
		List<TorrentStats> torrentStatsList = TorrentStatsCache.getInstance().getByMultipleIds(torrentIds);

		Logger.debug("*****************************************************");
		for (TorrentStats torrentStats : torrentStatsList) {
			Logger.debug(torrentStats.toString());
		}
		Logger.debug("*****************************************************");

//		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
//		map.put(10, "First");
//		map.put(2, null);
//		map.put(300, "Third");
//
//		map.put(2, "Second");
//
//		Logger.debug("*****************************************************");
//		for (Map.Entry<Integer, String> entry : map.entrySet()) {
//			Logger.debug(entry.getKey() + ":" + entry.getValue());
//		}
//		Logger.debug("*****************************************************");
//		for (String value : map.values()) {
//			Logger.debug(value);
//		}
//		Logger.debug("*****************************************************");

		renderText("tests");
	}
}
