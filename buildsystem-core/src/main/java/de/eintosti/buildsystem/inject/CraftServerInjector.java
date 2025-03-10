package de.eintosti.buildsystem.inject;

import org.bukkit.Bukkit;
import org.bukkit.World;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class CraftServerInjector {

    public static void inject() {
        try {
            Class<?> Class_CraftServer = Bukkit.getServer().getClass();
            Field Field_worlds = Class_CraftServer.getDeclaredField("worlds");
            Field_worlds.setAccessible(true);
            Field_worlds.set(Bukkit.getServer(), new InjectedWorlds((Map<String, World>) Field_worlds.get(Bukkit.getServer())));

            // Winds-Studio/Leaf and downstream
            Field Field_worldsByUUID = Class_CraftServer.getDeclaredField("worldsByUUID");
            Field_worldsByUUID.setAccessible(true);
            Field_worldsByUUID.set(Bukkit.getServer(), new InjectedWorldsByUUID((Map<UUID, World>) Field_worldsByUUID.get(Bukkit.getServer())));
        } catch (Exception ignored) {
        }
    }
}
