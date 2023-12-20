package club.premiering.permad.commands;

import club.premiering.permad.networking.GameSession;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class CommandManagerImpl implements CommandManager {
    @Getter
    private Set<GameCommand> registeredCommands = new HashSet<>();

    @Override
    public void registerCommand(GameCommand command) {
        for (var name : command.getCommandNames()) {
            if (name.contains(" "))
                throw new IllegalArgumentException(command + ": " + "Command names must not contain spaces!");
        }

        this.registeredCommands.add(command);
    }

    @Override
    public void handleCommand(GameSession session, String message) {
        for (var command : this.registeredCommands) {
            for (var name : command.getCommandNames()) {
                if (message.toLowerCase().startsWith("/" + name.toLowerCase())) {
                    //Found the command, get the args and handle the command
                    // TODO: 12/11/2023 Perms check
                    int substringAmount = (message.length() >= (2 + name.length())) ? 2 : 1;
                    String args = message.substring((substringAmount) + name.length());
                    command.doCommand(session, args);
                    return;
                }
            }
        }
        session.player.sendMessage("Command not found!");
    }
}
