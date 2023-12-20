package club.premiering.permad.commands;

import club.premiering.permad.networking.GameSession;

public class ResCommand extends GameCommand {
    public ResCommand() {
        super(new String[] {"res", "revive"}, true);
    }

    @Override
    public void doCommand(GameSession session, String args) {
        session.player.respawnPlayer();
    }
}
