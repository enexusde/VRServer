package de.e_nexus.vr.server.mesh.tex;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import de.e_nexus.vr.server.mesh.Mesh;
import de.e_nexus.vr.server.util.NumberTools;

public class MeshTextureInfoInputStream extends InputStream {

	private final static Logger LOG = Logger.getLogger(MeshTextureInfoInputStream.class.getCanonicalName());

	private final InputStream in;

	public MeshTextureInfoInputStream(InputStream in) {
		this.in = in;
	}

	public int read() throws IOException {
		return in.read();
	}

	public int hashCode() {
		return in.hashCode();
	}

	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	public boolean equals(Object obj) {
		return in.equals(obj);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	public int available() throws IOException {
		return in.available();
	}

	public void close() throws IOException {
		in.close();
	}

	public void mark(int readlimit) {
		in.mark(readlimit);
	}

	public void reset() throws IOException {
		in.reset();
	}

	public boolean markSupported() {
		return in.markSupported();
	}

	public void readTextureIndexes(Mesh<?> m) throws IOException {
		LOG.fine("Read texture indexes from client.");
		for (TextureStage s : TextureStage.values()) {
			int triesLeft = 30;
			LOG.finer("Maximum tries of " + triesLeft);
			while (in.available() < 4 && --triesLeft > 0) {
				LOG.finer("Not enougth bytes available, wait a short time.");
				try {
					int max = Math.max(10, triesLeft)*10;
					LOG.finest("Wait " + max + " miliseconds until look if enougth bytes availale.");
					Thread.sleep(max);
				} catch (InterruptedException e) {
					throw new IOException("Wait for texture index read interrupted!", e);
				}
			}

			if (triesLeft <= 0) {
				throw new IOException("Wait too long for reading.");
			}
			LOG.fine("Informations available, read level: " + s);
			int imageId = NumberTools.readByteArrayBigEndian(in);
			if (imageId != -1) {
				LOG.fine("Level " + s + " returns a imageId of " + imageId);
				m.textures.get(s).setId(imageId);
			} else {
				LOG.fine("Level " + s + " reported to have no image.");
			}
		}
	}
}
