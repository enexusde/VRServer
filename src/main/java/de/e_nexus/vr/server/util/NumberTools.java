/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

public class NumberTools {

	private final static Logger LOG = Logger.getLogger(NumberTools.class.getCanonicalName());

	public static byte[] toByteArrayLittleEndian(int number) {
		byte[] memoryBlock = new byte[4];
		memoryBlock[0] = (byte) (number & 0xFF);
		memoryBlock[1] = (byte) ((number >> 8) & 0xFF);
		memoryBlock[2] = (byte) ((number >> 16) & 0xFF);
		memoryBlock[3] = (byte) ((number >> 24) & 0xFF);
		return memoryBlock;
	}

	public static byte[] toByteArrayLittleEndian(float value) {
		ByteBuffer allocate = ByteBuffer.allocate(4);
		byte[] array = allocate.putFloat(value).order(ByteOrder.BIG_ENDIAN).array();
		return new byte[] { array[3], array[2], array[1], array[0] };
	}

	public static int readByteArrayBigEndian(InputStream is) throws IOException {
		byte[] array = { (byte) is.read(), (byte) is.read(), (byte) is.read(), (byte) is.read() };
		ByteBuffer allocate = ByteBuffer.allocate(4);
		allocate.order(ByteOrder.LITTLE_ENDIAN);
		allocate.put(array);
		return allocate.getInt(0);
	}

	public static float readByteArrayBigEndianFloat(InputStream is) throws IOException {
		byte[] array = { (byte) is.read(), (byte) is.read(), (byte) is.read(), (byte) is.read() };
		return getByxteArrayBigEndianFloat(array);
	}

	private static float getByxteArrayBigEndianFloat(byte[] array) {
		ByteBuffer allocate = ByteBuffer.allocate(4);
		allocate.order(ByteOrder.LITTLE_ENDIAN);
		allocate.put(array);
		return allocate.getFloat(0);
	}

	public static void printbytes(byte[] fd) {
		int index = 0;
		for (byte b : fd) {
			int i = b;
			if (i < 0) {
				i += 256;
			}
			LOG.info(index + "\t" + i + "(0-255) \t " + b + " (-127 - 127) \t " + byteToString(b));
			index++;
		}
	}

	public static void main(String[] args) {
		System.out.println("-----------sdf" + getByxteArrayBigEndianFloat(new byte[] { 84, 1, 4, 64 }));
	}

	public static String byteToString(byte b) {
		byte[] masks = { -128, 64, 32, 16, 8, 4, 2, 1 };
		StringBuilder builder = new StringBuilder();
		for (byte m : masks) {
			if ((b & m) == m) {
				builder.append('1');
			} else {
				builder.append('0');
			}
		}
		return builder.toString();
	}
}
