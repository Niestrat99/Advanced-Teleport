package io.github.teambanhammer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class configuration {

    public static File ConfigFile = new File("plugins/AdvancedTeleport","config.yml");
    public static FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);

    public static File HomesFile = new File("plugins/AdvancedTeleport","homes.yml");
    public static FileConfiguration Homes = YamlConfiguration.loadConfiguration(HomesFile);

    public static void save() throws IOException {
        Config.save(ConfigFile);
        Homes.save(HomesFile);
    }

    public static void setHome(Player player, String homename, Location location) throws IOException {
        Homes.set(player.getUniqueId().toString() + "." + homename + ".x", location.getBlockX());
        Homes.set(player.getUniqueId().toString() + "." + homename + ".y", location.getBlockY());
        Homes.set(player.getUniqueId().toString() + "." + homename + ".z", location.getBlockZ());
        Homes.set(player.getUniqueId().toString() + "." + homename + ".world", location.getWorld().getName());
        save();
    }

    public static HashMap<String,Location> getHomes(Player player){
        HashMap<String,Location> homes = new HashMap<>();
        try {
            for (String home: Homes.getConfigurationSection(player.getUniqueId().toString()).getKeys(false)) {
                Location location = new Location(Bukkit.getWorld(Homes.getString(player.getUniqueId().toString() + "." + home + ".world")), // Gets world from name
                        Homes.getInt(player.getUniqueId().toString() + "." + home + ".x"), // Gets X value
                        Homes.getInt(player.getUniqueId().toString() + "." + home + ".y"), // Gets Y value
                        Homes.getInt(player.getUniqueId().toString() + "." + home + ".z")); // Gets Z value
                homes.put(home,location);
            }
        } catch (NullPointerException ex) {
            Homes.createSection(player.getUniqueId().toString());
        }

        return homes;
    }

    public static void delHome(Player player, String homename) throws IOException {
        Homes.set(player.getUniqueId().toString()+"."+homename,null);
        save();
    }

    public static void setDefaults() throws IOException {
        Config.addDefault("features.teleport", true);
        Config.addDefault("features.warps", true);
        Config.addDefault("features.spawn", true);
        Config.addDefault("features.randomTP", true);
        Config.addDefault("features.homes",true);
        Config.addDefault("timers.commandCooldown",5);
        Config.addDefault("timers.teleportTimer",3);
        Config.addDefault("timers.requestLifetime",60);
        Config.addDefault("timers.cancel-on-rotate", false);
        Config.addDefault("events.eventBeforeTP" , "&aTeleporting in &b{countdown} seconds&a, please do not move!");
        Config.addDefault("events.eventTeleport" , "&aTeleporting...");
        Config.addDefault("events.eventMovement" , "&cTeleport has been cancelled due to movement.");
        Config.addDefault("booleans.useVault" , false);
        Config.addDefault("booleans.EXPPayment" , false);
        Config.addDefault("payments.vault.teleportPrice" , 100.00);
        Config.addDefault("payments.vault.vaultTPRCost" , 200);
        Config.addDefault("payments.exp.EXPTeleportPrice" , 2);
        Config.addDefault("payments.exp.EXPTPRCost" , 4);
        Config.addDefault("tpr.maximum-x", 10000);
        Config.addDefault("tpr.minimum-x", -10000);
        Config.addDefault("tpr.maximum-z", 10000);
        Config.addDefault("tpr.minimum-z", -10000);
        Config.addDefault("tpr.useWorldBorder", true);
        Config.addDefault("tpr.avoidBlocks", new ArrayList<>(Arrays.asList("WATER","LAVA", "STATIONARY_WATER", "STATIONARY_LAVA")));
        Config.options().copyDefaults(true);
        save();
    }
    public static int commandCooldown(){
        return Config.getInt("timers.commandCooldown");
    }
    public static int teleportTimer(){
        return Config.getInt("timers.teleportTimer");
    }
    public static int requestLifetime(){
        return Config.getInt("timers.requestLifetime");
    }
    public static boolean useVault() {return Config.getBoolean("booleans.useVault");}
    public static double teleportPrice() {return Config.getDouble("payments.vault.teleportPrice");}
    public static String eventMovement() {return ChatColor.translateAlternateColorCodes('&' , Config.getString("events.eventMovement"));}
    public static String eventTeleport() {return ChatColor.translateAlternateColorCodes('&' , Config.getString("events.eventTeleport"));}
    public static String eventBeforeTP() {return ChatColor.translateAlternateColorCodes('&' , Config.getString("events.eventBeforeTP"));}
    public static boolean EXPPayment() {return Config.getBoolean("booleans.EXPPayment");}
    public static int EXPTeleportPrice() {return Config.getInt("payments.exp.EXPTeleportPrice");}
    public static int vaultTPRCost() {return Config.getInt("payments.vault.vaultTPRCost");}
    public static int EXPTPRCost() {return Config.getInt("payments.exp.EXPTPRCost");}
    public static boolean useWorldBorder() {return Config.getBoolean("tpr.useWorldBorder");}
    public static int maxX() {return Config.getInt("tpr.maximum-x");}
    public static int minX() {return Config.getInt("tpr.minimum-x");}
    public static int maxZ() {return Config.getInt("tpr.maximum-z");}
    public static int minZ() {return Config.getInt("tpr.minimum-z");}
    public static List <String> avoidBlocks() {return Config.getStringList("tpr.avoidBlocks");}
    public static boolean featTP() {return Config.getBoolean("features.teleport");}
    public static boolean featWarps() {return Config.getBoolean("features.warps");}
    public static boolean featSpawn() {return Config.getBoolean("features.spawn");}
    public static boolean featRTP() {return Config.getBoolean("features.randomTP");}
    public static boolean featHomes() {return Config.getBoolean("features.homes");}
    public static boolean cancelOnRotate() {return Config.getBoolean("timers.cancel-on-rotate");}

    public static void reloadConfig() throws IOException {
        Config = YamlConfiguration.loadConfiguration(ConfigFile);
        save();
    }
}
