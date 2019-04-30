/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh.tex;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

public class PixelOutputStream extends InputStream {

	enum Field {
		R, G, B, A
	}

	private int currentX = 0;
	private int currentY = 0;
	private Field currentField = Field.R;
	private WritableRaster raster;
	private ColorModel colorModel;
	private int width;

	public PixelOutputStream(WritableRaster raster, ColorModel colorModel, int width) {
		this.raster = raster;
		this.colorModel = colorModel;
		this.width = width;
	}

	@Override
	public int read() throws IOException {
		Object pixel;
		try {
			pixel = raster.getDataElements(currentX, currentY, null);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(currentX + "x" + currentY, e);
		}
		switch (currentField) {
		case R:
			currentField = Field.G;
			// 0 means no red, 255 means full red.
			return colorModel.getRed(pixel);
		case G:
			currentField = Field.B;
			// 0 means no green, 255 means full green.
			return colorModel.getGreen(pixel);
		case B:
			currentField = Field.A;
			// 0 means no blue, 255 means full blue.
			return colorModel.getBlue(pixel);
		case A:
			currentField = Field.R;
			next();
			// 0 is invisible, 255 means no transparency
			return colorModel.getAlpha(pixel);
		default:
			throw new RuntimeException("Illegal field: " + currentField);
		}
	}

	protected void next() {
		currentX++;
		if (currentX == width) {
			currentX = 0;
			currentY++;
		}
	}

}
