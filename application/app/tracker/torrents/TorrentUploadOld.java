package tracker.torrents;

import play.Logger;
import tracker.Config;
import tracker.accounts.Account;
import tracker.bencode.Decoder;
import tracker.bencode.Encoder;
import tracker.cache.TorrentsCache;
import tracker.util.Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class TorrentUploadOld {
	public Account account;

	public String title;
	public String description;
	public File torrent;
	public File poster;
	public List<File> screenshots;

	public Torrent save() throws Throwable {
		return this.save(false);
	}

	@SuppressWarnings("unchecked")
	public Torrent save(Boolean overwrite) throws Throwable { // TODO: use TorrentMetaInfo class functionality
		// Read uploaded torrent file
		Map<String, Object> torrentMap;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(this.torrent));
			torrentMap = Decoder.get().decode(bufferedInputStream);
			if(torrentMap == null) {
				throw new IOException("Unable to decode torrent file.");
			}
		} finally {
			this.torrent.deleteOnExit();
//			if (!this.torrent.delete()) {
//				throw new IOException("Unable to delete temporary torrent file.");
//			}
			// TODO: fixme
		}

		// TODO: Validate uploaded torrent file
//		if (!torrentMap.containsKey("info_hash")) {
//			throw new Exception("info_hash");
//		}
//		byte[] infoHash = (byte[])torrentMap.get("info_hash");
//		if (infoHash.length != 20) {
//			throw new Exception("Incorrect info_hash value");
//		}

		if (!torrentMap.containsKey("info")) {
			throw new Exception("Info section is not defined.");
		}

		Map<String, Object> infoSection = (Map<String, Object>)torrentMap.get("info");

		byte[] infoHash = Utils.hashSha1(Encoder.get().encode(torrentMap.get("info")).toByteArray());
		Logger.debug("info_hash: " + Utils.getHexString(infoHash));

		Long totalSize = null;

		if (infoSection.containsKey("length")) {
			Logger.info("single-mode");
			totalSize = (Long)infoSection.get("length");
		} else if(infoSection.containsKey("files")) {
			totalSize = 0l;
			List<Map<String, Object>> files = (List<Map<String, Object>>)infoSection.get("files");
			for (Map<String, Object> file : files) {
//				String path = "";
//				ArrayList<Object> breadcrumbs = (ArrayList<Object>)file.get("path");
//				for (Object breadcrumb : breadcrumbs) {
//					path += "/" + new String((byte[])breadcrumb, Charset.forName(Config.getString("tracker.charset")));
//				}
//				Logger.info("file path: " + path);
//				Logger.info("file length: " + file.get("length"));

				totalSize += (Long)file.get("length");
			}
		} else {
			for (String key : infoSection.keySet()) {
				Logger.info("key: " + key);
			}
		}

		Logger.info("total torrent size: " + totalSize);


		// Save torrent in DB
		Torrent torrent = new Torrent();
		torrent.setAuthorId(1l); // TODO: get author id from session
		torrent.setTitle(this.title);
		torrent.setDescription(this.description);
		torrent.setEditDate(Utils.getCurrentTimestamp());
		torrent.setCreationDate(Utils.getCurrentTimestamp());
		torrent.setInfoHash(infoHash);
		torrent.setTorrentSize(totalSize);
		if (torrentMap.containsKey("created by")) {
			torrent.setTorrentCreatedBy(new String((byte[])torrentMap.get("created by"), Charset.forName(Config.getString("tracker.charset")))); // created_by
		}
		torrent.setTorrentCreationDate(null); // date
		torrent.save();

		try {
			// Save torrent file
			String uploadDirectory = Config.getString("uploads.paths.torrent");
			File torrentFile = new File(uploadDirectory, torrent.getInfoHashHexString());

			if (torrentFile.exists()) {
				if (!torrentFile.delete()) {
					Logger.error("Не удалось удалить старый файл " + torrentFile.getAbsolutePath());
				}
			}

			if (!torrentFile.createNewFile()) {
				throw new Exception("Не удалось создать новый файл торрента.");
			}

			ByteArrayOutputStream encodedTorrent = Encoder.get().encode(torrentMap);
			FileOutputStream fileOutputStream = new FileOutputStream(torrentFile);
			fileOutputStream.write(encodedTorrent.toByteArray());
			fileOutputStream.close();
			encodedTorrent.close();
		} catch (Exception exception) {
			torrent.delete();
			throw exception;
		}

		TorrentsCache.getInstance().put(torrent);

//		TorrentsPaginator.getCategory(torrent.getCategoryId()).reset(); // TODO:

		return torrent;
	}
}
