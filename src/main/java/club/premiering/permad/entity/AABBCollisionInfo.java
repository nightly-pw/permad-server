package club.premiering.permad.entity;

import lombok.AllArgsConstructor;

// Part of the weird hacky physics system
@AllArgsConstructor
public class AABBCollisionInfo {
    public boolean leftFace, topFace, rightFace, bottomFace;

    public AABBCollisionInfo invert() {
        if (leftFace) {
            leftFace = false;
            rightFace = true;
        }
        if (rightFace) {
            leftFace = true;
            rightFace = false;
        }
        if (topFace) {
            topFace = false;
            bottomFace = true;
        }
        if (bottomFace) {
            bottomFace = false;
            topFace = true;
        }
        return this;
    }

    public AABBCollisionInfo copy() {
        return new AABBCollisionInfo(leftFace, topFace, rightFace, bottomFace);
    }
}
