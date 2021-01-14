package de.e_nexus.vr.server.mesh;

import java.util.ArrayList;

public class UVNormalMeshBuilder {
	private Mesh<NormalVector> mesh = null;
	private ArrayList<Vector> points = null;

	public Mesh<NormalVector> buildAndReset() {
		checkOpenMesh();
		synchronized (this) {
			Mesh<NormalVector> result = this.mesh;
			this.mesh = null;
			this.points = null;
			return result;
		}
	}

	/**
	 * Add a square clockwise, all vectors are individuals for this face/plane.
	 * <table>
	 * <tr>
	 * <td>A</td>
	 * <td>--</td>
	 * <td>B</td>
	 * <tr>
	 * <td>|</td>
	 * <td>\</td>
	 * <td>|</td>
	 * <tr>
	 * <td>D</td>
	 * <td>--</td>
	 * <td>C</td>
	 * </table>
	 * 
	 * <p>
	 * The texture-coordinates are <br>
	 * <table>
	 * <tr>
	 * <th>Point</th>
	 * <th>X</th>
	 * <th>Y</th>
	 * </tr>
	 * <tr>
	 * <td>A</td>
	 * <td>0%</td>
	 * <td>0%
	 * </tr>
	 * <tr>
	 * <td>B</td>
	 * <td>100%</td>
	 * <td>0%
	 * </tr>
	 * <tr>
	 * <td>C</td>
	 * <td>100%</td>
	 * <td>100%
	 * </tr>
	 * <tr>
	 * <td>D</td>
	 * <td>0%</td>
	 * <td>100%
	 * </tr>
	 * </table>
	 * 
	 * 
	 * @return This instance.
	 */
	public UVNormalMeshBuilder addSquare(float rightA, float upA, float forwardA, float rightB, float upB, float forwardB, float rightC, float upC, float forwardC, float rightD, float upD,
			float forwardD) {
		checkOpenMesh();
		int a = mesh.addVector(new UVVector(rightA, upA, forwardA, 0f, 0f, 0f, 0f, 0f));
		int b = mesh.addVector(new UVVector(rightB, upB, forwardB, 1f, 0f, 0f, 0f, 0f));
		int c = mesh.addVector(new UVVector(rightC, upC, forwardC, 1f, 1f, 0f, 0f, 0f));
		int d = mesh.addVector(new UVVector(rightD, upD, forwardD, 0f, 1f, 0f, 0f, 0f));
		mesh.addSquareCounterClockwise(a, b, c, d);
		return this;

	}

	/**
	 * Add a square clockwise, all points are individual vectors for this
	 * face/plane.
	 * <table>
	 * <tr>
	 * <td>A</td>
	 * <td>--</td>
	 * <td>B</td>
	 * <tr>
	 * <td>|</td>
	 * <td>\</td>
	 * <td>|</td>
	 * <tr>
	 * <td>D</td>
	 * <td>--</td>
	 * <td>C</td>
	 * </table>
	 * 
	 * <p>
	 * The texture-coordinates are <br>
	 * <table>
	 * <tr>
	 * <th>Point</th>
	 * <th>X</th>
	 * <th>Y</th>
	 * </tr>
	 * <tr>
	 * <td>A</td>
	 * <td>0%</td>
	 * <td>0%
	 * </tr>
	 * <tr>
	 * <td>B</td>
	 * <td>100%</td>
	 * <td>0%
	 * </tr>
	 * <tr>
	 * <td>C</td>
	 * <td>100%</td>
	 * <td>100%
	 * </tr>
	 * <tr>
	 * <td>D</td>
	 * <td>0%</td>
	 * <td>100%
	 * </tr>
	 * </table>
	 * 
	 * 
	 * @return This instance.
	 */
	public UVNormalMeshBuilder addSquare(short a, short b, short c, short d) {
		Vector sa = points.get(a);
		Vector sb = points.get(b);
		Vector sc = points.get(c);
		Vector sd = points.get(d);
		return this.addSquare(sa.x, sa.y, sa.z, sb.x, sb.y, sb.z, sc.x, sc.y, sc.z, sd.x, sd.y, sd.z);
	}

	private void checkOpenMesh() {
		if (this.mesh != null) {
			return;
		}
		synchronized (this) {
			if (this.mesh == null) {
				this.mesh = new Mesh<>();
			}
		}
	}

	public short addPoint(float right, float up, float forward) {
		checkOpenPoints();
		Vector v = new Vector(right, up, forward);
		synchronized (this) {
			this.points.add(v);
			int lastPosNumber = this.points.size();
			int lastIndex = lastPosNumber - 1;
			return (short) lastIndex;
		}
	}

	private void checkOpenPoints() {
		if (this.points != null) {
			return;
		}
		synchronized (this) {
			if (this.points == null) {
				this.points = new ArrayList<Vector>();
			}
		}
	}

	/**
	 * Add a point to the right of a specific point.
	 * 
	 * @param offset        The offset-point from whom the distance to right should
	 *                      be, never <code>null</code>.
	 * @param lengthToRight The distance to right of the offset.
	 * @return The index of the new point.
	 */
	public short addPointToRight(short offset, float lengthToRight) {
		checkOpenPoints();
		Vector rel = points.get(offset);
		return addPoint(rel.x + lengthToRight, rel.y, rel.z);
	}

	/**
	 * Add a point straight below to a specific point.
	 * 
	 * @param offset      The offset-point from whom the distance downwards should
	 *                    be, never <code>null</code>.
	 * @param lengthBelow The distance straight downwards to the offset.
	 * @return The index of the new point.
	 */
	public short addPointBelow(short offset, float lengthBelow) {
		checkOpenPoints();
		Vector rel = points.get(offset);
		return addPoint(rel.x, rel.y - lengthBelow, rel.z);
	}

	/**
	 * Add a point straight left to a specific point.
	 * 
	 * @param offset       The offset-point from whom the distance leftwards should
	 *                     be, never <code>null</code>.
	 * @param lengthToLeft The distance leftwards to the offset.
	 * @return The index of the new point.
	 */
	public short addPointToLeft(short offset, float lengthToLeft) {
		checkOpenPoints();
		Vector rel = points.get(offset);
		return addPoint(rel.x - lengthToLeft, rel.y, rel.z);
	}

	/**
	 * Add a point straight backwards to a specific point.
	 * 
	 * @param offset       The offset-point from whom the distance backwards should
	 *                     be, never <code>null</code>.
	 * @param lengthToBack The distance backwards to the offset.
	 * @return The index of the new point.
	 */
	public short addPointBackwards(short offset, float lengthToBack) {
		checkOpenPoints();
		Vector rel = points.get(offset);
		return addPoint(rel.x, rel.y, rel.z - lengthToBack);
	}

	/**
	 * Add a point above a specific point.
	 * 
	 * @param offset        The offset-point from whom the distance upwards should
	 *                      be, never <code>null</code>.
	 * @param lengthUpwards The distance upwards to the offset.
	 * @return The index of the new point.
	 */
	public short addPointAbove(short offset, float lengthUpwards) {
		return addPointBelow(offset, -lengthUpwards);
	}

	/**
	 * Add a point straight forwards to a specific point.
	 * 
	 * @param offset        The offset-point from whom the distance forward should
	 *                      be, never <code>null</code>.
	 * @param lengthToFront The distance forwards to the offset.
	 * @return The index of the new point.
	 */
	public short addPointForwards(short offset, float lengthToFront) {
		return addPointBackwards(offset, -lengthToFront);
	}
}
