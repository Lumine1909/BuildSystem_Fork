package de.eintosti.buildsystem.inject;

import de.eintosti.buildsystem.BuildSystem;
import de.eintosti.buildsystem.world.BuildWorld;
import org.bukkit.World;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static de.eintosti.buildsystem.BuildSystem.plugin;

public class InjectedWorldsByUUID extends AbstractInjectedMap<UUID, World> {

    public InjectedWorldsByUUID(Map<UUID, World> origin) {
        super(origin);
    }

    @Override
    public World onQuery(UUID key) {
        if (!plugin.getConfigValues().isDynamicWorldLoad()) {
            return null;
        }
        String queryStackTrace = Arrays.toString(new Throwable().getStackTrace());
        if (queryStackTrace.contains("BuildWorld.unload") || queryStackTrace.contains("BuildWorld.<init>") || queryStackTrace.contains("BuildWorld.getWorld")) {
            return null;
        }
        BuildWorld buildWorld = plugin.getWorldManager().getBuildWorld(key);
        if (buildWorld == null || buildWorld.isLoading()) {
            return null;
        }
        if (!buildWorld.isLoaded()) {
            buildWorld.load();
        }
        return buildWorld.getWorld();
    }
}