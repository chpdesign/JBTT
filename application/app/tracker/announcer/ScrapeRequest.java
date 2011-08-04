package tracker.announcer;

import play.mvc.Http;
import play.mvc.Scope;
import tracker.Config;
import tracker.util.Utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScrapeRequest {
	private Scope.Params params;
	private Map<String, String[]> binarySafeParams;

	public ScrapeRequest(Scope.Params params) {
		this.params = params;
		this.binarySafeParams = Utils.parseUrlEncodedString(Http.Request.current().querystring, Charset.forName(Config.getString("tracker.bytesCharset")), true);
	}

	public List<byte[]> getInfoHashes() throws UnsupportedEncodingException {
		if (!this.binarySafeParams.containsKey("info_hash")) {
			Response.error("Invalid info_hash");
		}

		List<byte[]> infoHashes = new ArrayList<byte[]>();
		for (String infoHashStr : this.binarySafeParams.get("info_hash")) {
			byte[] infoHash = infoHashStr.getBytes(Config.getString("tracker.bytesCharset"));

			if (infoHash == null || infoHash.length != 20) {
				Response.error("Invalid info_hash");
			}

			infoHashes.add(infoHash);
		}

		return infoHashes;
	}

	public List<String> getInfoHashHexStrings() throws UnsupportedEncodingException {
		List<String> infoHashHexStrings = new ArrayList<String>();

		for (byte[] infoHash : this.getInfoHashes()) {
			infoHashHexStrings.add(Utils.getHexString(infoHash));
		}

		return infoHashHexStrings;
	}

	public String getPassKey() {
		if (!params._contains("passkey")) {
			return null;
		}

		return params.get("passkey");
	}
}
