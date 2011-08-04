package tracker.util;

import tracker.Config;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
	private final static String[] MONTHS = { "января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа",
		"сентября", "октября", "ноября", "декабря" };

	public static String getDate(Timestamp timestamp) {
		Calendar calendar = timestampToCalendar(timestamp);
		String monthString = MONTHS[calendar.get(Calendar.MONTH)];

		return String.format("%d %s %d в %d:%02d",
			calendar.get(Calendar.DAY_OF_MONTH),
			monthString,
			calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE)
		);
	}

	public static Calendar timestampToCalendar(Timestamp timestamp) {
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(Config.getString("common.timezone")));
		calendar.setTimeInMillis(timestamp.getTime());
		return calendar;
	}
}
