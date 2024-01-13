package mc.reflexed.permission;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;

@AllArgsConstructor
public class PermissionUser {
    private final PermissionManager manager;
    private final Player player;

    public void setPermission(String permission, boolean value) {
        if(player.isOp()) return;

        if (permission.equalsIgnoreCase("*")) {
            manager.getAllPermissions().forEach(p -> setPermission(p.getName(), value));
            return;
        }

        if(permission.endsWith("*")) {
            String base = permission.substring(0, permission.length() - 1);
            manager.getAllPermissions().stream()
                    .filter(p -> p.getName().startsWith(base))
                    .forEach(p -> setPermission(p.getName(), value));

            return;
        }

        player.addAttachment(manager.plugin(), permission, value);
    }

    public List<String> permissions() {
        return player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).toList();
    }

    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

}
