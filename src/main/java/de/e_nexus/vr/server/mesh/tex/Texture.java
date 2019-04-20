/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh.tex;

import java.io.InputStream;

/**
 * Represents a texture. A texture is a two dimensional representation of a
 * image used to change the apperance of an triangle. There are four channels:
 * <ul>
 * <li>Red
 * <li>Green
 * <li>Blue
 * <li>Alpha
 * </ul>
 * All values are in byte-range from 0 to 255. A <code>0</code> value means for
 * any of the color that the color must not be shown (if you have a 0red, 0green
 * and 255 blue you have a non-controversial blue pixel). Whereat a alpha of
 * <code>255</code> is compleatly opaque.
 * 
 * <p>
 * As a rule of thumb, the size of the array of byte is
 * <code>4*width*height</code>.
 */
public interface Texture {
	/**
	 * The client's id of the texture loaded. If the id is <code>null</code> the
	 * texture is not load! Vice versa: if the id is not <code>null</code>, the
	 * texture is compleatly load by the client.
	 * 
	 * @return
	 */
	Integer getId();

	/**
	 * The width of the texture. Never negative, never <code>0</code>.
	 * 
	 * @return The width (horizontal dimension) of the texture.
	 */
	int getWidth();

	/**
	 * The height of the texture. Never negative, never <code>0</code>.
	 * 
	 * @return The height (vertical dimension) of the texture.
	 */
	int getHeight();

	/**
	 * The uncompressed data having blocks of 4 bytes (rgba).
	 * 
	 * <p>
	 * The stream is uncompressed and must support 4*width*height bytes. After
	 * stream-end no bytes is allowed to be available anymore.
	 * 
	 * @return Uncompressed texture data.
	 */
	InputStream createDataStream();

	/**
	 * Should only be used by {@link MeshTextureInfoInputStream}.
	 * 
	 * @param id
	 */
	void setId(int id);
}
