package de.eintosti.buildsystem.util;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlotUtil {

    private static boolean hasPlotPlugin = false;
    private static PlotAPI plotAPI;

    public static void init() {
        hasPlotPlugin = true;
        plotAPI = new PlotAPI();
    }

    public static boolean hasPermissionAt(Player player, Location location) {
        if (!hasPlotPlugin) {
            return true;
        }
        if (plotAPI.getPlotAreas(location.getWorld().getName()).isEmpty()) {
            return true;
        }
        Plot plot = Plot.getPlot(com.plotsquared.core.location.Location.at(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        if (plot == null) {
            return false;
        }
        return plot.getMembers().contains(player) || plot.getOwners().contains(player) || plot.getTrusted().contains(player);
    }

}
