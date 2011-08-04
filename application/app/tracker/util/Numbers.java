package tracker.util;

public class Numbers {
	public static byte[] longToNetwork(long value) {
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

	public static byte[] longToNetworkIp(long value) {
		byte[] bytes = longToNetwork(value);
		return new byte[] { bytes[4], bytes[5], bytes[6], bytes[7] };
	}

	public static byte[] longToNetworkPort(long value) {
		byte[] bytes = longToNetwork(value);
		return new byte[] { bytes[6], bytes[7] };
	}
}
