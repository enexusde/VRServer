/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.e_nexus.vr.server.mesh.tex.BufferedImageTexture;

public class TextureTools {
	private static final int DEFAULT_TEXTURE_TYPE = BufferedImage.TYPE_INT_ARGB;

	/**
	 * Constructs a texture that have a specific text as content.
	 * 
	 * @param text         The text, never <code>null</code>.
	 * @param color        The forground color, never <code>null</code>.
	 * @param background   The background color, never <code>null</code> should be
	 *                     different from the color.
	 * @param defaultValue The default value if there where problems creating the
	 *                     image.
	 * @return The unbound texture.
	 */
	public static BufferedImageTexture fromText(String text, Color color, Color background,
			BufferedImageTexture defaultValue) {
		BufferedImage bi = new BufferedImage(1, 1, DEFAULT_TEXTURE_TYPE);
		Graphics g = bi.getGraphics();
		Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
		int height = (int) Math.round(Math.ceil(rect.getHeight()));
		int width = (int) Math.round(Math.ceil(rect.getWidth()));
		bi = new BufferedImage(width, height, DEFAULT_TEXTURE_TYPE);
		g = bi.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		g.setColor(color);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, 0, fm.getAscent());
		return new BufferedImageTexture(bi);
	}
}
