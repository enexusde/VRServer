/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh.tex;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

/**
 * Creates an image-texture out of a {@link BufferedImage}.
 * <p>
 * In contrast to {@link URLTexture} this class do not require a {@link URL} to
 * transport an image to the client. This class is thread-save. There is no
 * guarantee that the texture is available in the vr-client unless the id is
 * set.
 */
public class BufferedImageTexture implements Texture {

	private final BufferedImage image;

	public BufferedImageTexture(BufferedImage image) {
		this.image = image;
	}

	private Integer id;

	public Integer getId() {
		return id;
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public InputStream createDataStream() {
		return new PixelOutputStream(image.getRaster(), image.getColorModel(), image.getWidth());
	}

	public void setId(int id) {
		this.id = id;
	}
}
