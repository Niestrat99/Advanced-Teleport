package io.github.teambanhammer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Warps {

    public static File Warp = new File("plugins/AdvancedTeleport", "warps.yml");
    public static FileConfiguration Warps = YamlConfiguration.loadConfiguration(Warp);

    public static void save() throws IOException {
        Warps.save(Warp);
    }

    public static void setWarp(String warpName, Location location) throws IOException {
        Warps.set(warpName + ".x", location.getBlockX());
        Warps.set(warpName + ".y", location.getBlockY());
        Warps.set(warpName + ".z", location.getBlockZ());
        Warps.set(warpName + ".world", location.getWorld().getName());
        save();
    }

    public static HashMap<String, Location> getWarps() {
        HashMap<String, Location> warps = new HashMap<>();
        for (String Warp : Warps.getKeys(false)) {
            Location location = new Location(Bukkit.getWorld(Warps.getString(Warp + ".world")), Warps.getInt(Warp + ".x"), Warps.getInt(Warp + ".y"), Warps.getInt(Warp + ".z"));
            warps.put(Warp, location);
        }
        return warps;

    }
    public static void delWarp(String warpName) throws IOException {
        Warps.set(warpName,null);
        save();
    }

    public static void setSpawn(Location location) throws IOException {
        Warps.set("spawnpoint.x", location.getBlockX());
        Warps.set("spawnpoint.y", location.getBlockY());
        Warps.set("spawnpoint.z", location.getBlockZ());
        Warps.set("spawnpoint.world", location.getWorld().getName());
        save();
    }

    public static Location getSpawn() {
        try {
            Location location = new Location(Bukkit.getWorld(Warps.getString( "spawnpoint.world")), Warps.getInt(  "spawnpoint.x"), Warps.getInt("spawnpoint.y"), Warps.getInt("spawnpoint.z"));
            return location;
        } catch (NullPointerException ex) {
            return null;
        }

    }
}
