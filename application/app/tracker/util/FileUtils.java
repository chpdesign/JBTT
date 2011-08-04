package tracker.util;

public class FileUtils {
	public static String getSizeString(Long size) {
		String sizeString;
		if (size > 1099511627776D) {
			sizeString = String.format("%.2f Тб", size / 1099511627776D);
		} else if (size > 1073741824D) {
			sizeString = String.format("%.2f Гб", size / 1073741824D);
		} else if (size > 1048576D) {
			sizeString = String.format("%.2f Мб", size / 1048576D);
		} else if (size > 1024D) {
			sizeString = String.format("%.2f Кб", size / 1024D);
		} else {
			sizeString = String.format("%d байт", size);
		}
		return sizeString;
	}
}
