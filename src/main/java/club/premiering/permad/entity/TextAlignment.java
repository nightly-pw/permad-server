package club.premiering.permad.entity;

public enum TextAlignment {
    LEFT, CENTER, RIGHT;

    public byte toByte() {
        for (byte i = 0; i < TextAlignment.values().length; i++) {
            if (TextAlignment.values()[i] == this)
                return i;
        }
        return 0;
    }

    public static TextAlignment fromByte(byte b) {
        return TextAlignment.values()[b];
    }
}
