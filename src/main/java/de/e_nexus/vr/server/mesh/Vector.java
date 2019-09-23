/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh;

/**
 * A vector represents one point that is absolute by default in a three
 * dimensional world.
 * 
 * <p>
 * Althou a value of 0.1 is a rounding result of an range of <code>0.06</code>
 * to <code>0.14999999...</code> , we assume that the value that is closest to 0
 * is meant.
 * 
 * <p>
 * The coordinates gives are assumed to be in metric system of 1 meter.
 * 
 * <p>
 * All coordinates origins from the floor's starting position. So a x0,y0,z0
 * coordinate centers at your foot in VR if you stand upright.
 * 
 * @author Guest
 *
 */
public class Vector {
	/**
	 * The x-coordiante of the point. If it is a positive value it is a point to the
	 * right of you. If it is a negative value, its to the left.
	 */
	protected final float x;
	/**
	 * The y-coordinate of the point. Its the vertical coordinate. Positive values
	 * are above the ground, negative values are below the floor.
	 */
	protected final float y;
	/**
	 * The z-coordiate of the point. Its indicating how many meters you have to move
	 * forward to be on the same z-coordinate as the point. Negative values
	 * indicating that you have to move backward to touch the point.
	 */
	protected final float z;

	/**
	 * 
	 * @param right   Meter to the side.
	 * @param up      Meter from ground up to the point. Negative values may under
	 *                the floor.
	 * @param forward Meter forward.
	 */
	public Vector(float right, float up, float forward) {
		this.x = right;
		this.y = up;
		this.z = forward;
	}

	/**
	 * Returns the distance (absolute by default to the starting position) to the
	 * right in meter. Negative values are values to the left (absolute by default
	 * to the starting position).
	 * 
	 * @return Relative position to the right in meter.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Returns the distance (absolute by default to the starting position) from the
	 * floor in meter. Negative values are values below the floor (absolute by
	 * default to the starting position).
	 * 
	 * @return Relative position from the ground in meter.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Returns the distance (absolute by default to the starting position) in front
	 * of you in meter. Negative values are values that indicates a position behind
	 * you (absolute by default to the starting position).
	 * 
	 * @return Relative position in front of you in meter.
	 */
	public float getZ() {
		return z;
	}

	@Override
	public String toString() {
		return super.toString() + "[x:" + x + ", y:" + y + ", z:" + z + "]";
	}

	public Vector cloneScaled(float centerX, float centerY, float centerZ, float factor) {
		// if x =-200 and centerX=100 and factor = 0.5 then x must be 150 because
		// (200-100)*0.5=150

		float distX = scale(centerX, x, factor);
		float distY = scale(centerY, y, factor);
		float distZ = scale(centerZ, z, factor);

		return new Vector(x + distX, y + distY, z + distZ);
	}

	protected static float scale(float p, float o, float factor) {
		if (p == o) {
			return p;
		}
		float dist = Math.abs(p - o) * factor;
		if (p < o) {
			return p + dist;
		} else {
			return p - dist;
		}
	}

	public Vector cloneMoved(float x2, float y2, float z2) {
		return new Vector(x + x2, y + y2, z + z2);
	}

	public Vector cloneRotateHorizontal(Vector aroundVector, double rotateHorizontalRadians) {
		float ny = (float) (Math.cos(rotateHorizontalRadians) * (aroundVector.y - y)
				- Math.sin(rotateHorizontalRadians) * z);
		float nz = (float) (Math.sin(rotateHorizontalRadians) * (aroundVector.y - y)
				+ Math.cos(rotateHorizontalRadians) * z);
		return new Vector(x, ny, nz);
	}

	public Vector cloneRotateClockwise(Vector mid, double rad) {
		double nRight = Math.cos(rad) * (mid.x - x) - Math.sin(rad) * x;
		double nUp = Math.sin(rad) * (mid.z - z) + Math.cos(rad) * y;
		return new Vector((float) nRight, (float) nUp, z);
	}
}
