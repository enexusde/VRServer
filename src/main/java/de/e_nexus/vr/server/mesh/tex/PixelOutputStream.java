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
			return 255 - colorModel.getRed(pixel);
		case G:
			currentField = Field.B;
			return 255 - colorModel.getGreen(pixel);
		case B:
			currentField = Field.A;
			return 255 - colorModel.getBlue(pixel);
		case A:
			currentField = Field.R;
			next();
			return 255 - colorModel.getAlpha(pixel);
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
