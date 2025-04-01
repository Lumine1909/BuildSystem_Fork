package de.eintosti.buildsystem.inject;

import static de.eintosti.buildsystem.BuildSystem.plugin;

import de.eintosti.buildsystem.world.BuildWorld;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class CraftServerInjector {

    private static final Function<Object, World> queryFunction = key -> {
        if (!plugin.getConfigValues().isDynamicWorldLoad()) {
            return null;
        }
        String queryStackTrace = Arrays.toString(new Throwable().getStackTrace());
        if (queryStackTrace.contains("BuildWorld.unload") || queryStackTrace.contains("BuildWorld.<init>") || queryStackTrace.contains("BuildWorld.getWorld")) {
            return null;
        }
        BuildWorld buildWorld = null;
        if (key instanceof String string) {
            buildWorld = plugin.getWorldManager().getBuildWorld(string);
        } else if (key instanceof UUID uuid) {
            buildWorld = plugin.getWorldManager().getBuildWorld(uuid);
        }
        if (buildWorld == null || buildWorld.isLoading()) {
            return null;
        }
        if (!buildWorld.isLoaded()) {
            buildWorld.load();
        }
        return buildWorld.getWorld();
    };

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void inject() {
        try {
            Class<?> Class_CraftServer = Bukkit.getServer().getClass();
            Field Field_worlds = Class_CraftServer.getDeclaredField("worlds");
            Field_worlds.setAccessible(true);
            Field_worlds.set(Bukkit.getServer(), ProxyMapHandler.createInjectedMap(
                (Map) Field_worlds.get(Bukkit.getServer()),
                (Function) queryFunction
            ));

            // Winds-Studio/Leaf and downstream
            Field Field_worldsByUUID = Class_CraftServer.getDeclaredField("worldsByUUID");
            Field_worldsByUUID.setAccessible(true);
            Field_worldsByUUID.set(Bukkit.getServer(), ProxyMapHandler.createInjectedMap(
                (Map) Field_worlds.get(Bukkit.getServer()),
                (Function) queryFunction
            ));
        } catch (Exception ignored) {
        }
    }
}