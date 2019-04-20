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
 * @param <T>
 */
public class Mesh<T extends Vector> extends TexturesHolder {

	protected final List<T> vectors = new ArrayList<T>();
	protected final Set<Triangle> polygons = new LinkedHashSet<Triangle>();

	/**
	 * Add an 3 dimensional point (aka vector or vertex) to the Mesh.
	 * <p>
	 * You need at least 3 points for an {@link Triangle triangle}. Notice that a
	 * minimum of 3 points is required to paint an object in 3D.
	 * 
	 * @param v
	 * @return
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
	 * @param x The relative move to the right in meter.
	 * @param y The relative move upwards in meter.
	 * @param z The relative move forward in meter.
	 * @return The cloned and moved mesh.
	 */
	public Mesh<T> cloneMoved(float x, float y, float z) {
		Mesh<T> m = new Mesh<T>();
		for (T t : vectors) {
			m.addVector((T) t.cloneMoved(x, y, z));
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
		int a = addVector((T) new UVVector(right - width, height + up, forward + width, 0f, 0f, -1f, 1f, 1f));
		int b = addVector((T) new UVVector(right + width, height + up, forward + width, 1f, 0f, 1f, 1f, 1f));
		int c = addVector((T) new UVVector(right + width, height + up, forward - width, 1f, 1f, 1f, 1f, -1f));
		int d = addVector((T) new UVVector(right - width, height + up, forward - width, 0f, 1f, -1f, 1f, -1f));

		int e = addVector((T) new UVVector(right - width, up - height, forward + width, 0f, 0f, -1f, -1f, 1f));
		int f = addVector((T) new UVVector(right + width, up - height, forward + width, 1f, 0f, 1f, -1f, 1f));
		int g = addVector((T) new UVVector(right + width, up - height, forward - width, 1f, 1f, 1f, -1f, -1f));
		int h = addVector((T) new UVVector(right - width, up - height, forward - width, 0f, 1f, -1f, -1f, -1f));
		addSquareClockwise(a, b, c, d);
		addSquareCounterClockwise(e, f, g, h);
		addSquareClockwise(d, c, g, h);
		addSquareClockwise(a, d, h, e);
		addSquareClockwise(c, b, f, g);
		addSquareCounterClockwise(a, b, f, e);
	}

}
