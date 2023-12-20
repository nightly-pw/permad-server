package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;
import club.premiering.permad.format.EntityMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EntityMoving extends Entity {
    @Getter
    private List<PathPoint> points = new ArrayList<>();
    private int movingEntType;
    private Entity childEntity;
    private int currentPointIndex = 0;
    private PathPoint lastPoint;
    private PathPoint currentPoint;
    private double pointProgress = 0d;

    @Override
    public void onAddedToWorld() {
        this.currentPoint = this.getNextPoint();
        this.lastPoint = points.get(0);

        this.childEntity = EntityRegistry.ENTITY_REGISTRY.create(this.movingEntType);
        this.world.addEntity(this.childEntity);
    }

    @Override
    public void doTick() {
        if (this.points.size() == 0) {
            return;
        }

        if (this.pointProgress > 1) {
            this.pointProgress = 0;
            this.getNextPoint();
        }
        this.pointProgress += (1 / this.lastPoint.point.dist(this.currentPoint.point)) * this.lastPoint.velocity;
        this.pos = this.lastPoint.point.clone().lerpTo(this.currentPoint.point, (float) this.pointProgress);
        this.childEntity.pos = this.pos.clone();
        this.childEntity.size = this.size.clone();
    }

    public PathPoint getNextPoint() {
        this.currentPointIndex++;
        if (this.currentPointIndex == this.points.size())
            this.currentPointIndex = 0;

        this.lastPoint = this.currentPoint;
        var cur = this.points.get(this.currentPointIndex);
        this.currentPoint = cur;
        return cur;
    }

    @Override
    public void readSerializedEntityMetadata(EntityMetadata metadata) {
        super.readSerializedEntityMetadata(metadata);

        try {
            var points = metadata.getJson().getAsJsonArray("points");
            this.movingEntType = metadata.getJson().get("entType").getAsInt();
            for (int i = 0; i < points.size(); i++) {
                var pObj = points.get(i).getAsJsonObject();
                var pointVec = metadata.getVector2(pObj);
                var vel = pObj.get("vel");
                var point = new PathPoint(pointVec, vel.getAsFloat());
                this.points.add(point);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Hard to find a suitable name, this will have to do
    @AllArgsConstructor
    static class PathPoint {
        public Vector2 point;
        public float velocity;
    }
}
