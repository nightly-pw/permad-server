package club.premiering.permad.math;

public class AABoundingBox {
    public float x, y, width, height;

    public AABoundingBox(Vector2 centerPos, Vector2 size) {
        this.x = centerPos.x - size.x / 2;
        this.y = centerPos.y - size.y / 2;
        this.width = size.x;
        this.height = size.y;
    }

    public Vector2 getCenter() {
        return new Vector2(x + width / 2, y + height / 2);
    }
}
