package io.github.teambanhammer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class tbht_main extends JavaPlugin {

private List<Player>tpoff = new ArrayList<>();

    @Override
    public void onEnable (){
        System.out.println("TBH_Teleport is now enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("tphelp")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + "Teleport Commands");
            sender.sendMessage("/tpa <Player> - Sends the targeted player a teleport request to where they are.");
            sender.sendMessage("/tpahere <Player> - Sends the targeted player a teleport requests to where you are.");
            sender.sendMessage("/tpaall - Sends everyone a teleport request to where you are.");
            sender.sendMessage("/tpacancel - Cancels your teleport request.");
            sender.sendMessage("/tpayes - Accepts a teleport request someone sent you.");
            sender.sendMessage("/tpano - Declines a teleport request someone sent you.");
            sender.sendMessage("/tpon - Enables receiving teleport requests.");
            sender.sendMessage("/tpoff - Disables receiving teleport requests.");
            sender.sendMessage("/tpo <Player> - (ADMIN ONLY COMMAND) Instantly teleports you to another player.");
            return false;
        } else if (label.equalsIgnoreCase("tpa")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length > 0) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " Either the player is currently offline or doesn't exist.");
                        return false;
                    } else {
                        if (tpoff.contains(target)) {
                            sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.RED + " has disabled to receive teleport requests!");
                            return false;
                        }
                        sender.sendMessage(ChatColor.GREEN + "Teleport request send to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");
                        sender.sendMessage(ChatColor.GREEN + "They've got " + ChatColor.AQUA + "60 " + ChatColor.GREEN + "seconds to respond!");
                        sender.sendMessage(ChatColor.GREEN + "To cancel the request use " + ChatColor.AQUA + "/tpcancel " + ChatColor.GREEN + "to cancel it.");
                        target.sendMessage(ChatColor.GREEN + "The Player " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " wants to teleport to you!");
                        target.sendMessage(ChatColor.GREEN + "If you want to accept it use " + ChatColor.AQUA + "/tpayes " + ChatColor.GREEN + ", if not use" + ChatColor.AQUA + "/tpano" + ChatColor.GREEN + ".");
                        target.sendMessage(ChatColor.GREEN + "You've got " + ChatColor.AQUA + "60 " + ChatColor.GREEN + "seconds to respond to the request!");
                        BukkitRunnable run = new BukkitRunnable() {
                            @Override
                            public void run() {
                                sender.sendMessage(ChatColor.GREEN + "Your teleport request to " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " has expired!");
                                TeleportRequest.removeRequest(TeleportRequest.getRequest(player));
                            }
                        };
                        run.runTaskLater(this, 1200); // 60 seconds
                        TeleportRequest request = new TeleportRequest(player, target, run, TeleportRequest.TeleportType.TPA_NORMAL); // Creates a new teleport request.
                        TeleportRequest.addRequest(request);
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("tpahere")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length > 0) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " Either the player is currently offline or doesn't exist.");
                        return false;
                    } else {
                        if (tpoff.contains(target)) {
                            sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.RED + " has disabled to receive teleport requests!");
                            return false;
                        }
                        sender.sendMessage(ChatColor.GREEN + "Teleport request send to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");
                        sender.sendMessage(ChatColor.GREEN + "They've got " + ChatColor.AQUA + "60 " + ChatColor.GREEN + "seconds to respond!");
                        sender.sendMessage(ChatColor.GREEN + "To cancel the request use " + ChatColor.AQUA + "/tpcancel " + ChatColor.GREEN + "to cancel it.");
                        target.sendMessage(ChatColor.GREEN + "The Player " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " wants to teleport you to them!");
                        target.sendMessage(ChatColor.GREEN + "If you want to accept it use " + ChatColor.AQUA + "/tpayes " + ChatColor.GREEN + ", if not use" + ChatColor.AQUA + "/tpano" + ChatColor.GREEN + ".");
                        target.sendMessage(ChatColor.GREEN + "You've got " + ChatColor.AQUA + "60 " + ChatColor.GREEN + "seconds to respond to the request!");
                        BukkitRunnable run = new BukkitRunnable() {
                            @Override
                            public void run() {
                                sender.sendMessage(ChatColor.GREEN + "Your teleport request to " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " has expired!");
                                TeleportRequest.removeRequest(TeleportRequest.getRequest(player));
                            }
                        };
                        run.runTaskLater(this, 1200); // 60 seconds
                        TeleportRequest request = new TeleportRequest(player, target, run, TeleportRequest.TeleportType.TPA_HERE); // Creates a new teleport request.
                        TeleportRequest.addRequest(request);
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("tpayes")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (TeleportRequest.getRequest(player) != null) {
                    TeleportRequest request = TeleportRequest.getRequest(player);
                    request.getRequester().sendMessage(ChatColor.YELLOW + "" + player.getName() + ChatColor.GREEN + " has accepted your teleport request!");
                    player.sendMessage(ChatColor.GREEN + "You've accepted the teleport request!");
                    if (request.getType() == TeleportRequest.TeleportType.TPA_HERE) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.teleport(request.getRequester());
                            }
                        }.runTaskLater(this, 60); // 3 seconds
                        player.sendMessage(ChatColor.GREEN + "Teleporting in " + ChatColor.AQUA + "3 seconds" + ChatColor.GREEN + ", please don't move!"); //TODO Add checker for movement!);
                    } else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                request.getRequester().teleport(player);
                            }
                        }.runTaskLater(this, 60); // 3 seconds
                        request.getRequester().sendMessage(ChatColor.GREEN + "Teleporting in " + ChatColor.AQUA + "3 seconds" + ChatColor.GREEN + ", please don't move!"); //TODO Add checker for movement!
                    }
                    request.destroy();
                    return false;
                } else {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You don't have any pending requests!");
                    return false;
                }
            }

        } else if (label.equalsIgnoreCase("tpano")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (TeleportRequest.getRequest(player) != null) {
                    TeleportRequest request = TeleportRequest.getRequest(player);
                    request.getRequester().sendMessage(ChatColor.YELLOW + "" + player.getName() + ChatColor.GREEN + " has declined your teleport request!");
                    player.sendMessage(ChatColor.GREEN + "You've declined the teleport request!");
                    request.destroy();
                    return false;
                } else {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You don't have any pending requests!");
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("tpo")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length > 0) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " Either the player is currently offline or doesn't exist.");
                        return false;
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Teleporting to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");
                        player.teleport(target);
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("tpacancel")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (TeleportRequest.getRequestByRequester(player) != null) {
                    TeleportRequest request = TeleportRequest.getRequestByRequester(player);
                    player.sendMessage(ChatColor.GREEN + "You have canceled your teleport request.");
                    request.getResponder().sendMessage(ChatColor.YELLOW + "" + sender.getName() + ChatColor.RED + " has canceled their teleport request.");
                    request.destroy();
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("tpaall")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (target != player) {
                        target.sendMessage(ChatColor.GREEN + "The Player " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " wants to teleport you to them!");
                        target.sendMessage(ChatColor.GREEN + "If you want to accept it use " + ChatColor.AQUA + "/tpayes " + ChatColor.GREEN + ", if not use" + ChatColor.AQUA + "/tpano" + ChatColor.GREEN + ".");
                        target.sendMessage(ChatColor.GREEN + "You've got " + ChatColor.AQUA + "60 " + ChatColor.GREEN + "seconds to respond to the request!");
                        BukkitRunnable run = new BukkitRunnable() {
                            @Override
                            public void run() {
                                TeleportRequest.removeRequest(TeleportRequest.getRequest(player));
                            }
                        };
                        run.runTaskLater(this, 1200); // 60 seconds
                        TeleportRequest request = new TeleportRequest(player, target, run, TeleportRequest.TeleportType.TPA_HERE); // Creates a new teleport request.
                        TeleportRequest.addRequest(request);
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpoff")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!tpoff.contains(player)) {
                    tpoff.add(player);
                    sender.sendMessage(ChatColor.GREEN + "Successfully disabled teleport requests!");
                    sender.sendMessage(ChatColor.GREEN + "You can no longer receive any teleport requests.");
                    sender.sendMessage(ChatColor.AQUA + "If you want to receive teleport requests type " + ChatColor.YELLOW + "/tpon " + ChatColor.AQUA + "to enable it.");
                }
            }
        } else if (label.equalsIgnoreCase("tpon")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                if (tpoff.contains(player)) {
                    tpoff.remove(player);
                    sender.sendMessage(ChatColor.GREEN + "Successfully enabled teleport requests!");
                    sender.sendMessage(ChatColor.GREEN + "You can now receive teleport requests.");
                }
            }
        } else if (label.equalsIgnoreCase("tpblock")) {
            if (sender instanceof Player){
                Player player = (Player)sender;
                if (args.length>0){
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    if (target == null){
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " The player doesn't exist.");
                        return false;
                    } else {
                        if (TeleportBlock.getBlockedPlayers(player).contains(target.getPlayer())){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " The player is already blocked.");
                            return false;
                        } else {
                            try {
                                TeleportBlock.addBlockedPlayer(player, target.getPlayer());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.GREEN + " has been blocked.");
                            return false;
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("tpunblock")) {
            if (sender instanceof Player){
                Player player = (Player)sender;
                if (args.length>0){
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    if (target == null){
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " The player doesn't exist.");
                        return false;
                    } else {
                        if (TeleportBlock.getBlockedPlayers(player).contains(target.getPlayer())){
                            try {
                                TeleportBlock.remBlockedPlayer(player, target.getPlayer());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sender.sendMessage(ChatColor.GREEN + "Successfully unblocked " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + " .");
                            return false;
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                    return false;
                }
            }
        }
        return false;
    }
}
