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
        if (!Bukkit.getServer().isPrimaryThread()) {
            throw new RuntimeException("What the hack are you doing here, access world asynchronously?");
        }

        boolean shouldAbort = StackWalker.getInstance().walk(frames -> frames.anyMatch(frame -> {
            String className = frame.getClassName();
            String methodName = frame.getMethodName();
            return (className.contains("BuildWorld") && (methodName.contains("unload") || methodName.contains("<init>") || methodName.contains("getWorld")) ||
                (className.contains("FilteredWorldsInventory") && methodName.contains("isValidWorld")) || className.contains("de.Ste3et_C0st"));
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
            Field field$worlds = Class_CraftServer.getDeclaredField("worlds");
            field$worlds.setAccessible(true);
            field$worlds.set(Bukkit.getServer(), ProxyMapHandler.createInjectedMap(
                (Map) field$worlds.get(Bukkit.getServer()),
                (Function) queryFunction
            ));

            // Winds-Studio/Leaf and downstream
            Field field$worldsByUUID = Class_CraftServer.getDeclaredField("worldsByUUID");
            field$worldsByUUID.setAccessible(true);
            field$worldsByUUID.set(Bukkit.getServer(), ProxyMapHandler.createInjectedMap(
                (Map) field$worlds.get(Bukkit.getServer()),
                (Function) queryFunction
            ));
        } catch (Exception ignored) {
        }
    }
}