package club.premiering.permad.entity;

import lombok.Getter;

// Creates identifiers for entities
public class EntityId {
    private static int idCount = 0;

    public static EntityId createId() {// TODO: 8/8/2023 async checks?
        return new EntityId(idCount++);
    }

    @Getter
    private int id;

    public EntityId(int id) {
        this.id = id;
    }
}
