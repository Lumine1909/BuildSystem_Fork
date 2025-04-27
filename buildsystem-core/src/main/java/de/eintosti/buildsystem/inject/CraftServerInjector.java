package de.eintosti.buildsystem.inject;

import static de.eintosti.buildsystem.BuildSystem.plugin;

import de.eintosti.buildsystem.world.BuildWorld;
import java.lang.reflect.Field;
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

        boolean shouldAbort = StackWalker.getInstance().walk(frames -> frames.anyMatch(frame -> {
            String className = frame.getClassName();
            String methodName = frame.getMethodName();
            return (className.contains("BuildWorld") && (methodName.contains("unload") || methodName.contains("<init>") || methodName.contains("getWorld")) ||
                (className.contains("FilteredWorldsInventory") && methodName.contains("isValidWorld")));
        }));
        if (shouldAbort) {
            return null;
        }

        BuildWorld buildWorld = null;
        if (key instanceof String stringKey) {
            buildWorld = plugin.getWorldManager().getBuildWorld(stringKey);
        } else if (key instanceof UUID uuidKey) {
            buildWorld = plugin.getWorldManager().getBuildWorld(uuidKey);
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