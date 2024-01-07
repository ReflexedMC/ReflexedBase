package mc.reflexed.command;

import org.bukkit.command.CommandSender;

public interface ICommandExecutor {
    boolean execute(CommandSender sender, String[] args, String label);

    default String[] tabComplete(CommandSender sender, String[] args, String label) {
        return new String[0];
    }
}
