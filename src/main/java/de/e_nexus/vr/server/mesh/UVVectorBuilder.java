package de.e_nexus.vr.server.mesh;

public class UVVectorBuilder {
	private Float normalFront;
	private Float normalHeight;
	private Float normalRight;
	private float uvTop;
	private float uvLeft;
	private float front;
	private float height;
	private float right;

	public UVVectorBuilder position(float right, float height, float front) {
		this.right = right;
		this.height = height;
		this.front = front;
		return this;
	}

	public UVVectorBuilder uv(float left, float top) {
		uvLeft = left;
		uvTop = top;
		return this;
	}

	public UVVector build() {
		return new UVVector(right, height, front, uvLeft, uvTop, normalRight, normalHeight, normalFront);
	}

	public Float getNormalFront() {
		return normalFront;
	}

	public void setNormalFront(Float normalFront) {
		this.normalFront = normalFront;
	}

	public Float getNormalHeight() {
		return normalHeight;
	}

	public void setNormalHeight(Float normalHeight) {
		this.normalHeight = normalHeight;
	}

	public Float getNormalRight() {
		return normalRight;
	}

	public void setNormalRight(Float normalRight) {
		this.normalRight = normalRight;
	}

	public float getUvTop() {
		return uvTop;
	}

	public void setUvTop(float uvTop) {
		this.uvTop = uvTop;
	}

	public float getUvLeft() {
		return uvLeft;
	}

	public void setUvLeft(float uvLeft) {
		this.uvLeft = uvLeft;
	}

	public float getFront() {
		return front;
	}

	public void setFront(float front) {
		this.front = front;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getRight() {
		return right;
	}

	public void setRight(float right) {
		this.right = right;
	}
}
