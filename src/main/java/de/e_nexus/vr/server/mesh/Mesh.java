package de.e_nexus.vr.server.mesh;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.e_nexus.vr.server.mesh.tex.Texture;
import de.e_nexus.vr.server.mesh.tex.TextureStage;
import de.e_nexus.vr.server.mesh.tex.TexturesHolder;
import de.e_nexus.vr.server.mesh.tex.URLTexture;

public class Mesh<T extends Vector> extends TexturesHolder {

	protected final List<T> vectors = new ArrayList<T>();
	protected final Set<Triangle> polygons = new LinkedHashSet<Triangle>();

	public int addVector(T v) {
		vectors.add(v);
		return vectors.size() - 1;
	}

	/**
	 * You can only see triangles that is build anti-clockwise from your pov.
	 * 
	 * @param p1 First Index.
	 * @param p2 Second Index.
	 * @param p3 Third Index.
	 * @return
	 */
	public Mesh<T> addTriangleCounterClockwise(int p1, int p2, int p3) {
		assert p1 <= vectors.size() || p1 >= 0 : "Vector p1 (" + p1 + ") out of range (0 - " + vectors.size() + ")!";
		assert p2 <= vectors.size() || p2 >= 0 : "Vector p2 (" + p2 + ") out of range (0 - " + vectors.size() + ")!";
		assert p3 <= vectors.size() || p3 >= 0 : "Vector p3 (" + p3 + ") out of range (0 - " + vectors.size() + ")!";
		Triangle triangle = new Triangle(p1, p2, p3);
		addTriangle(triangle);
		return this;
	}

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
	 * @param a
	 * @param d
	 * @param c
	 * @param b
	 */
	public Mesh<T> addSquareClockwise(int a, int d, int c, int b) {
		addTriangleCounterClockwise(a, c, d);
		addTriangleCounterClockwise(a, b, c);
		return this;

	}

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

	public void addSquareCounterClockwise(int a, int b, int c, int d) {
		addSquareClockwise(d, c, b, a);
	}

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

	public void addBlock(float right, float up, float forward, float width, float height) {
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
