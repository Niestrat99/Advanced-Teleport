package io.github.teambanhammer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;

public class spawnCommand implements CommandExecutor, Listener {

    private static HashMap<Player, BukkitRunnable> movement = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getLabel().equalsIgnoreCase("spawn")){
            if (configuration.featSpawn()) {
                if (commandSender.hasPermission("tbh.tp.member.spawn")){
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        BukkitRunnable movementtimer = new BukkitRunnable() {

                            @Override
                            public void run() {
                                if (Warps.getSpawn() != null) {
                                    player.teleport(Warps.getSpawn());
                                    commandSender.sendMessage(ChatColor.GREEN + "Teleporting you to the spawn.");
                                    movement.remove(player);
                                } else {
                                    player.teleport(player.getWorld().getSpawnLocation());
                                    commandSender.sendMessage(ChatColor.GREEN + "Teleporting you to the spawn.");
                                    movement.remove(player);
                                }

                            }
                        };
                        movement.put(player, movementtimer);
                        movementtimer.runTaskLater(tbht_main.getProvidingPlugin(tbht_main.class), configuration.teleportTimer() * 20);
                        commandSender.sendMessage(ChatColor.GREEN + "Teleporting in " + ChatColor.AQUA + configuration.teleportTimer() + " seconds" + ChatColor.GREEN + ", please don't move!");
                        return false;

                    }
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Spawn " + ChatColor.RED + "is disabled!");
                return false;
            }
        } else if (command.getLabel().equalsIgnoreCase("setspawn")){
            if (configuration.featSpawn()) {
                if (commandSender.hasPermission("tbh.tp.admin.setspawn")){
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        Location spawn = player.getLocation();
                        try {
                            Warps.setSpawn(spawn);
                            commandSender.sendMessage(ChatColor.GREEN + "Successfully set the spawn location!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to use this command!");
                    return false;
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Spawn " + ChatColor.RED + "is disabled!");
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent event) {
        if (!configuration.cancelOnRotate()) {
            Location locTo = event.getTo();
            Location locFrom = event.getFrom();
            if (locTo.getBlockX() == locFrom.getBlockX() // If the player rotated instead of moved
                    && locTo.getBlockY() == locFrom.getBlockY()
                    && locTo.getBlockZ() == locTo.getBlockZ()) {
                return;
            }
        }
        if (movement.containsKey(event.getPlayer())) {
            BukkitRunnable timer = movement.get(event.getPlayer());
            timer.cancel();
            event.getPlayer().sendMessage(configuration.eventMovement());
            movement.remove(event.getPlayer());
        }
    }
}
