package tracker.torrents.upload;

import library.JsonResponse;

public class TorrentApplyResponse extends JsonResponse {
	public Long torrentId = null;
	public String torrentTitle = null;

	public void setTorrentId(Long torrentId) {
		this.torrentId = torrentId;
	}

	public void setTorrentTitle(String torrentTitle) {
		this.torrentTitle = torrentTitle;
	}
}