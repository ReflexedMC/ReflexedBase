package mc.reflexed.permission;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.List;

public record PermissionManager(Plugin plugin) {

    public PermissionUser getUser(Player player) {
        return new PermissionUser(this, player);
    }

    public void register(Permission permission) {
        if(plugin.getServer().getPluginManager().getPermission(permission.getName()) != null) return;

        plugin.getServer().getPluginManager().addPermission(permission);
    }

    public void register(String name) {
        register(permission(name));
    }

    public void unregister(Permission permission) {
        plugin.getServer().getPluginManager().removePermission(permission);
    }

    public void unregister(String name) {
        unregister(permission(name));
    }

    public List<Permission> getAllPermissions() {
        return plugin.getServer().getPluginManager().getPermissions().stream().toList();
    }

    public Permission permission(String name) {
        List<Permission> allPermissions = getAllPermissions();

        for (Permission permission : allPermissions) {
            if (permission.getName().equals(name)) {
                return permission;
            }
        }

        return new Permission(name);
    }
}
