package tracker.torrents;

import play.Logger;
import tracker.Config;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Images {
	private Long torrentId = null;

	public Images(Long torrentId) {
		this.torrentId = torrentId;
	}

	public Long getTorrentId() {
		return this.torrentId;
	}

	public String getDirectoryPath() {
		return String.format("%s/%d", Config.getString("uploads.paths.screenshot"), this.getTorrentId());
	}

	public String getDirectoryUrl() {
		return String.format("%s/%d", Config.getString("uploads.urls.screenshot"), this.getTorrentId());
	}

	public String getPosterUrl() {
		File directory = new File(this.getDirectoryPath());
		if (!directory.exists()) {
			return null; // TODO: default image?
		}

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				Pattern pattern = Pattern.compile("poster\\.(jpg|png)"); // TODO: вынести доступные расширения в конфиг или вообще не проверять?
				Matcher matcher = pattern.matcher(name);
				return matcher.matches();
			}
		};

		String[] list = directory.list(filter);
		if (list.length < 1) {
			Logger.warn("Poster image for torrent " + this.getTorrentId() + " doesn't exists!");
			return null;
		}

		if (list.length > 1) {
			Logger.warn("Multiple poster images for torrent " + this.getTorrentId());
		}

		return String.format("%s/%s", this.getDirectoryUrl(), list[0]);
	}

	public List<String> getImageUrls() {
		File directory = new File(this.getDirectoryPath());
		if (!directory.exists()) {
			return null; // TODO: default image?
		}

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				Pattern pattern = Pattern.compile("image-[0-9]+\\.(jpg|png)"); // TODO: вынести доступные расширения в конфиг или вообще не проверять?
				Matcher matcher = pattern.matcher(name);
				return matcher.matches();
			}
		};

		String[] list = directory.list(filter);

		List<String> urls = new ArrayList<String>();
		for (String listItem : list) {
			String url = String.format("%s/%s", this.getDirectoryUrl(), listItem);
			urls.add(url);
		}

		return urls;
	}
}
