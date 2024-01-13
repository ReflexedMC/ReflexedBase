package mc.reflexed.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public class CommandManager {

    private final List<FlexedCommand> commands = new ArrayList<>();
    private final Plugin plugin;

    public CommandManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(ICommandExecutor... commands) {
        Arrays.stream(commands).forEach(this::register);
    }

    public void register(ICommandExecutor command) {
        SimpleCommandMap commandMap = getCommandMap();

        FlexedCommand cmd = new FlexedCommand(command);
        commandMap.register(cmd.getInfo().fallback(), cmd);
        commands.add(cmd);

        reloadAllCommands();
    }

    public void unregister(FlexedCommand command) {
        SimpleCommandMap commandMap = getCommandMap();
        Map<String, Command> knownCommands = commandMap.getKnownCommands();

        knownCommands.remove(command.getInfo().name());
        knownCommands.remove(String.format("%s:%s", command.getInfo().fallback(), command.getInfo().name()));

        command.unregister(commandMap);
        commands.remove(command);

        reloadAllCommands();
    }

    public void onDisable() {
        new ArrayList<>(this.commands).forEach(this::unregister);
    }

    public SimpleCommandMap getCommandMap() {
        try {
            Field field = plugin.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);

            return (SimpleCommandMap) field.get(plugin.getServer());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadAllCommands() {
        try {
            Class<?> craftServer = Class.forName(String.format("org.bukkit.craftbukkit.%s.CraftServer", plugin.getServer().getClass().getPackage().getName().split("\\.")[3]));

            Method syncCommandsMethod = craftServer.getDeclaredMethod("syncCommands");
            syncCommandsMethod.setAccessible(true);
            syncCommandsMethod.invoke(plugin.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FlexedCommand getCommandFromExecutor(ICommandExecutor executor) {
        return commands.stream().filter(cmd -> cmd.getExecutor().equals(executor)).findFirst().orElse(null);
    }

}
