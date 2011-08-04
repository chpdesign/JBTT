package tracker.announcer;

import play.exceptions.UnexpectedException;
import play.mvc.Http;
import play.mvc.results.RenderBinary;
import tracker.bencode.Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

public class Response {
	public static void error(String reason) {
		TreeMap<String, Object> response = new TreeMap<String, Object>();
		response.put("failure reason", reason);
		Response.send(response);
	}

	public static void send(Map<String, Object> response) {
		ByteArrayOutputStream outputStream;
		try {
			outputStream = Encoder.get().encode(response);


			Http.Response httpResponse = Http.Response.current();
			httpResponse.setHeader("content-type", "text/plain");
			httpResponse.setHeader("pragma" , "no-cache");
			httpResponse.setHeader("server", "JBTT");
			// httpResponse.cookies = new TreeMap<String, Http.Cookie>();

			// httpResponse.setCookie("PLAY_SESSION", "");

			// httpResponse.setHeader("Date", System.currentTimeMillis()); // TODO: Cast into RFC 2616 sec 3.3.1.

			InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			throw new RenderBinary(inputStream, null, true);

//			httpResponse.out.write(outputStream.toByteArray());
//			httpResponse.out.close();
		} catch (IOException exception) {
			throw new UnexpectedException(exception);
		}
	}
}
