package tracker.bencode;

import play.exceptions.UnexpectedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Encoder {
	private Charset charset = Charset.forName("UTF-8");
	private Charset byteCharset = Charset.forName("ISO-8859-1");

	public ByteArrayOutputStream encode(Float value) throws IOException {
		return this.encode(String.valueOf(value));
	}

	public ByteArrayOutputStream encode(String value) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ByteBuffer byteBuffer = this.getCharset().encode(value);

		this.write(outputStream, String.valueOf(byteBuffer.limit()));
		this.write(outputStream, ':');
		this.write(outputStream, byteBuffer);

		return outputStream;
	}

	public ByteArrayOutputStream encode(Long value) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		this.write(outputStream, 'i');
		this.write(outputStream, value);
		this.write(outputStream, 'e');

		return outputStream;
	}

	public ByteArrayOutputStream encode(Integer value) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		this.write(outputStream, 'i');
		this.write(outputStream, value);
		this.write(outputStream, 'e');

		return outputStream;
	}

	public ByteArrayOutputStream encode(ByteBuffer value) throws IOException {
		return this.encode(value.array());
	}

	public ByteArrayOutputStream encode(byte[] value) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		this.write(outputStream, String.valueOf(value.length));
		this.write(outputStream, ':');
		this.write(outputStream, value);

		return outputStream;
	}

	public ByteArrayOutputStream encode(List value) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		this.write(outputStream, 'l');
		for (Object listValue : value) {
			this.encode(listValue.getClass().cast(listValue)).writeTo(outputStream);
		}
		this.write(outputStream, 'e');

		return outputStream;
	}

	public ByteArrayOutputStream encode(Map<String, Object> value) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		this.write(outputStream, 'd');

		SortedMap tree;
		if (value instanceof TreeMap) {
			tree = (TreeMap) value;
		} else {
			tree = new TreeMap<String, Object>(value); // do map sorting here
		}

		for (Object entryObject : tree.entrySet()) {
			Map.Entry entry = (Map.Entry)entryObject;
			Object entryKey = entry.getKey();
			Object entryValue = entry.getValue();

			if (entryValue == null) {
				continue;
			}

			this.encode(entryKey.getClass().cast(entryKey)).writeTo(outputStream);
			this.encode(entryValue.getClass().cast(entryValue)).writeTo(outputStream);
		}

		this.write(outputStream, 'e');

		return outputStream;
	}

	public ByteArrayOutputStream encode(Object value) throws IOException {
		if (value instanceof Float) {
			return this.encode((Float)value);
		}

		if (value instanceof String) {
			return this.encode((String)value);
		}

		if (value instanceof Long) {
			return this.encode((Long)value);
		}

		if (value instanceof Integer) {
			return this.encode((Integer)value);
		}

		if (value instanceof ByteBuffer) {
			return this.encode((ByteBuffer)value);
		}

		if (value instanceof ByteArrayOutputStream) {
			return this.encode(((ByteArrayOutputStream)value).toByteArray());
		}

		if (value instanceof byte[]) {
			return this.encode((byte[])value);
		}

		if (value instanceof List) {
			return this.encode((List)value);
		}

		if (value instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> mapValue = (TreeMap<String, Object>)value;

			return this.encode(mapValue);
		}

		throw new UnexpectedException(value.getClass().getName() + " class encoding not implemented.");
	}


	protected void write(OutputStream outputStream, Long value) throws IOException {
		this.write(outputStream, this.getCharset().encode(value.toString()));
	}

	protected void write(OutputStream outputStream, Integer value) throws IOException {
		this.write(outputStream, this.getCharset().encode(value.toString()));
	}

	protected void write(OutputStream outputStream, String value) throws IOException {
		this.write(outputStream, this.getCharset().encode(value));
	}

	protected void write(OutputStream outputStream, Character value) throws IOException {
		outputStream.write(value);
	}

	protected void write(OutputStream outputStream, ByteBuffer byteBuffer) throws IOException {
        outputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
    }

	protected void write(OutputStream outputStream, byte[] byteArray) throws IOException {
        outputStream.write(byteArray, 0, byteArray.length);
    }


	public Encoder() { }

	public Encoder(String charset) {
		this(Charset.forName(charset));
	}

	public Encoder(Charset charset) {
		this.charset = charset;
	}

	public Encoder(String charset, String byteCharset) {
		this(Charset.forName(charset), Charset.forName(byteCharset));
	}

	public Encoder(Charset charset, Charset byteCharset) {
		this.charset = charset;
		this.byteCharset = byteCharset;
	}


	public Charset getCharset() {
		return this.charset;
	}

	public Encoder setCharset(String charset) {
		this.setCharset(Charset.forName(charset));
		return this;
	}

	public Encoder setCharset(Charset charset) {
		this.charset = charset;
		return this;
	}

	public Charset getByteCharset() {
		return this.byteCharset;
	}

	public Encoder setByteCharset(String charset) {
		this.setByteCharset(Charset.forName(charset));
		return this;
	}

	public Encoder setByteCharset(Charset charset) {
		this.byteCharset = charset;
		return this;
	}

	public static Encoder get() {
		return new Encoder();
	}
}
