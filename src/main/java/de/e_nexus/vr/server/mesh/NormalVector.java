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

	public NormalVector(float right, float up, float forward, Float normalRight, Float normalUp, Float normalForward) {
		super(right, up, forward);
		this.normalX = normalRight;
		this.normalY = normalUp;
		this.normalZ = normalForward;
	}

	public NormalVector(Vector defaultVector, Float normalX, Float normalY, Float normalZ) {
		this(defaultVector.x, defaultVector.y, defaultVector.z, normalX, normalY, normalZ);

	}

	@Override
	public NormalVector cloneRotateHorizontal(Vector aroundVector, double rotateHorizontalRadians) {
		float ny = (float) (Math.cos(rotateHorizontalRadians) * (aroundVector.y - y) - Math.sin(rotateHorizontalRadians) * z);
		float nz = (float) (Math.sin(rotateHorizontalRadians) * (aroundVector.y - y) + Math.cos(rotateHorizontalRadians) * z);
		return new NormalVector(x, ny, nz, normalX, normalY, normalZ);
	}

	@Override
	public NormalVector cloneRotateClockwise(Vector mid, double rad) {
		double nRight = Math.cos(rad) * (mid.x - x) - Math.sin(rad) * x;
		double nUp = Math.sin(rad) * (mid.z - z) + Math.cos(rad) * y;
		return new NormalVector((float) nRight, (float) nUp, z, normalX, normalY, normalZ);
	}
}
