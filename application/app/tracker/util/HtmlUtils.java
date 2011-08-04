package tracker.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import play.Logger;
import tracker.util.bbcode.BBString;

public class HtmlUtils {
	public static String newLineToBr(String value) {
		return value.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
	}

	public static String brToNewLine(String value) { // TODO: check me
		return value.replaceAll("<(|/)\\s*?br\\s*?>", "\n");
	}

	public static String parseBB(String source) {
		source = StringEscapeUtils.escapeHtml(source);
		source = newLineToBr(source);
		source = Jsoup.clean(source, new Whitelist().addTags("br"));
		BBString result = new BBString(source);
		return result.getHtml();
	}

	public static void test() {
		String html = "<p>Hello <span style='color: red;'>world</span><script>alert('xss!');</script></p>";

//		Document document = Jsoup.parseBodyFragment(html);


		Whitelist whitelist = new Whitelist();
		whitelist.addTags("p", "span", "blockquote");
//		whitelist.addAttributes("span", "style");

		String cleanedHtml = Jsoup.clean(html, whitelist);

		Logger.info(cleanedHtml);
	}
}
