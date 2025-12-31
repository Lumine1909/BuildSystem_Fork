package de.eintosti.buildsystem.command;

import de.eintosti.buildsystem.BuildSystemPlugin;
import de.eintosti.buildsystem.Messages;
import de.eintosti.buildsystem.api.world.BuildWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class RealmBackCommand implements CommandExecutor {

    private final BuildSystemPlugin plugin;

    public RealmBackCommand(BuildSystemPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("realm-back").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getLogger().warning(Messages.getString("sender_not_player", sender));
            return true;
        }

        if (!player.hasPermission("buildsystem.realm-back")) {
            Messages.sendPermissionError(player);
            return true;
        }

        if (args.length == 0) {
            List<BuildWorld> worlds = plugin.getWorldService()
                .getWorldStorage()
                .getBuildWorldsCreatedByPlayer(player);
            if (!worlds.isEmpty()) {
                worlds.getFirst().getTeleporter().teleport(player);
            } else {
                player.sendMessage(ChatColor.RED + "你没有已创建的领域");
            }
        }

        return true;
    }
}