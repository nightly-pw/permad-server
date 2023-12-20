package club.premiering.permad.commands;

import club.premiering.permad.networking.GameSession;

public interface CommandManager {
    void registerCommand(GameCommand command);
    void handleCommand(GameSession session, String message);
}
