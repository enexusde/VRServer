/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh.tex;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds the textures for every layer.
 */
public class TexturesHolder {
	/**
	 * The textures bound.
	 */
	protected final Map<TextureStage, Texture> textures = new LinkedHashMap<TextureStage, Texture>();

	/**
	 * Set a texture (bound or unbound in the VR client). Override the texture if
	 * exists.
	 * 
	 * @param stage   The supported stage, never <code>null</code>.
	 * @param texture The texture.
	 */
	public void setTexture(TextureStage stage, Texture texture) {
		textures.put(stage, texture);
	}
}
