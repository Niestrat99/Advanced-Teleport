package io.github.teambanhammer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class configuration {

    public static File ConfigFile = new File("plugins/AdvancedTeleport","config.yml");
    public static FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);

    public static void save() throws IOException {
        Config.save(ConfigFile);
    }
    public static void setDefaults() throws IOException {
        Config.addDefault("timers.commandCooldown",5);
        Config.addDefault("timers.teleportTimer",3);
        Config.addDefault("timers.requestLifetime",60);
        Config.addDefault("events.eventBeforeTP" , "&aTeleporting in &b{countdown} seconds&a, please don't move!");
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
        Config.addDefault("tpr.avoidBlocks", new ArrayList<>(Arrays.asList("Water","Lava")));
        Config.options().copyDefaults(true);
        save();
    }
    public static int commandCooldown(){
        return Config.getInt("commandCooldown");
    }
    public static int teleportTimer(){
        return Config.getInt("teleportTimer");
    }
    public static int requestLifetime(){
        return Config.getInt("requestLifetime");
    }
    public static boolean useVault() {return Config.getBoolean("useVault");}
    public static double teleportPrice() {return Config.getDouble("teleportPrice");}
    public static String eventMovement() {return ChatColor.translateAlternateColorCodes('&' , Config.getString("eventMovement"));}
    public static String eventTeleport() {return ChatColor.translateAlternateColorCodes('&' , Config.getString("eventTeleport"));}
    public static String eventBeforeTP() {return ChatColor.translateAlternateColorCodes('&' , Config.getString("eventBeforeTP"));}
    public static boolean EXPPayment() {return Config.getBoolean("EXPPayment");}
    public static int EXPTeleportPrice() {return Config.getInt("EXPTeleportPrice");}
    public static int vaultTPRCost() {return Config.getInt("vaultTPRCost");}
    public static int EXPTPRCost() {return Config.getInt("EXPTPRCost");}
    public static boolean useWorldBorder() {return Config.getBoolean("tpr.useWorldBorder");}
    public static int maxX() {return Config.getInt("tpr.maximum-x");}
    public static int minX() {return Config.getInt("tpr.minimum-x");}
    public static int maxZ() {return Config.getInt("tpr.maximum-z");}
    public static int minZ() {return Config.getInt("tpr.minimum-z");}
    public static List <String> avoidBlocks() {return Config.getStringList("tpr.avoidBlocks");}
}
