package de.e_nexus.vr.server.mesh;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import de.e_nexus.vr.server.mesh.tex.MeshTextureInfoInputStream;
import de.e_nexus.vr.server.util.NumberTools;

public abstract class BigEndianOutputStream extends OutputStream {
	private final static Logger LOG = Logger.getLogger(BigEndianOutputStream.class.getCanonicalName());

	protected final OutputStream out;

	public BigEndianOutputStream(OutputStream out) {
		this.out = out;
	}

	protected void writeBigEndian(float i) throws IOException {
		write(NumberTools.toByteArrayBigEndian(i));
	}

	protected void writeBigEndian(int i) throws IOException {
		LOG.finest("Write int " + i);
		write(NumberTools.toByteArrayBigEndian(i));
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
