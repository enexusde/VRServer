package de.e_nexus.vr.server.mesh;

/**
 * A special kind of Vector. A point that have additional informations about the
 * position on a texture.
 * <p>
 * A texture is an digital image have only two dimensions. Starting at the upper
 * left corner and ending at the lower right coordinate (aka the last
 * coordinate).
 * 
 * <p>
 * As a rule of thumb, the coordinates for the texture are not allowed to be
 * smaller than 0. Another rule of thumb is that the coordinates are not allowed
 * to be higher than the size of the image, that means that the x-cooedinate is
 * not allowed to be higher than the width of the texture.
 * 
 * @see Vector
 *
 */
public class UVVector extends NormalVector {

	/**
	 * The x-coordinate on the texture. Never negative.
	 */
	protected final float uvX;
	/**
	 * The y-coordinate on the texture. Never negative.
	 */
	protected final float uvY;

	/**
	 * Creates a vector having texture mappings.
	 * 
	 * @param right          Meter to right. {@link Vector#x}
	 * @param height         Meter up from ground. {@link Vector#y}
	 * @param front          Meter in front of you. {@link Vector#z}
	 * @param uvFracFromLeft The x-factor of the left of the texture, never
	 *                       negative, range of 0-1.
	 * @param uvFracFromTop  The y-factor from the top of the texture, never
	 *                       negative, range of 0-1.
	 * @param normalRight    Normal vector to relative right in Meter.
	 * @param normalHeight   Normal vector to relative up in Meter.
	 * @param normalFront    Normal vector to relative front in Meter.
	 */
	public UVVector(float right, float height, float front, float uvFracFromLeft, float uvFracFromTop,
			Float normalRight, Float normalHeight, Float normalFront) {
		super(right, height, front, normalRight, normalHeight, normalFront);
		this.uvX = uvFracFromLeft;
		this.uvY = uvFracFromTop;
	}

	@Override
	public String toString() {
		return super.toString() + "[uv:" + uvX + "x" + uvY + "]";
	}

	public UVVector cloneScaled(float centerX, float centerY, float centerZ, float factor) {
		float distX = scale(centerX, x, factor);
		float distY = scale(centerY, y, factor);
		float distZ = scale(centerZ, z, factor);
		UVVector f = new UVVector(x + distX, y + distY, z + distZ, normalX, normalY, normalZ, uvX, uvY);
		return f;
	}

	public Vector cloneMoved(float x2, float y2, float z2) {
		return new UVVector(x + x2, y + y2, z + z2, normalX, normalY, normalZ, uvX, uvY);
	}
}
