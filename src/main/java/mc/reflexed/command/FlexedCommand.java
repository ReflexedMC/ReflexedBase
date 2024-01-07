package mc.reflexed.command;

import lombok.Getter;
import mc.reflexed.command.data.CommandInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class FlexedCommand extends Command {

    protected final CommandInfo info;

    protected final ICommandExecutor executor;

    protected FlexedCommand(ICommandExecutor executor) {
        super(executor.getClass().getAnnotation(CommandInfo.class).name(),
                executor.getClass().getAnnotation(CommandInfo.class).description(),
                executor.getClass().getAnnotation(CommandInfo.class).usage(),
                List.of(executor.getClass().getAnnotation(CommandInfo.class).aliases())
        );

        this.info = executor.getClass().getAnnotation(CommandInfo.class);
        this.executor = executor;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(info.permission().length() > 0 && !sender.hasPermission(info.permission())) {
            sender.sendMessage(info.noPermission());
            return false;
        }

        return executor.execute(sender, args, label);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        String[] ex = executor.tabComplete(sender, args, alias);

        if(ex.length == 0) {
            return super.tabComplete(sender, alias, args);
        }

        return List.of(ex);}
}