package tracker.bencode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Decoder {
	private Boolean recoveryMode = false;
	private Charset charset = Charset.forName("UTF-8");
	private Charset byteCharset = Charset.forName("ISO-8859-1");
	private InputStream inputStream = null;

	public Map decode(byte[] data) throws IOException {
		return this.decode(new ByteArrayInputStream(data));
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> decode(InputStream inputStream) throws IOException {
		this.inputStream = inputStream;

		Object result = this.decodeInputStream(0);

		if (result == null) {
			throw new IOException("Zero length file");
		}

		if (!(result instanceof Map)) {
			throw new IOException("Top level isn't a Map");
		}

		return (Map<String, Object>)result;
	}

	private Map<String, Object> decodeDictionary(Integer nesting) throws IOException {
		Map<String, Object> dictionary = new TreeMap<String, Object>();

		try {
			while (true) {
				byte[] keyBytes = (byte[])this.decodeInputStream(nesting + 1);
				if (keyBytes == null) {
					break;
				}

				CharBuffer charBuffer = this.getByteCharset().decode(ByteBuffer.wrap(keyBytes));
				String key = new String(charBuffer.array(), 0, charBuffer.limit());

				Object value = this.decodeInputStream(nesting + 1);

				dictionary.put(key, value);
			}

			this.getInputStream().mark(Integer.MAX_VALUE);
			Integer nextByte = this.getInputStream().read();
			this.getInputStream().reset();

			if (nesting > 0 && nextByte == -1) {
				throw new IOException("Invalid input data, 'e' missing from end of dictionary");
			}
		} catch (IOException e) {
			if (!this.isRecoveryMode()) {
				throw e;
			}
		}

		return dictionary;
	}

	private List<Object> decodeList(Integer nesting) throws IOException {
		List<Object> list = new ArrayList<Object>();

		try {
			while (true) {
				Object item = this.decodeInputStream(nesting + 1);
				if (item == null) {
					break;
				}

				list.add(item);
			}

			this.getInputStream().mark(Integer.MAX_VALUE);
			Integer nextByte = this.getInputStream().read();
			this.getInputStream().reset();

			if (nesting > 0 && nextByte == -1) {
				throw new IOException("Invalid input data, 'e' missing from end of list");
			}
		} catch (IOException e) {
			if (!this.isRecoveryMode()) {
				throw e;
			}
		}

		return list;
	}

	private Long decodeLong() throws IOException {
		return this.decodeLong('e');
	}

	private Long decodeLong(Character parseChar) throws IOException {
		StringBuffer stringBuffer = new StringBuffer();

		while (true) {
			int nextByte = this.getInputStream().read();

			if (nextByte == parseChar || nextByte < 0) {
				break;
			}

			stringBuffer.append((char)nextByte);
		}

		if (stringBuffer.length() < 1) {
			return null;
		}

		return Long.parseLong(stringBuffer.toString());
	}


	private Object decodeInputStream(Integer nesting) throws IOException {
		if (nesting == 0 && !this.getInputStream().markSupported()) {
			throw new IOException("InputStream must support the mark() method");
		}

		this.getInputStream().mark(Integer.MAX_VALUE);
		int nextByte = this.getInputStream().read();
		switch (nextByte) {
			case 'd':
				return this.decodeDictionary(nesting);

			case 'l':
				return this.decodeList(nesting);

			case 'e':
			case -1:
				return null;

			case 'i':
				return this.decodeLong();

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				this.getInputStream().reset();
				return getByteArrayFromStream();

			default: {
				byte[] remainderData = new byte[(inputStream.available() > 256) ? 256 : inputStream.available()];
				inputStream.read(remainderData);

				throw new IOException("Unknown command: " + nextByte);
			}
		}
	}

	private byte[] getByteArrayFromStream() throws IOException {
		Integer arrayLength = this.decodeLong(':').intValue();

		if (arrayLength < 0) {
			return null;
		}

		if (arrayLength > 8 * 1024 * 1024) { // TODO: move to config
			throw new IOException("Byte array length too large: " + arrayLength);
		}

		byte[] buffer = new byte[arrayLength];
		int bytesReceived = 0;

		while (bytesReceived != arrayLength) {
			int length = this.getInputStream().read(buffer, bytesReceived, arrayLength - bytesReceived);

			if (length < 1) {
				break;
			}

			bytesReceived += length;
		}



		if (bytesReceived != buffer.length) {
			throw new IOException("Truncated");
		}

		return buffer;
	}

	public Decoder() { }

	public Decoder(String charset) {
		this(Charset.forName(charset));
	}

	public Decoder(Charset charset) {
		this.charset = charset;
	}

	public Decoder(String charset, String byteCharset) {
		this(Charset.forName(charset), Charset.forName(byteCharset));
	}

	public Decoder(Charset charset, Charset byteCharset) {
		this.charset = charset;
		this.byteCharset = byteCharset;
	}


	public Charset getCharset() {
		return this.charset;
	}

	public Decoder setCharset(String charset) {
		this.setCharset(Charset.forName(charset));
		return this;
	}

	public Decoder setCharset(Charset charset) {
		this.charset = charset;
		return this;
	}

	public Charset getByteCharset() {
		return this.byteCharset;
	}

	public Decoder setByteCharset(String charset) {
		this.setByteCharset(Charset.forName(charset));
		return this;
	}

	public Decoder setByteCharset(Charset charset) {
		this.byteCharset = charset;
		return this;
	}

	public Decoder setRecoveryMode(Boolean recoveryMode) {
		this.recoveryMode = recoveryMode;
		return this;
	}

	public Boolean getRecoveryMode() {
		return this.recoveryMode;
	}

	public Boolean isRecoveryMode() {
		return this.getRecoveryMode();
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public static Decoder get() {
		return new Decoder();
	}
}
