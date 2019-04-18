package de.e_nexus.vr.server.mesh.tex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import de.e_nexus.vr.server.mesh.BigEndianOutputStream;
import de.e_nexus.vr.server.mesh.Mesh;
import de.e_nexus.vr.server.mesh.Vector;

public class MeshTexturesOutputStream<T extends Vector> extends BigEndianOutputStream {

	private final static Logger LOG = Logger.getLogger(MeshTexturesOutputStream.class.getCanonicalName());

	public MeshTexturesOutputStream(OutputStream out) {
		super(out);
	}

	public void writeTextures(Mesh<T> mesh) throws IOException {
		if (mesh.textures.isEmpty()) {
			LOG.fine("No textures in " + mesh + "! Send -1.");
			write(-1);
			return;
		}
		byte textureCount = (byte) mesh.textures.size();
		LOG.fine("There are " + textureCount + " textures available in mesh " + mesh + ".");
		write(textureCount);
		flush();
		for (TextureStage stage : mesh.textures.keySet()) {
			LOG.finer("Start to send texture stage: " + stage + " of mesh " + mesh + ".");
			Texture texture = mesh.textures.get(stage);
			if (texture.getId() == null) {
				LOG.finer("Texture of mesh " + mesh + " is unknown to the VR client.");
				LOG.finest("Transport ADD TEXTURE (byte 0) to VR client.");
				write((byte) 0);
				int width = texture.getWidth();
				int height = texture.getHeight();
				write((byte) stage.ordinal());
				LOG.finest("Transport width of " + width + " and height of " + height + " of the new texture for mesh "
						+ mesh + " to VR client.");
				writeBigEndian(width);
				writeBigEndian(height);
				LOG.finest("Start transportation of memblock to VR client.");
				writeBigEndian(width);
				writeBigEndian(height);
				writeBigEndian(32);
				InputStream in = texture.createDataStream();
				int bytesLeft = width * height * 4;
				byte[] buff;
				while (bytesLeft > 0) {
					buff = new byte[Math.min(1024, bytesLeft)];
					int haveRead = in.read(buff);
					write(buff, 0, haveRead);
					bytesLeft -= haveRead;
					flush();
				}
				LOG.fine(
						"All texture data on stage " + stage + " for a new texture are written for mesh " + mesh + ".");
			} else {
				LOG.fine("The texture already exists in the client having the id " + texture.getId());
				LOG.finest("Sending TEXTURE ALREADY EXISTS(byte 1) for mesh " + mesh + " to the VR client.");
				write((byte) 1);
				LOG.finest("Sending exists for stage " + stage + ".");
				write((byte) stage.ordinal());
				LOG.finest("Sending imageId " + texture.getId() + " to VR client.");
				writeBigEndian(texture.getId());
				LOG.finest("All reference informations are written for stage " + stage + " of mesh " + mesh + ".");
			}
			LOG.finest("Flushing the output.");
			flush();
		}
	}
}
