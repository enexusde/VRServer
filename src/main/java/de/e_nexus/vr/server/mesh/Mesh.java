/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.e_nexus.vr.server.mesh.tex.TextureStage;
import de.e_nexus.vr.server.mesh.tex.TexturesHolder;

/**
 * An Mesh represents an solid object in 3d. A {@link Mesh} is a collection of
 * points. A edge is a strong relation between exactly two points (also known as
 * line in a wireframe model). A triangle (a polygon of 3 points) is a strong
 * relation between three points and this is the first representation that is
 * visible in 3d (usually a white-filled triangle)
 * 
 * <p>
 * The word polygon or polygons is used and constantly suggest forms having more
 * than 3 vertices. But any object in 3d is constructed by the minimal form of
 * an polygon: the triangle. By assembling of multiple triangle every polygon
 * can be represented in 3d. So even if the word polygon is used always are
 * triangles meant.
 * 
 * <p>
 * A mesh can have normals. Normals are coordinates of optimal lighting
 * direction. A normal is a technical requirement for a vertex in order to give
 * the object a depth effect. As a rule of thumb, the effect of a normal is only
 * visible having a connected triangle. <b>Notice:</b>The normals-effect is
 * either completely active or completely inactive. Only if every single vertex
 * in a mesh have normal informations the normal informations of all other
 * vertices are respected and visible in triangles.
 * 
 * <p>
 * A mesh can also hold a texture and texture mapping informations. How the
 * texture is folded around the mesh is defined per vertex by using x and y
 * coordinates starting from the left upper corner of the texture. By the smart
 * placement of x and y coordinates the direction(upward, downward and/or
 * flipped) of textures is done.
 * <p>
 * <b>Notice:</b> Every closed 3d object (starting with the tetraeder - the most
 * primitive one) there is a texture-mapping problem! The texture who folds
 * around the object will have at least 3 ambiguous texture coordinates that
 * represents the same vertex! This can technically not be solved but we can
 * have two additional vertices at the same location to solve the uniqueness of
 * coordinates in a tetraeder.
 * 
 * @param <T> The type of vectors a support.
 */
public class Mesh<T extends Vector> extends TexturesHolder {

	/**
	 * The vectors representing the mesh.
	 */
	protected final List<T> vectors = new ArrayList<T>();

	/**
	 * The polygons in the mesh.
	 */
	protected final Set<Triangle> polygons = new LinkedHashSet<Triangle>();

	/**
	 * Add an 3 dimensional point (aka vector or vertex) to the Mesh.
	 * <p>
	 * You need at least 3 points for an {@link Triangle triangle}. Notice that a
	 * minimum of 3 points is required to paint an object in 3D.
	 * 
	 * @param v The vector to add, never <code>null</code>.
	 * @return The index the vector is placed in the mesh, never negative.
	 */
	public int addVector(T v) {
		vectors.add(v);
		return vectors.size() - 1;
	}

	/**
	 * Add a triangle in counter-clockwise (CCW) order.
	 * <p>
	 * The values of p1, p2 and p3 must contain different values.
	 * 
	 * @param p1 First point of the triangle, never negative.
	 * @param p2 Second point in counter-clockwise order, never negative.
	 * @param p3 Third point in counter-clockwise order, never negative.
	 * @return The current mesh, not a copy.
	 */
	public Mesh<T> addTriangleCounterClockwise(int p1, int p2, int p3) {
		assert p1 <= vectors.size() || p1 >= 0 : "Vector p1 (" + p1 + ") out of range (0 - " + vectors.size() + ")!";
		assert p2 <= vectors.size() || p2 >= 0 : "Vector p2 (" + p2 + ") out of range (0 - " + vectors.size() + ")!";
		assert p3 <= vectors.size() || p3 >= 0 : "Vector p3 (" + p3 + ") out of range (0 - " + vectors.size() + ")!";
		Triangle triangle = new Triangle(p1, p2, p3);
		addTriangle(triangle);
		return this;
	}

	/**
	 * Adds an triangle to the mesh.
	 * 
	 * @param triangle The triangle to use.
	 */
	public void addTriangle(Triangle triangle) {
		polygons.add(triangle);
	}

