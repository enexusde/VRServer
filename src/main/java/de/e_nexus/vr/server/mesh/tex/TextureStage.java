/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh.tex;

public enum TextureStage {
	/**
	 * The natural appearance. A red texture will make the object appear red.
	 * <p>
	 * I am not sure about the difference to normal.
	 */
	DIFFUSE,
	/**
	 * The normals apperance. A blue texture will make the object appear blue.
	 * <p>
	 * I am not sure about the difference to diffuse.
	 */
	NORMALS,
	/**
	 * The lightmap. Does not effect the color of a mesh. It is for raytraced
	 * calculations, what part of the mesh is in shaddow and what part of the mesh
	 * is lighen-up because of light-sources like lamps or the sun.
	 */
	LIGHT, SHADER_CUSTOM3, SHADER_CUSTOM4, SHADER_CUSTOM5, SHADER_CUSTOM6, SHADER_CUSTOM7
}
