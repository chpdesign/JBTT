package controllers.tracker;

import play.mvc.Controller;
import tracker.announcer.Response;
import tracker.announcer.ScrapeRequest;
import tracker.torrents.Torrent;

import java.util.List;
import java.util.TreeMap;

public class Scrape extends Controller {
	public static void index() throws Throwable {
		ScrapeRequest scrapeRequest = new ScrapeRequest(params);

		if (scrapeRequest.getInfoHashes().size() < 1) {
			Response.error("Invalid info_hash");
		}

		TreeMap<byte[], Object> files = new TreeMap<byte[], Object>();
		List<Torrent> torrents = Torrent.byInfoHashMultiple(scrapeRequest.getInfoHashes());
		for (Torrent torrent : torrents) {
			TreeMap<String, Object> torrentStats = new TreeMap<String, Object>();
			torrentStats.put("complete", 0);
			torrentStats.put("downloaded", 0);
			torrentStats.put("incomplete", 0);
			// torrentStats.put("name", "");

			files.put(torrent.getInfoHash(), torrentStats);
		}

		TreeMap<String, Object> flags = new TreeMap<String, Object>();
		flags.put("min_request_interval", 5);

		TreeMap<String, Object> response = new TreeMap<String, Object>();
		response.put("files", files);
		response.put("flags", flags);

		Response.send(response);
	}
}