	/**
	 * Adds a square (containing two triangles) in anti-clockwise order.
	 * <table>
	 * <caption>Model:</caption>
	 * <tr>
	 * <td>c</td>
	 * <td>-</td>
	 * <td>b</td>
	 * </tr>
	 * <tr>
	 * <td>|</td>
	 * <td>\</td>
	 * <td>|</td>
	 * </tr>
	 * <tr>
	 * <td>d</td>
	 * <td>-</td>
	 * <td>a</td>
	 * </tr>
	 * </table>
	 * 
	 * @param a The first point, never negative.
	 * @param d The second point, never negative.
	 * @param c The third point, never negative.
	 * @param b The fourth point, never negative.
	 * @return The mesh itself, not a copy.
	 */
	public Mesh<T> addSquareClockwise(int a, int d, int c, int b) {
		addTriangleCounterClockwise(a, c, d);
		addTriangleCounterClockwise(a, b, c);
		return this;
	}

	/**
	 * @see #addSquareClockwise(int, int, int, int)
	 */
	public Mesh<T> addSquareClockwiseDoublesided(int a, int d, int c, int b) {
		addTriangleCounterClockwise(a, c, d);
		addTriangleCounterClockwise(a, b, c);
		return this;
	}

	/**
	 * Clones the current mesh to a specific scale. Does not clone the texture.
	 * 
	 * @param centerX The x-center of where to scale from.
	 * @param centerY The y-center of where to scale from.
	 * @param centerZ The z-center of where to scale from.
	 * @param factor  The factor of scaling.
	 * @return The cloned mesh without cloned textures, never <code>null</code>.
	 */
	public Mesh<T> cloneScaled(float centerX, float centerY, float centerZ, float factor) {
		Mesh<T> m = new Mesh<T>();
		for (T t : vectors) {
			m.addVector((T) t.cloneScaled(centerX, centerY, centerZ, factor));
		}
		for (Triangle triangle : polygons) {
			m.addTriangle(triangle);
		}
		for (TextureStage stage : textures.keySet()) {
			m.setTexture(stage, textures.get(stage));
		}
		return m;
	}

	/**
	 * Add a square by 4 points. Does not correct the cutting edge.
	 * 
	 * @param a The index of the first vector in the mesh.
	 * @param b The index of the second vector in the mesh.
	 * @param c The index of the third vector in the mesh.
	 * @param d The index of the fourth vector in the mesh.
	 */
	public void addSquareCounterClockwise(int a, int b, int c, int d) {
		addSquareClockwise(d, c, b, a);
	}

	/**
	 * Clone and moves the mesh to a new mesh. The texture is not cloned but
	 * referenced in the cloned mesh.
	 * 
	 * @param rightMoveM   The relative move to the right in meter, may be negative
	 *                     in order to move left.
	 * @param upwardMoveM  The relative move upwards in meter, may be negative in
	 *                     order to move downwards.
	 * @param forwardMoveM The relative move forward in meter, may be negative in
	 *                     order to move backwards.
	 * @return The cloned and moved mesh.
	 */
	public Mesh<T> cloneMoved(float rightMoveM, float upwardMoveM, float forwardMoveM) {
		Mesh<T> m = new Mesh<T>();
		for (T t : vectors) {
			m.addVector((T) t.cloneMoved(rightMoveM, upwardMoveM, forwardMoveM));
		}
		for (Triangle triangle : polygons) {
			m.addTriangle(triangle);
		}
		for (TextureStage stage : textures.keySet()) {
			m.setTexture(stage, textures.get(stage));
		}
		return m;
	}

	/**
	 * Clone and rotate the mesh around a specific point.
	 * 
	 * @param aroundPoint             Point to rotate around, never
	 *                                <code>null</code>.
	 * @param rotateHorizontalRadians Radians to rotate.
	 * @return
	 */

	public Mesh<T> cloneRotateHorizontal(Vector aroundPoint, double rotateHorizontalRadians) {
		Mesh<T> m = new Mesh<T>();
		for (T t : vectors) {
			m.addVector((T) t.cloneRotateHorizontal(aroundPoint, rotateHorizontalRadians));
		}
		for (Triangle triangle : polygons) {
			m.addTriangle(triangle);
		}
		for (TextureStage stage : textures.keySet()) {
			m.setTexture(stage, textures.get(stage));
		}
		return m;

	}

