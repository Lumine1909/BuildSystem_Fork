package de.eintosti.buildsystem.command;

import de.eintosti.buildsystem.BuildSystem;
import de.eintosti.buildsystem.Messages;
import de.eintosti.buildsystem.world.BuildWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.List;

import static de.eintosti.buildsystem.Messages.sendMessage;

public class RealmBackCommand implements TabExecutor {

    private final BuildSystem plugin;

    public RealmBackCommand(BuildSystem plugin) {
        this.plugin = plugin;

        plugin.getCommand("realm-back").setExecutor(this);
        plugin.getCommand("realm-back").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getLogger().warning(Messages.getString("sender_not_player", null));
            return true;
        }

        if (!player.hasPermission("buildsystem.realmback")) {
            plugin.sendPermissionMessage(player);
            return true;
        }

        if (args.length == 0) {
            List<BuildWorld> buildWorlds = plugin.getWorldManager().getBuildWorldsCreatedByPlayer(player);
            if (buildWorlds.isEmpty()) {
                sendMessage(sender, "realm_back_not_available");
                return true;
            }
            plugin.getWorldManager().teleport(player, buildWorlds.get(0));
        } else if (args.length == 1) {
            List<BuildWorld> buildWorlds = plugin.getWorldManager().getBuildWorldsCreatedByPlayer(player).stream()
                .filter(buildWorld -> buildWorld.getName().equalsIgnoreCase(args[0])).toList();
            if (buildWorlds.isEmpty()) {
                sendMessage(sender, "realm_back_unknown_world");
                return true;
            }
            plugin.getWorldManager().teleport(player, buildWorlds.get(0));
        }
        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }
        if (args.length <= 1) {
            List<BuildWorld> buildWorlds = plugin.getWorldManager().getBuildWorldsCreatedByPlayer(player);
            return buildWorlds.stream().map(BuildWorld::getName).toList();
        }
        return Collections.emptyList();
    }
}
