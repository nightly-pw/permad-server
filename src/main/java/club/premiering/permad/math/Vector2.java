package club.premiering.permad.math;

public class Vector2 implements Cloneable {
    public float x, y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Vector2() {
        this(0, 0);
    }

    public float dist(Vector2 point) {
        return Math.abs(this.x - point.x) + Math.abs(this.y - point.y);
    }

    public Vector2 distVec(Vector2 b) {
        return new Vector2(b.x - this.x, b.y - this.y);
    }

    public Vector2 rotate(float angle, Vector2 rotationAxis) {
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);

        this.sub(rotationAxis);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        this.add(rotationAxis);
        return this;
    }
    
    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 add(Vector2 vec) {
        return this.add(vec.x, vec.y);
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 mul(float x) {
        this.mul(x, x);
        return this;
    }

    public Vector2 mul(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2 divide(float x) {
        return this.divide(x, x);
    }

    public Vector2 divide(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vector2 lerpTo(Vector2 destination, float o) {
        o = MathUtil.clamp(o, 0, 1);//Pass by value with primitives, so we're fine (at least I think)
        this.x += (destination.x - this.x) * o;
        this.y += (destination.y - this.y) * o;
        return this;
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public Vector2 clone() {
        try {
            Vector2 clone = (Vector2) super.clone();
            clone.x = this.x;
            clone.y = this.y;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
