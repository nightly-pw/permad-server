package club.premiering.permad.commands;

import club.premiering.permad.networking.GameSession;

public class GodCommand extends GameCommand {
    public GodCommand() {
        super(new String[]{"nc", "noclip", "god"}, true);
    }

    @Override
    public void doCommand(GameSession session, String args) {
        session.player.setGod(!session.player.isGod());
    }
}
