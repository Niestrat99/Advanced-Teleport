package io.github.teambanhammer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class configuration {

    public static File ConfigFile = new File("plugins/AdvancedTeleport","config.yml");
    public static FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);

    public static void save() throws IOException {
        Config.save(ConfigFile);
    }
    public static void setDefaults() throws IOException {
        Config.addDefault("commandCooldown",5);
        Config.addDefault("teleportTimer",3);
        Config.addDefault("requestLifetime",60);
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
}
