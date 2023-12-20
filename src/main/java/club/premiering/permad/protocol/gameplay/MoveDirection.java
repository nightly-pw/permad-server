package club.premiering.permad.protocol.gameplay;

import lombok.Getter;

public enum MoveDirection {
    W((byte) 1),
    A((byte) 2),
    S((byte) 3),
    D((byte) 4),
    SPRINT((byte) 5),
    CANCEL((byte) 6);

    public static MoveDirection getFromByte(byte b) {
        for (MoveDirection dir : values()) {
            if (dir.getId() == b)
                return dir;
        }
        return null;
    }

    @Getter
    private byte id;

    private MoveDirection(byte id) {
        this.id = id;
    }

}
