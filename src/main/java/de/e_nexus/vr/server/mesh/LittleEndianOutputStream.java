package de.e_nexus.vr.server.mesh;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import de.e_nexus.vr.server.util.NumberTools;

/**
 * A adapter for little-endian output streams.
 */
public abstract class LittleEndianOutputStream extends OutputStream {

	/**
	 * The logger of the class.
	 */
	private final static Logger LOG = Logger.getLogger(LittleEndianOutputStream.class.getCanonicalName());

	/**
	 * The origin output stream.
	 */
	protected final OutputStream out;

	/**
	 * Creates a new instance of the outputstream.
	 * 
	 * @param out The origin output stream.
	 */
	public LittleEndianOutputStream(OutputStream out) {
		this.out = out;
	}

	/**
	 * Writes a float value in little endian.
	 * 
	 * @param i The float value to write.
	 * @throws IOException if an I/O exception occoured (i.e. opposite closed
	 *                     connection).
	 */
	public void writeLittleEndian(float i) throws IOException {
		write(NumberTools.toByteArrayLittleEndian(i));
	}

	/**
	 * Writes a int value in little endian bytes.
	 * 
	 * @param i The int value to write.
	 * @throws IOException if an I/O exception occoured (i.e. opposite closed
	 *                     connection).
	 */
	public void writeLittleEndian(int i) throws IOException {
		LOG.finest("Write int " + i);
		write(NumberTools.toByteArrayLittleEndian(i));
	}

	public void write(int b) throws IOException {
		out.write(b);
	}

	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	public boolean equals(Object obj) {
		return out.equals(obj);
	}

	public void flush() throws IOException {
		out.flush();
	}

	public void close() throws IOException {
		out.close();
	}
}
