package tracker.torrents;

import library.JsonResponse;

import java.util.List;

public class TorrentContentsResponse extends JsonResponse {
	public List<Entry> files = null;

	public void setFiles(List<Entry> files) {
		this.files = files;
	}

	public static class Entry {
		public String path = null;
		public String size = null;

		public Entry(String path, String size) {
			this.path = path;
			this.size = size;
		}
	}
}
