package com.imaging.geom;

public class Point2D {

	private float x;
	private float y;

	public Point2D() {
	}

	public Point2D(Point2D p) {
		this.x = p.x;
		this.y = p.y;
	}

	public Point2D(float x, float y) {
		this.x = x;
		this.y = y;
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

	public void addX(float x) {
		this.x += x;
	}

	public void addY(float y) {
		this.y += y;
	}

	public Point2D add(Point2D p) {
		this.x += p.x;
		this.y += p.y;
		return this;
	}

	public Point2D multiply(float factor) {
		this.x *= factor;
		this.y *= factor;
		return this;
	}

	public Point2D divide(float factor) {
		this.x /= factor;
		this.y /= factor;
		return this;
	}

	public Point2D add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Point2D subtract(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Point2D subtract(Point2D p) {
		this.x -= p.x;
		this.y -= p.y;
		return this;
	}

	public void adjustWRT(Point2D position) {
		this.x -= position.x;
		this.y -= position.y;
	}
}
