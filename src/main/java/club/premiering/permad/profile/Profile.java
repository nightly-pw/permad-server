package club.premiering.permad.profile;

import java.awt.*;
import java.util.UUID;

public class Profile {
    //Identifiers
    public String name;
    public UUID accountId;
    public byte[] passwordHash;//SHA-256

    //Game data
    public Color profileColor = Color.WHITE;

}
