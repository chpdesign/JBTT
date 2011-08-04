package tracker.torrents.upload;

import library.JsonResponse;

public class TorrentUploadResponse extends JsonResponse {
	public Long uploadId = null;
	public String infoHash = null;

	public void setUploadId(Long uploadId) {
		this.uploadId = uploadId;
	}

	public void setInfoHash(String infoHash) {
		this.infoHash = infoHash;
	}
}