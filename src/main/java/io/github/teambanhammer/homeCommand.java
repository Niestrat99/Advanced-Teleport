package io.github.teambanhammer;

import org.bukkit.Bukkit;
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

public class homeCommand implements CommandExecutor, Listener {

    private static HashMap<Player, BukkitRunnable> movement = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getLabel().equalsIgnoreCase("sethome")) {
            if (configuration.featHomes()) {
                if (sender instanceof Player) {
                    Player player = (Player)sender;
                    if (sender.hasPermission("tbh.tp.member.sethome")) {
                        if (args.length>0) {
                            if (Bukkit.getPlayer(args[0]) != null) {
                                if (sender.hasPermission("tbh.tp.admin.sethome")) {
                                    if (args.length>1) {
                                        Player target = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                                        Location thome = player.getLocation();
                                        try {
                                            if (configuration.getHomes(target).containsKey(args[1])) {
                                                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.GOLD + args[0] + ChatColor.RED + " already has a home named " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                                                return false;
                                            } else {
                                                try {
                                                    configuration.setHome(target,args[1],thome);
                                                    sender.sendMessage(ChatColor.GREEN + "Successfully set the home " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " for " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "!");
                                                    if (target.isOnline()) {
                                                        target.sendMessage(ChatColor.GREEN + "An admin has set the home named " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " for you!");
                                                    }
                                                    return false;
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (NullPointerException ex) {
                                            try {
                                                configuration.setHome(target,args[1],thome);
                                                sender.sendMessage(ChatColor.GREEN + "Successfully set the home " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " for " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "!");
                                                if (target.isOnline()) {
                                                    target.sendMessage(ChatColor.GREEN + "An admin has set the home named " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " for you!");
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You have to include the home name!");
                                        return false;
                                    }
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "This player does not exist!");
                                return false;
                            }
                            Location home = player.getLocation();
                            try {
                                if (configuration.getHomes(player).containsKey(args[0])) {
                                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You already have a home named " + ChatColor.GOLD + args[0] + ChatColor.RED + "!");
                                    return false;
                                } else {
                                    try {
                                        configuration.setHome(player,args[0],home);
                                        sender.sendMessage(ChatColor.GREEN + "Successfully set the home " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "!");
                                    } catch (IOException e) {
                                        e.getStackTrace();
                                    }
                                }
                            } catch (NullPointerException ex) {
                                try {
                                    configuration.setHome(player,args[0],home);
                                    sender.sendMessage(ChatColor.GREEN + "Successfully set the home " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "!");
                                } catch (IOException e) {
                                    e.getStackTrace();
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You have to include the home name!");
                            return false;
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Homes " + ChatColor.RED + "is disabled!");
                return false;
            }
        } else if (command.getLabel().equalsIgnoreCase("delhome")) {
            if (configuration.featHomes()) {
                if (sender instanceof Player) {
                    Player player = (Player)sender;
                    if (sender.hasPermission("tbh.tp.member.delhome")) {
                        if (args.length>0) {
                            if (Bukkit.getPlayer(args[0]) != null) {
                                if (sender.hasPermission("tbh.tp.admin.delhome")) {
                                    if (args.length>1) {
                                        Player target = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                                        try {
                                            if (configuration.getHomes(target).containsKey(args[1])) {
                                                try {
                                                    configuration.delHome(target, args[1]);
                                                    sender.sendMessage(ChatColor.GREEN + "Successfully deleted " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "'s home named " +ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                                                    if (target.isOnline()) {
                                                        target.sendMessage(ChatColor.GOLD + "An admin has deleted your home named " + ChatColor.YELLOW + args [1] + ChatColor.GOLD + "!");
                                                    }
                                                    return false;
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.GOLD + args[0] + ChatColor.RED + " does not have a home named " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                                                return false;
                                            }
                                        } catch (NullPointerException ex) {
                                            try {
                                                configuration.delHome(target, args[1]);
                                                sender.sendMessage(ChatColor.GREEN + "Successfully deleted " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "'s home named " +ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                                                if (target.isOnline()) {
                                                    target.sendMessage(ChatColor.GOLD + "An admin has deleted your home named " + ChatColor.YELLOW + args [1] + ChatColor.GOLD + "!");
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You have to include the home name!");
                                        return false;
                                    }
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "This player does not exist!");
                                return false;
                            }
                            try {
                                if (configuration.getHomes(player).containsKey(args[0])) {
                                    try {
                                        configuration.delHome(player,args[0]);
                                        sender.sendMessage(ChatColor.GREEN + "Successfully deleted the home " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "!");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "This home does not exist!");
                                }
                            } catch (NullPointerException ex) {
                                try {
                                    configuration.delHome(player,args[0]);
                                    sender.sendMessage(ChatColor.GREEN + "Successfully deleted the home " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "!");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You have to include the home name!");
                            return false;
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Homes " + ChatColor.RED + "is disabled!");
                return false;
            }
        } else if (command.getLabel().equalsIgnoreCase("home")) {
            if (configuration.featHomes()) {
                if (sender.hasPermission("tbh.tp.member.home")) {
                    if (sender instanceof Player) {
                        Player player = (Player)sender;
                        if (args.length>0) {
                            if (Bukkit.getPlayer(args[0]) != null) {
                                if (sender.hasPermission("tbh.tp.admin.home")) {
                                    if (args.length>1) {
                                        Player target = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                                        try {
                                            if (configuration.getHomes(target).containsKey(args[1])) {

                                            } else {
                                                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "This home does not exist!");
                                                return false;
                                            }
                                        } catch (NullPointerException ex) {
                                            Location tlocation = configuration.getHomes(target).get(args[1]);
                                            player.teleport(tlocation);
                                            sender.sendMessage(ChatColor.GREEN + "Successfully teleported you to " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "'s home!");
                                            return false;
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You have to include the home name!");
                                        return false;
                                    }
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "This player does not exist!");
                                return false;
                            }
                            try {
                                if (configuration.getHomes(player).containsKey(args[0])) {
                                    Location location = configuration.getHomes(player).get(args[0]);
                                    BukkitRunnable movementtimer = new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            player.teleport(location);
                                            movement.remove(player);
                                            sender.sendMessage(ChatColor.GREEN + "Successfully teleported to your home!");
                                        }
                                    };
                                    movement.put(player, movementtimer);
                                    movementtimer.runTaskLater(tbht_main.getProvidingPlugin(tbht_main.class), configuration.teleportTimer() * 20);
                                    player.sendMessage(configuration.eventBeforeTP().replaceAll("\\{countdown}", String.valueOf(configuration.teleportTimer())));
                                    return false;
                                } else {
                                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "This home does not exist!");
                                }
                            } catch (NullPointerException ex) {
                                Location location = configuration.getHomes(player).get(args[0]);
                                BukkitRunnable movementtimer = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.teleport(location);
                                        movement.remove(player);
                                        sender.sendMessage(ChatColor.GREEN + "Successfully teleported to your home!");
                                    }
                                };
                                movement.put(player, movementtimer);
                                movementtimer.runTaskLater(tbht_main.getProvidingPlugin(tbht_main.class), configuration.teleportTimer() * 20);
                                player.sendMessage(configuration.eventBeforeTP().replaceAll("\\{countdown}", String.valueOf(configuration.teleportTimer())));
                                return false;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You have to include the home name!");
                            return false;
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Homes " + ChatColor.RED + "is disabled!");
                return false;
            }
        } else if (command.getLabel().equalsIgnoreCase("homes")) {
            if (configuration.featHomes()) {
                if (sender.hasPermission("tbh.tp.member.homes")) {
                    if (args.length>0) {
                        if (sender.hasPermission("tbh.tp.admin.homes")) {
                            Player player = (Player)Bukkit.getPlayer(args[0]);
                            StringBuilder hlist = new StringBuilder();
                            hlist.append(ChatColor.GOLD + "" + ChatColor.BOLD + args[0] + ChatColor.AQUA + "" + ChatColor.BOLD + "'s Homes: " + ChatColor.WHITE);
                            if (Bukkit.getPlayer(args[0]) != null) {
                                try {
                                    if (configuration.getHomes(player).size()>0) {
                                        for (String home:configuration.getHomes(player).keySet()) {
                                            hlist.append(home + ", ");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.GOLD + args[0] + ChatColor.RED + " does not have any homes!");
                                        return false;
                                    }

                                } catch (NullPointerException ex) {
                                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.GOLD + args[0] + ChatColor.RED + " does not have any homes!");
                                    return false;
                                }
                                sender.sendMessage(hlist.toString());
                                return false;
                            } else {
                                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "This player does not exist!");
                                return false;
                            }
                        }
                    }
                    if (sender instanceof Player) {
                        Player player = (Player)sender;
                        StringBuilder hlist = new StringBuilder();
                        hlist.append(ChatColor.AQUA + "" + ChatColor.BOLD + "Homes: " + ChatColor.WHITE);
                        try {
                            if (configuration.getHomes(player).size()>0){
                                for (String home:configuration.getHomes(player).keySet()) {
                                    hlist.append(home + ", ");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You haven't got any homes!");
                                return false;
                            }
                        } catch (NullPointerException ex) { // If a player has never set any homes
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You haven't got any homes!");
                            return false;
                        }
                        sender.sendMessage(hlist.toString());
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Homes " + ChatColor.RED + "is disabled!");
                return false;
            }
        }
        return false;
    }
    @EventHandler
    public void onMovement(PlayerMoveEvent event) {
        if (movement.containsKey(event.getPlayer())) {
            BukkitRunnable timer = movement.get(event.getPlayer());
            timer.cancel();
            event.getPlayer().sendMessage(configuration.eventMovement());
            movement.remove(event.getPlayer());
        }
    }
}
