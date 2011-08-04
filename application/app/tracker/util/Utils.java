package tracker.util;

import play.exceptions.UnexpectedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Utils {
	private static Random random = new Random();

	private static final String HEXES = "0123456789abcdef";

	public static Random getRandom() {
		return random;
	}

	public static String getHexString(byte[] raw) {
		if (raw == null) {
			return null;
		}

		final StringBuilder hex = new StringBuilder(2 * raw.length);

		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}

		return hex.toString();
	}

	public static byte[] getByteArray(String hex) {
		int len = hex.length();
		byte[] raw = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			raw[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
		}

		return raw;
	}

	public static Map<String, String[]> parseUrlEncodedString(String urlEncoded, Charset charset, boolean isQueryString) {
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		try {
			InputStream is = new ByteArrayInputStream(urlEncoded.getBytes(charset));

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
            byte[] data = os.toByteArray();


            // add the complete body as a parameters
            if(!isQueryString) {
                parameters.put("body", new String[] {new String(data, charset)});
            }


            int ix = 0;
            int ox = 0;
            String key = null;
            String value = null;
            while (ix < data.length) {
                byte c = data[ix++];
                switch ((char) c) {
                    case '&':
                        value = new String(data, 0, ox, charset);
                        if (key != null) {
                            play.utils.Utils.Maps.mergeValueInMap(parameters, key, value);
                            key = null;
                        }
                        ox = 0;
                        break;
                    case '=':
                        if (key == null) {
                            key = new String(data, 0, ox, charset);
                            ox = 0;
                        } else {
                            data[ox++] = c;
                        }
                        break;
                    case '+':
                        data[ox++] = (byte) ' ';
                        break;
                    case '%':
                        data[ox++] = (byte) ((convertHexDigit(data[ix++]) << 4) + convertHexDigit(data[ix++]));
                        break;
                    default:
                        data[ox++] = c;
                }
            }
            //The last value does not end in '&'.  So save it now.
            if (key != null) {
                value = new String(data, 0, ox, charset);
                play.utils.Utils.Maps.mergeValueInMap(parameters, key, value);
            }

		} catch (Exception ex) {
            throw new UnexpectedException(ex);
        }

		return parameters;
	}

	private static byte convertHexDigit(byte b) {
        if ((b >= '0') && (b <= '9')) {
            return (byte) (b - '0');
        }
        if ((b >= 'a') && (b <= 'f')) {
            return (byte) (b - 'a' + 10);
        }
        if ((b >= 'A') && (b <= 'F')) {
            return (byte) (b - 'A' + 10);
        }
        return 0;
    }

	public static String integerToIp(Integer ip) {
        return ((ip >> 24 ) & 0xFF) + "." +
               ((ip >> 16 ) & 0xFF) + "." +
               ((ip >>  8 ) & 0xFF) + "." +
               ((ip       ) & 0xFF);
    }

	public static String longToIp(long ip) { // TODO: check me
        return ((ip >> 24 ) & 0xFF) + "." +
               ((ip >> 16 ) & 0xFF) + "." +
               ((ip >>  8 ) & 0xFF) + "." +
               ((ip       ) & 0xFF);
    }

	public static int ipToInteger(String ip) {
		final String[] ipBytesStr = ip.split("\\.");
		int ipValue = 0;
		for (int i = 0; i < 4; i++) {
			ipValue <<= 8;
			ipValue |= Integer.parseInt(ipBytesStr[i]);
		}
		return ipValue;
	}

	public static long ipToLong(String ip) { // TODO: check me
		final String[] ipBytesStr = ip.split("\\.");
		long ipValue = 0;
		for (int i = 0; i < 4; i++) {
			ipValue <<= 8;
			ipValue |= Integer.parseInt(ipBytesStr[i]);
		}
		return ipValue;
	}

	public static byte[] integerToBytes(int value) {
		return new byte[] {
			(byte)(value >>> 24),
			(byte)(value >>> 16),
			(byte)(value >>> 8),
			(byte)value
		};
	}

	public static byte[] longToNetworkBytes(long value) { // TODO: rename me
		return new byte[] {
			(byte)(value >>> 56),
			(byte)(value >>> 48),
			(byte)(value >>> 40),
			(byte)(value >>> 32),
			(byte)(value >>> 24),
			(byte)(value >>> 16),
			(byte)(value >>> 8),
			(byte)value,
		};
	}

	public static int bytesToInteger(byte[] bytes) {
		return (bytes[0] << 24)
			+ ((bytes[1] & 0xFF) << 16)
			+ ((bytes[2] & 0xFF) << 8)
			+ (bytes[3] & 0xFF);
	}

	public static byte[] longToByteArray(long value) {
		byte[] byteArray = new byte[8];
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		LongBuffer longBuffer = byteBuffer.asLongBuffer();
		longBuffer.put(0, value);
		return byteArray;
	}

	public static Long byteArrayToLong(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        return byteBuffer.getLong(byteArray[1]);
	}

	public static Timestamp getCurrentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static byte[] hashSha1(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return md.digest(data);
    }
}
