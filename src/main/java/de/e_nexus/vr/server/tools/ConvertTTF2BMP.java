package de.e_nexus.vr.server.tools;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ConvertTTF2BMP {
	private static final int HORI = 1024;
	private static final int VERT = 500;

	public static void main(String[] args) throws IOException, FontFormatException {
		if (args.length != 2) {
			throw new RuntimeException("Please specify ttf-file and output.bmp!");
		}

		Font font = Font.createFont(Font.TRUETYPE_FONT, new File(args[0]));
		Rectangle2D ee = maxSize(font, 16f);

		int sizew = (int) Math.floor(ee.getWidth());
		int sizeh = (int) Math.floor(ee.getHeight());

		BufferedImage bi = new BufferedImage(HORI * sizew, VERT * sizeh, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.setFont(font.deriveFont(sizeh * 1f));
		int i = 0;
		int maxw = 0;
		for (int x = 0; x < HORI; x++) {
			for (int y = 0; y < VERT; y++) {
				char[] chaar = new char[] { (char) i };
				g.drawChars(chaar, 0, 1, x * sizew, y * sizeh+g.getFontMetrics().getAscent());
				i++;
			}
		}
		ImageIO.write(bi, "bmp", new File(args[1]));
	}

	private static Rectangle2D maxSize(Font font, float size) {
		BufferedImage bi2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bi2.getGraphics();
		graphics.setFont(font.deriveFont(size));
		Rectangle2D ee = graphics.getFontMetrics().getMaxCharBounds(graphics);
		return ee;
	}
}
