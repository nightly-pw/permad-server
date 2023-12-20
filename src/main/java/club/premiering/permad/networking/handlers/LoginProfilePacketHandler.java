package club.premiering.permad.networking.handlers;

import club.premiering.permad.PermaServer;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.PacketHandler;
import club.premiering.permad.profile.ProfileException;
import club.premiering.permad.protocol.login.LoginProfilePacketIn;

public class LoginProfilePacketHandler implements PacketHandler<LoginProfilePacketIn> {
    @Override
    public boolean isSynchronousPacket() {
        return false;
    }

    @Override
    public void handle(GameSession session, LoginProfilePacketIn packet) {
        if (session.isLoggedIn() || session.player != null)
            return;

        try {
            session.profile = PermaServer.getServer().getProfileManager().loginProfile(packet.username, packet.password);
        } catch (ProfileException e) {
            e.printStackTrace();
        }
    }
}
