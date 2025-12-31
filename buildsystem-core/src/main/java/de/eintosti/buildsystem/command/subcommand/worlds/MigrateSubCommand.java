package de.eintosti.buildsystem.command.subcommand.worlds;

import de.eintosti.buildsystem.BuildSystemPlugin;
import de.eintosti.buildsystem.Messages;
import de.eintosti.buildsystem.api.world.BuildWorld;
import de.eintosti.buildsystem.command.subcommand.Argument;
import de.eintosti.buildsystem.command.subcommand.SubCommand;
import de.eintosti.buildsystem.command.tabcomplete.WorldsTabCompleter.WorldsArgument;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;

public class MigrateSubCommand implements SubCommand {

    private final BuildSystemPlugin plugin;

    public MigrateSubCommand(BuildSystemPlugin plugin, String worldName) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NonNull Player player, String @NonNull [] args) {
        if (!player.isOp()) {
            return;
        }

        if (args.length > 2) {
            Messages.sendMessage(player, "worlds_migrate_usage");
            return;
        }

        for (BuildWorld world : new ArrayList<>(plugin.getWorldService().getWorldStorage().getBuildWorlds())) {
            if (!world.getName().startsWith("Realm_")) {}
            plugin.getWorldService().renameWorld(player, world, world.getName().replace("Realm_", ""));
        }
    }

    @Override
    public @NonNull Argument getArgument() {
        return WorldsArgument.RENAME;
    }
}