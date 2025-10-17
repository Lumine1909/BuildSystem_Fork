package de.eintosti.buildsystem.dynamic;

import de.eintosti.buildsystem.BuildSystemPlugin;
import de.eintosti.buildsystem.api.storage.WorldStorage;
import de.eintosti.buildsystem.api.world.BuildWorld;
import de.eintosti.buildsystem.config.Config.Settings;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.World;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CraftServerInjector {

    private static final WorldStorage storage = BuildSystemPlugin.get().getWorldService().getWorldStorage();

    private static final Function<Object, World> queryFunction = key -> {
        if (!Settings.dynamicWorldLoad) {
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
            buildWorld = storage.getBuildWorld(stringKey);
        } else if (key instanceof UUID uuidKey) {
            buildWorld = storage.getBuildWorld(uuidKey);
        }

        if (buildWorld == null || buildWorld.isLoading()) {
            return null;
        }
        if (!buildWorld.isLoaded()) {
            buildWorld.getLoader().load();
        }
        return buildWorld.getWorld();
    };

    private static Field field$worlds;
    private static Field field$worldsByUUID;

    private static Map worldsMap;
    private static Map worldsByUUIDMap;
    private static boolean isInjected = false;

    public static void inject() {
        try {
            Class<?> class$CraftServer = Bukkit.getServer().getClass();
            field$worlds = class$CraftServer.getDeclaredField("worlds");
            field$worlds.setAccessible(true);
            worldsMap = (Map) field$worlds.get(Bukkit.getServer());
            field$worlds.set(Bukkit.getServer(), ProxyMapHandler.createInjectedMap(
                (Map) field$worlds.get(Bukkit.getServer()),
                (Function) queryFunction
            ));

            // Winds-Studio/Leaf and downstream
            field$worldsByUUID = class$CraftServer.getDeclaredField("worldsByUUID");
            field$worldsByUUID.setAccessible(true);
            worldsByUUIDMap = (Map) field$worldsByUUID.get(Bukkit.getServer());
            field$worldsByUUID.set(Bukkit.getServer(), ProxyMapHandler.createInjectedMap(
                (Map) field$worlds.get(Bukkit.getServer()),
                (Function) queryFunction
            ));
        } catch (Exception ignored) {
        }
        isInjected = true;
    }

    public static void uninject() {
        if (!isInjected) {
            return;
        }
        try {
            field$worlds.set(Bukkit.getServer(), worldsMap);
            field$worldsByUUID.set(Bukkit.getServer(), worldsByUUIDMap);
        } catch (Exception ignored) {
        }
        isInjected = false;
    }
}