	/**
	 * Adds an cube using conclusive normal mappings over the complete size of the
	 * texture.
	 * <p>
	 * Using negative values for width and/or height may cause display problems.
	 * 
	 * @param right   The distance to the right in meter.
	 * @param up      The distance upwards in meter.
	 * @param forward The distance forward in meter.
	 * @param width   The width of the cube in meter, the distance from the
	 *                most-left side of the block to the most-right side of the
	 *                block.
	 * @param height  The height of the cube in meter.
	 */
	public void addCube(float right, float up, float forward, float width, float height) {
		width = width / 2;
		height = height / 2;

		float p8x = right - width;
		float p8y = up - height;
		float p8z = forward - width;

		float p5x = p8x;
		float p5y = p8y;
		float p5z = forward + width;

		float p7x = right + width;
		float p7y = p5y;
		float p7z = p5z;

		float p6x = p7x;
		float p6y = p8y;
		float p6z = p8z;

		float p1x = right - width;
		float p1y = up + height;
		float p1z = forward - width;

		float p2x = p1x;
		float p2y = p1y;
		float p2z = forward + width;

		float p3x = right + width;
		float p3y = p2y;
		float p3z = p2z;

		float p4x = p3x;
		float p4y = p1y;
		float p4z = p1z;

		Vector p1 = new Vector(p1x, p1y, p1z);
		Vector p2 = new Vector(p2x, p2y, p2z);
		Vector p3 = new Vector(p3x, p3y, p3z);
		Vector p4 = new Vector(p4x, p4y, p4z);
		Vector p5 = new Vector(p5x, p5y, p5z);
		Vector p6 = new Vector(p6x, p6y, p6z);
		Vector p7 = new Vector(p7x, p7y, p7z);
		Vector p8 = new Vector(p8x, p8y, p8z);

		// add A
		addSquareClockwise(addVector((T) new UVVector(new NormalVector(p1, 0f, 0f, 0f), 0f, 1f)), //
				addVector((T) new UVVector(new NormalVector(p2, 0f, 0f, 0f), 0f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p3, 0f, 0f, 0f), 1f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p4, 0f, 0f, 0f), 1f, 1f)));

		// add F
		addSquareClockwise(addVector((T) new UVVector(new NormalVector(p1, 0f, 0f, 0f), 0f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p4, 0f, 0f, 0f), 1f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p6, 0f, 0f, 0f), 1f, 1f)), //
				addVector((T) new UVVector(new NormalVector(p8, 0f, 0f, 0f), 0f, 1f)));
		// add B
		addSquareClockwise(addVector((T) new UVVector(new NormalVector(p4, 0f, 0f, 0f), 0f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p3, 0f, 0f, 0f), 1f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p7, 0f, 0f, 0f), 1f, 1f)), //
				addVector((T) new UVVector(new NormalVector(p6, 0f, 0f, 0f), 0f, 1f)));
		// add D
		addSquareClockwise(addVector((T) new UVVector(new NormalVector(p2, 0f, 0f, 0f), 0f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p1, 0f, 0f, 0f), 1f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p8, 0f, 0f, 0f), 1f, 1f)), //
				addVector((T) new UVVector(new NormalVector(p5, 0f, 0f, 0f), 0f, 1f)));
		// add E
		addSquareClockwise(addVector((T) new UVVector(new NormalVector(p3, 0f, 0f, 0f), 0f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p2, 0f, 0f, 0f), 1f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p5, 0f, 0f, 0f), 1f, 1f)), //
				addVector((T) new UVVector(new NormalVector(p7, 0f, 0f, 0f), 0f, 1f)));
		// add C
		addSquareClockwise(addVector((T) new UVVector(new NormalVector(p7, 0f, 0f, 0f), 0f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p5, 0f, 0f, 0f), 1f, 0f)), //
				addVector((T) new UVVector(new NormalVector(p8, 0f, 0f, 0f), 1f, 1f)), //
				addVector((T) new UVVector(new NormalVector(p6, 0f, 0f, 0f), 0f, 1f)));

	}

	public Mesh<T> cloneRotateClockwise(Vector aroundPoint, double rotateHorizontalRadians) {
		Mesh<T> m = new Mesh<T>();
		for (T t : vectors) {
			m.addVector((T) t.cloneRotateClockwise(aroundPoint, rotateHorizontalRadians));
		}
		for (Triangle triangle : polygons) {
			m.addTriangle(triangle);
		}
		for (TextureStage stage : textures.keySet()) {
			m.setTexture(stage, textures.get(stage));
		}
		return m;

	}
}
