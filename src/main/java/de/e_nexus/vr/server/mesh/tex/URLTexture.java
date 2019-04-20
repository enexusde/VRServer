/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh.tex;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class URLTexture implements Texture {

	private final URL url;
	private final int width;
	private final int height;
	private Integer id;
	private final ImageReader reader;

	public URLTexture(URL url) throws IOException, ArrayIndexOutOfBoundsException {
		this.url = url;
		InputStream openStream = url.openStream();

		ImageInputStream iis = ImageIO.createImageInputStream(openStream);
		Iterator<ImageReader> d = ImageIO.getImageReaders(iis);
		reader = d.next();
		reader.setInput(iis);
		width = reader.getWidth(reader.getMinIndex());
		height = reader.getHeight(reader.getMinIndex());
		if (width < 1) {
			throw new ArrayIndexOutOfBoundsException("Width of image must be > 0 but is " + width);
		}
		if (height < 1) {
			throw new ArrayIndexOutOfBoundsException("Height of image must be > 0 but is " + height);
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public InputStream createDataStream() {
		try {
			BufferedImage i = reader.read(0);
			System.out.println(i.isAlphaPremultiplied() + "i=" + i);
			return new PixelOutputStream(i.getRaster(), i.getColorModel(), getWidth());
		} catch (IOException e) {
			throw new RuntimeException("URL " + url + " not available anymore!");
		}
	}

}
