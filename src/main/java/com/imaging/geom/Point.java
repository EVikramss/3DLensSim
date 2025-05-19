package com.imaging.geom;

/**
 * Represent a point in 3d space. All operations via methods are treated as immutable.
 */
public class Point {

	private float x;
	private float y;
	private float z;
	public static final Point ORIGIN = new Point(0.0f, 0.0f, 0.0f);

	public Point() {
	}

	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}

	public Point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void addX(float x) {
		this.x += x;
	}

	public void addY(float y) {
		this.y += y;
	}

	public void addZ(float z) {
		this.z += z;
	}
	
	public Point clone() {
		return new Point(this.x, this.y, this.z);
	}

	public Point add(Point p) {
		Point output = this.clone();
		output.x += p.x;
		output.y += p.y;
		output.z += p.z;
		return output;
	}

	public Point multiply(float factor) {
		Point output = this.clone();
		output.x *= factor;
		output.y *= factor;
		output.z *= factor;
		return output;
	}

	public Point divide(float factor) {
		Point output = this.clone();
		output.x /= factor;
		output.y /= factor;
		output.z /= factor;
		return output;
	}

	public Point add(float x, float y, float z) {
		Point output = this.clone();
		output.x += x;
		output.y += y;
		output.z += z;
		return output;
	}

	public Point subtract(float x, float y, float z) {
		Point output = this.clone();
		output.x -= x;
		output.y -= y;
		output.z -= z;
		return output;
	}

	public Point subtract(Point p) {
		Point output = this.clone();
		output.x -= p.x;
		output.y -= p.y;
		output.z -= p.z;
		return output;
	}

	public Point adjustWRT(Point position) {
		Point output = this.clone();
		output.x -= position.x;
		output.y -= position.y;
		output.z -= position.z;
		return output;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
