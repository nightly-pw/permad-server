package club.premiering.permad.networking;

import club.premiering.permad.entity.EntityPlayer;
import club.premiering.permad.profile.Profile;
import club.premiering.permad.room.Room;
import club.premiering.permad.world.World;
import org.java_websocket.WebSocket;

// Represents a client connecting to the game server, and their state in the game server
public class GameSession {
    public Profile profile;
    public Room room;
    public World world;
    public EntityPlayer player;
    public WebSocket handle;

    public GameSession(WebSocket handle) {
        this.handle = handle;
    }

    public void setState(EntityPlayer player) {
        this.player = player;
        this.world = player.world;
    }

    public boolean isLoggedIn() {
        return this.profile == null;
    }

    public boolean isClosed() {
        return !this.canSend();
    }

    public boolean canSend() {
        return !this.handle.isClosed();
    }
}
