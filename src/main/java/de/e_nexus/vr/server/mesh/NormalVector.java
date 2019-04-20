/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh;

public class NormalVector extends Vector {

	protected final Float normalX;

	protected final Float normalY;

	protected final Float normalZ;

	public NormalVector(float x, float y, float z, Float normalX, Float normalY, Float normalZ) {
		super(x, y, z);
		this.normalX = normalX;
		this.normalY = normalY;
		this.normalZ = normalZ;
	}
}
