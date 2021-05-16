package com.conti.elf_reader.utils.data_types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

public class DataTypes {

	public final static String start32String = "********************************";

	public static void readFully(
			final ReadableByteChannel readableByteChannel, final ByteBuffer byteBuffer) throws Exception {

		byteBuffer.rewind();
		final int read = readableByteChannel.read(byteBuffer);
		if (read != byteBuffer.limit()) {
			return;
		}

		byteBuffer.flip();
	}

	public static ByteBuffer getByteBuffer(final ByteBuffer source, final int count, final ByteOrder order) {

		try {
			final byte[] data = new byte[count];
			source.get(data);
			final ByteBuffer result = ByteBuffer.wrap(data);
			result.order(order);
			return result;
		} catch (final Exception ignored) {
		}
		return null;
	}

	public static byte tryParseByte(final String byteString) {

		byte value = -1;
		try {
			value = Byte.parseByte(byteString);
		} catch (final Exception ignored) {
		}
		return value;
	}

	public static boolean tryParseBoolean(final String booleanString) {

		boolean b = false;
		try {
			b = Boolean.parseBoolean(booleanString);
		} catch (final Exception ignored) {
		}
		return b;
	}

	public static int tryParseInteger(final String intString) {

		int value = -1;
		try {
			value = Integer.parseInt(intString);
		} catch (final Exception ignored) {
		}
		return value;
	}

	public static long tryParseLong(final String longString) {

		long value = -1;
		try {
			value = Long.parseLong(longString);
		} catch (final Exception ignored) {
		}
		return value;
	}

	public static long tryParseHexString(String hexString) {

		long value = -1;
		try {
			hexString = hexString.substring(2);
			value = Long.parseLong(hexString, 16);
		} catch (final Exception ignored) {
		}
		return value;
	}

	public static String parseNullTerminatedString(final ByteBuffer buffer) {

		final StringBuilder result = new StringBuilder();
		while (buffer.remaining() > 0) {

			final char c = (char) buffer.get();
			if (c == '\0') {
				break;
			}

			result.append(c);
		}
		return result.toString();
	}

	public static long parseUnsignedLittleEndianBase128(final ByteBuffer byteBuffer) {

		long val = 0;
		int shift = 0;

		byte b;
		while (true) {

			b = byteBuffer.get();
			val |= ((long) (b & 0x7f)) << shift;
			if ((b & 0x80) == 0) {
				break;
			}
			shift += 7;
		}

		return val;
	}

	public static int parseSignedLittleEndianBase128(final ByteBuffer byteBuffer) {

		int result = 0;
		for (int i = 0; i < 5; i++) {

			final byte b = byteBuffer.get();
			result |= ((b & 0x7f) << (7 * i));
			if ((b & 0x80) == 0) {
				final int s = 32 - (7 * (i + 1));
				result = (result << s) >> s;
				break;
			}
		}
		return result;
	}

	public static String signExtendTo32(final String binaryString, final int binaryStringMaxLength) {

		final int binaryStringLength = binaryString.length();
		final char signExtendBit = (binaryStringLength == binaryStringMaxLength + 1 && binaryString.charAt(0) == '1') ?
				'1' : '0';

		final StringBuilder signExtendedBinaryString = new StringBuilder();
		final int deficit = 32 - binaryStringLength;
		for (int i = 0; i < deficit; i++) {
			signExtendedBinaryString.append(signExtendBit);
		}
		signExtendedBinaryString.append(binaryString);
		return signExtendedBinaryString.toString();
	}

	public static String bytesToString(final byte[] bytes) {

		final StringBuilder result = new StringBuilder();
		for (final byte b : bytes) {
			result.append(byteToString(b));
		}
		return result.toString();
	}

	public static String byteToString(final byte b) {
		return intToPaddedBinaryString(b & 0xFF, 8);
	}

	public static String intToPaddedBinaryString(final int n, final int size) {

		final StringBuilder stringBuilder = new StringBuilder();
		for (int position = size - 1; position >= 0; position--) {
			stringBuilder.append(((n >> position) & 1) == 1 ? '1' : '0');
		}
		return stringBuilder.toString();
	}

	public static String hexString(final long value) {
		return "0x" + Long.toHexString(value);
	}
}
