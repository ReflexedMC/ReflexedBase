package mc.reflexed.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EventSender {

    private final List<Object> players = new ArrayList<>();

    private final Class<? extends Event> eventClass;
    private final Method method;
    private final Object obj;

    private boolean registered;

    public EventSender(Class<? extends Event> eventClass, Method method, Object obj, Player... players) {
        this.eventClass = eventClass;
        this.method = method;
        this.obj = obj;
        this.registered = true;

        if(players.length == 0) return;

        this.players.addAll(List.of(players));
    }

    public void execute(Object... args) {
        try {
            if (!registered) return;

            if (players.size() == 0 && eventClass.isAssignableFrom(args[0].getClass())) {
                method.invoke(obj, args);
                return;
            }

            if (!players.contains(args[0])) return;

            if (method.getParameterCount() >= 1 && eventClass.isAssignableFrom(args[1].getClass()) && args[0] instanceof Player) {
                Object player = args[0];
                Object event = args[1];

                method.invoke(obj, player, event);
                return;
            }

            method.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPlayers(Player... player) {
        players.addAll(List.of(player));
    }

    public void register(boolean registered) {
        this.registered = registered;
    }



}
