package club.premiering.permad.commands;

import club.premiering.permad.networking.GameSession;
import lombok.Getter;

public abstract class GameCommand {
    @Getter
    private final String[] commandNames;
    @Getter
    private final boolean permsOnly;

    public GameCommand(String[] commandNames, boolean permsOnly) {
        this.commandNames = commandNames;
        this.permsOnly = permsOnly;
    }

    public GameCommand(String[] commandNames) {
        this(commandNames, false);
    }

    public abstract void doCommand(GameSession session, String args);
}
