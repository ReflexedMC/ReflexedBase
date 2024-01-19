package mc.reflexed.event;

import lombok.Getter;
import mc.reflexed.event.data.EventInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EventManager {

    private final FlexedListener listener = new FlexedListener();
    private final List<EventSender> senders = new ArrayList<>();

    private final Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @SuppressWarnings("unchecked")
    public void register(Object obj) {
        for(Method method : obj.getClass().getDeclaredMethods()) {
            if(!method.isAnnotationPresent(EventInfo.class) || method.getParameterCount() != 1) continue;

            EventInfo info = method.getAnnotation(EventInfo.class);

            Class<?> parameter = method.getParameterTypes()[0];

            if(parameter.isAssignableFrom(Event.class)) continue;

            Class<? extends Event> clazz = (Class<? extends Event>) method.getParameterTypes()[0];

            EventSender matchedSender = getMatchingSender(clazz, method);

            if(matchedSender != null && !matchedSender.isRegistered()) {
                senders.remove(matchedSender);
                senders.add(new EventSender(clazz, method, obj));
                continue;
            }

            EventSender sender = new EventSender(clazz, method, obj);
            senders.add(sender);

            plugin.getServer().getPluginManager().registerEvent(clazz, listener, info.priority(), ((listener, e) -> sender.execute(e)), plugin);
        }
    }

    @SuppressWarnings("unchecked")
    public void register(Object obj, Player... players) {
        for(Method method : obj.getClass().getDeclaredMethods()) {
            if(!method.isAnnotationPresent(EventInfo.class) || method.getParameterCount() != 2) continue;

            EventInfo info = method.getAnnotation(EventInfo.class);

            Class<? extends Event> clazz = (Class<? extends Event>) method.getParameterTypes()[1];

            EventSender matchedSender = getMatchingSender(clazz, method);

            if(matchedSender != null && !matchedSender.isRegistered() && matchedSender.getPlayers().size() == 0) {
                senders.remove(matchedSender);
                senders.add(new EventSender(clazz, method, obj, players));
                continue;
            }

            EventSender sender = new EventSender(clazz, method, obj, players);
            senders.add(sender);

            plugin.getServer().getPluginManager().registerEvent(clazz, listener, info.priority(), ((listener, e) -> {
                if(e instanceof PlayerEvent playerEvent) {
                    sender.execute(playerEvent.getPlayer(), e);
                }
            }), plugin);
        }
    }

    public void onDisable() {
        senders.forEach(sender -> sender.register(false));
        senders.clear();
    }

    public void unregister(Object obj) {
        senders.stream().filter(sender -> sender.getObj().equals(obj)).forEach(sender -> sender.register(false));
    }

    protected EventSender getMatchingSender(Class<? extends Event> clazz, Method method) {
        return senders.stream().filter(sender -> sender.getEventClass().equals(clazz) && sender.getMethod().equals(method)).findFirst().orElse(null);
    }

}
