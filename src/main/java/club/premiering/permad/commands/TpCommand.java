package club.premiering.permad.commands;

import club.premiering.permad.networking.GameSession;

public class TpCommand extends GameCommand {
    public TpCommand() {
        super(new String[] {"tp", "teleport"}, true);
    }

    @Override
    public void doCommand(GameSession session, String args) {
        var world = session.room.getWorldByName(args, true);
        if (world != null) {
            session.room.switchPlayerWorld(session.player, world);
            session.player.sendMessage("Teleported to " + args);
        } else {
            session.player.sendMessage(String.format("Could not find world with name \"%s\"", args));
        }
    }
}
