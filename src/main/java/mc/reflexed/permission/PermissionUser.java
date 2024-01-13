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
        player.addAttachment(manager.plugin(), permission, value);
    }

    public List<String> permissions() {
        return player.getEffectivePermissions().stream() .map(PermissionAttachmentInfo::getPermission).toList();
    }

    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

}
