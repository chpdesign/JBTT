package tracker.util;

import play.mvc.Http;

public class NetUtils {
	public static String getIp() {
		play.mvc.Http.Header h = Http.Request.current().headers.get("x-real-ip");
		if (h == null) {
			return Http.Request.current().remoteAddress;
		} else {
			return h.value();
		}
	}
}
