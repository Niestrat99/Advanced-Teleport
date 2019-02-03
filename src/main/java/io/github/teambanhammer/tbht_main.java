package io.github.teambanhammer;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import com.wimbli.WorldBorder.WorldBorder;
import fanciful.FancyMessage;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class tbht_main extends JavaPlugin implements Listener {

private List<Player>tpoff = new ArrayList<>();

private HashMap<Player, BukkitRunnable>cooldown = new HashMap<>();

private HashMap<Player, BukkitRunnable>movement = new HashMap<>();

private static Economy Vault;
private static WorldBorder worldBorder;

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        Vault = rsp.getProvider();
        return Vault != null;
    }

    @Override
    public void onEnable (){
        System.out.println("AdvancedTeleport is now enabled!");
        System.out.println("AdvancedTeleport Version: " + getDescription().getVersion());
        getServer().getPluginManager().registerEvents(this, this);
        try {
            configuration.setDefaults();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupEconomy();
        if (getServer().getPluginManager().getPlugin("WorldBorder") != null) {
            worldBorder = (WorldBorder) getServer().getPluginManager().getPlugin("WorldBorder");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("tphelp")) {
            if (sender.hasPermission("tbh.tp.member.help")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Teleport Commands");
                    sender.sendMessage(ChatColor.AQUA + "/tpa <Player> - " + ChatColor.GREEN + "Sends the targeted player a teleport request to where they are.");
                    sender.sendMessage(ChatColor.AQUA + "/tpahere <Player> - " + ChatColor.GREEN + "Sends the targeted player a teleport requests to where you are.");
                    sender.sendMessage(ChatColor.AQUA + "/tpcancel - " + ChatColor.GREEN + "Cancels your teleport request.");
                    sender.sendMessage(ChatColor.AQUA + "/tpayes - " + ChatColor.GREEN + "Accepts a teleport request someone sent you.");
                    sender.sendMessage(ChatColor.AQUA + "/tpano - " + ChatColor.GREEN + "Declines a teleport request someone sent you.");
                    sender.sendMessage(ChatColor.AQUA + "/tpon - " + ChatColor.GREEN + "Enables receiving teleport requests.");
                    sender.sendMessage(ChatColor.AQUA + "/tpoff - " + ChatColor.GREEN + "Disables receiving teleport requests.");
                    sender.sendMessage(ChatColor.AQUA + "/tpr - " + ChatColor.GREEN + "Teleports you to a random place.");
                    return false;
                } else {
                    if (args[0].equalsIgnoreCase("admin")) {
                        if (sender.hasPermission("tbh.tp.admin.help")) {
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Teleport Commands for Admins");
                            sender.sendMessage(ChatColor.RED + "/tpaall - " + ChatColor.GOLD + "Sends everyone a teleport request to where you are.");
                            sender.sendMessage(ChatColor.RED + "/tpohere <Player> - " + ChatColor.GOLD + "Teleports the player to you instantly.");
                            sender.sendMessage(ChatColor.RED + "/tpo <Player> - " + ChatColor.GOLD + "Teleports you to another player instantly.");
                            return false;
                        } else {
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to this command!");
                            return false;
                        }
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpa")) {
            if (sender.hasPermission("tbh.tp.member.tpa")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cooldown.containsKey(player)) {
                        sender.sendMessage(ChatColor.RED + "This command has a cooldown of " + configuration.commandCooldown() + " seconds each use - Please wait!");
                        return false;
                    }
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase(player.getName())){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You cannot send a teleport request to yourself!");
                            return false;
                        }
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " Either the player is currently offline or doesn't exist.");
                            return false;
                        } else {
                            if (tpoff.contains(target)) {
                                sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.RED + " has disabled to receive teleport requests!");
                                return false;
                            }
                            if (TeleportBlock.getBlockedPlayers(target).contains(player)) {
                                sender.sendMessage(ChatColor.RED + "You can not teleport to " + ChatColor.YELLOW + target.getName() + ChatColor.RED + "!");
                                return false;
                            }
                            if (TeleportRequest.getRequestByReqAndResponder(target, player) != null) {
                                sender.sendMessage(ChatColor.RED + "You already have sent a teleport request to " + ChatColor.YELLOW + target.getName() + ChatColor.RED + "!");
                                return false;
                            }
                            if (configuration.EXPPayment()){
                                if (player.getLevel()<configuration.EXPTeleportPrice()){
                                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You do not have enough EXP Levels to send a teleport request to someone else!");
                                    player.sendMessage(ChatColor.RED + "You need atleast " + ChatColor.YELLOW + configuration.EXPTeleportPrice() + ChatColor.RED + " EXP Levels!");
                                    return false;
                                }
                            }
                            if (Vault != null && configuration.useVault()) {
                                if (Vault.getBalance(player)<configuration.teleportPrice()){
                                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You do not have enough money to send a teleport request to someone else!");
                                    player.sendMessage(ChatColor.RED + "You need atleast $" + ChatColor.YELLOW + configuration.teleportPrice() + ChatColor.RED + "!");
                                    return false;
                                }
                            }
                            sender.sendMessage(ChatColor.GREEN + "Teleport request send to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");
                            sender.sendMessage(ChatColor.GREEN + "They've got " + ChatColor.AQUA + configuration.requestLifetime() + ChatColor.GREEN + " seconds to respond!");
                            sender.sendMessage(ChatColor.GREEN + "To cancel the request use " + ChatColor.AQUA + "/tpcancel " + ChatColor.GREEN + "to cancel it.");
                            target.sendMessage(ChatColor.GREEN + "The Player " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " wants to teleport to you!");
                            target.sendMessage(ChatColor.GREEN + "If you want to accept it use " + ChatColor.AQUA + "/tpayes " + ChatColor.GREEN + ", if not use" + ChatColor.AQUA + "/tpano" + ChatColor.GREEN + ".");
                            target.sendMessage(ChatColor.GREEN + "You've got " + ChatColor.AQUA + configuration.requestLifetime() + ChatColor.GREEN + " seconds to respond to the request!");
                            BukkitRunnable run = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    sender.sendMessage(ChatColor.GREEN + "Your teleport request to " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " has expired!");
                                    TeleportRequest.removeRequest(TeleportRequest.getRequestByReqAndResponder(target, player));
                                }
                            };
                            run.runTaskLater(this, configuration.requestLifetime()*20); // 60 seconds
                            TeleportRequest request = new TeleportRequest(player, target, run, TeleportRequest.TeleportType.TPA_NORMAL); // Creates a new teleport request.
                            TeleportRequest.addRequest(request);
                            BukkitRunnable cooldowntimer = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    cooldown.remove(player);
                                }
                            };
                            cooldown.put(player, cooldowntimer);
                            cooldowntimer.runTaskLater(this, configuration.commandCooldown()*20); // 20 ticks = 1 second
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                        return false;
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpahere")) {
            if (sender.hasPermission("tbh.tp.member.here")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cooldown.containsKey(player)) {
                        sender.sendMessage(ChatColor.RED + "This command has a cooldown of " + configuration.commandCooldown() + " seconds each use - Please wait!");
                        return false;
                    }
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase(player.getName())){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You cannot send a teleport request to yourself!");
                            return false;
                        }
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " Either the player is currently offline or doesn't exist.");
                            return false;
                        } else {
                            if (tpoff.contains(target)) {
                                sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.RED + " has disabled to receive teleport requests!");
                                return false;
                            }
                            if (TeleportBlock.getBlockedPlayers(target).contains(player)) {
                                sender.sendMessage(ChatColor.RED + "You can not teleport to " + ChatColor.YELLOW + target.getName() + ChatColor.RED + "!");
                                return false;
                            }
                            if (TeleportRequest.getRequestByReqAndResponder(target, player) != null) {
                                sender.sendMessage(ChatColor.RED + "You already have sent a teleport request to " + ChatColor.YELLOW + target.getName() + ChatColor.RED + "!");
                                return false;
                            }
                            if (configuration.EXPPayment()){
                                if (player.getLevel()<configuration.EXPTeleportPrice()){
                                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You do not have enough EXP Levels to send a teleport request to someone else!");
                                    player.sendMessage(ChatColor.RED + "You need atleast " + ChatColor.YELLOW + configuration.EXPTeleportPrice() + ChatColor.RED + " EXP Levels!");
                                    return false;
                                }
                            }
                            if (Vault != null && configuration.useVault()) {
                                if (Vault.getBalance(player)<configuration.teleportPrice()){
                                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You do not have enough money to send a teleport request to someone else!");
                                    player.sendMessage(ChatColor.RED + "You need atleast $" + ChatColor.YELLOW + configuration.teleportPrice() + ChatColor.RED + "!");
                                    return false;
                                }
                            }
                            sender.sendMessage(ChatColor.GREEN + "Teleport request send to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");
                            sender.sendMessage(ChatColor.GREEN + "They've got " + ChatColor.AQUA + configuration.requestLifetime() + ChatColor.GREEN + " seconds to respond!");
                            sender.sendMessage(ChatColor.GREEN + "To cancel the request use " + ChatColor.AQUA + "/tpcancel " + ChatColor.GREEN + "to cancel it.");
                            target.sendMessage(ChatColor.GREEN + "The Player " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " wants to teleport you to them!");
                            target.sendMessage(ChatColor.GREEN + "If you want to accept it use " + ChatColor.AQUA + "/tpayes " + ChatColor.GREEN + ", if not use" + ChatColor.AQUA + "/tpano" + ChatColor.GREEN + ".");
                            target.sendMessage(ChatColor.GREEN + "You've got " + ChatColor.AQUA + configuration.requestLifetime() + ChatColor.GREEN + " seconds to respond to the request!");
                            BukkitRunnable run = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    sender.sendMessage(ChatColor.GREEN + "Your teleport request to " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " has expired!");
                                    TeleportRequest.removeRequest(TeleportRequest.getRequestByReqAndResponder(target, player));
                                }
                            };
                            run.runTaskLater(this, configuration.requestLifetime()*20); // 1200 ticks = 60 seconds
                            TeleportRequest request = new TeleportRequest(player, target, run, TeleportRequest.TeleportType.TPA_HERE); // Creates a new teleport request.
                            TeleportRequest.addRequest(request);
                            BukkitRunnable cooldowntimer = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    cooldown.remove(player);
                                }
                            };
                            cooldown.put(player, cooldowntimer);
                            cooldowntimer.runTaskLater(this, configuration.commandCooldown()*20); // 20 ticks = 1 second
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                        return false;
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpayes")) {
            if (sender.hasPermission("tbh.tp.member.yes")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (teleportTests(player, args, "tpayes")) {
                        Player target;
                        if (args.length > 0) {
                            target = Bukkit.getPlayer(args[0]);
                        } else {
                            target = TeleportRequest.getRequests(player).get(0).getRequester();
                        }

                        TeleportRequest request = TeleportRequest.getRequestByReqAndResponder(player, target);
                        acceptRequest(request);
                        return false;
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpano")) {
            if (sender.hasPermission("tbh.tp.member.no")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (teleportTests(player, args, "tpano")) {
                        Player target = Bukkit.getPlayer(args[0]);
                        TeleportRequest request = TeleportRequest.getRequestByReqAndResponder(player, target);
                        request.getRequester().sendMessage(ChatColor.YELLOW + "" + player.getName() + ChatColor.GREEN + " has declined your teleport request!");
                        player.sendMessage(ChatColor.GREEN + "You've declined the teleport request!");
                        request.destroy();
                        return false;
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpo")) {
            if (sender.hasPermission("tbh.tp.admin.tpo")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase(player.getName())){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You cannot teleport to yourself!");
                            return false;
                        }
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
            } else {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to this command!");
                return false;
            }
        } else if (label.equalsIgnoreCase("tpcancel")) {
            if (sender.hasPermission("tbh.tp.member.cancel")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    // Checks if any players have sent a request at all.
                    if (!TeleportRequest.getRequests(player).isEmpty()) {
                        // Checks if there's more than one request.
                        if (TeleportRequest.getRequests(player).size() > 1) {
                            // If the player has specified the request they're accepting.
                            if (args.length > 0) {
                                Player target = Bukkit.getPlayer(args[0]);
                                if (target == null) {
                                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " Either the player is currently offline or doesn't exist.");
                                    return false;
                                } else {
                                    TeleportRequest request = TeleportRequest.getRequestByReqAndResponder(player, target);
                                    if (request == null) {
                                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You don't have any pending requests from " + ChatColor.YELLOW + target.getName() + ChatColor.RED + "!");
                                        return false;
                                    } else {
                                        player.sendMessage(ChatColor.GREEN + "You have canceled your teleport request.");
                                        request.getResponder().sendMessage(ChatColor.YELLOW + "" + sender.getName() + ChatColor.RED + " has canceled their teleport request.");
                                        request.destroy();
                                        return false;
                                    }
                                }
                            } else {
                                // This utility helps in splitting lists into separate pages, like when you list your plots with PlotMe/PlotSquared.
                                PagedLists<TeleportRequest> requests = new PagedLists<>(TeleportRequest.getRequests(player), 8);
                                player.sendMessage(ChatColor.GREEN + "You have multiple teleport requests pending! Click one of the following to accept:");
                                for (TeleportRequest request : requests.getContentsInPage(1)) {
                                    new FancyMessage()
                                            .command("/tpacancel " + request.getRequester().getName())
                                            .color(ChatColor.AQUA)
                                            .text("> " + request.getRequester().getName())
                                            .send(player);
                                }
                                if (requests.getTotalPages() > 1) {
                                    player.sendMessage(ChatColor.GREEN + "Do /tpalist <Page Number> To check other requests.");
                                }

                            }
                        } else {
                            TeleportRequest request = TeleportRequest.getRequests(player).get(0);
                            request.getRequester().sendMessage(ChatColor.YELLOW + "" + player.getName() + ChatColor.GREEN + " has declined your teleport request!");
                            player.sendMessage(ChatColor.GREEN + "You've declined the teleport request!");
                            request.destroy();
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You don't have any pending requests!");
                        return false;
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpaall")) {
            if (sender.hasPermission("tbh.tp.admin.all")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cooldown.containsKey(player)) {
                        sender.sendMessage(ChatColor.RED + "This command has a cooldown of " + configuration.commandCooldown() + " seconds each use - Please wait!");
                        return false;
                    }
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (target != player) {
                            target.sendMessage(ChatColor.GREEN + "The Player " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " wants to teleport you to them!");
                            target.sendMessage(ChatColor.GREEN + "If you want to accept it use " + ChatColor.AQUA + "/tpayes " + ChatColor.GREEN + ", if not use" + ChatColor.AQUA + "/tpano" + ChatColor.GREEN + ".");
                            target.sendMessage(ChatColor.GREEN + "You've got " + ChatColor.AQUA + configuration.requestLifetime() + ChatColor.GREEN + " seconds to respond to the request!");
                            BukkitRunnable run = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    TeleportRequest.removeRequest(TeleportRequest.getRequestByReqAndResponder(target, player));
                                }
                            };
                            run.runTaskLater(this, configuration.requestLifetime()*20); // 60 seconds
                            TeleportRequest request = new TeleportRequest(player, target, run, TeleportRequest.TeleportType.TPA_HERE); // Creates a new teleport request.
                            TeleportRequest.addRequest(request);
                            BukkitRunnable cooldowntimer = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    cooldown.remove(player);
                                }
                            };
                            cooldown.put(player, cooldowntimer);
                            cooldowntimer.runTaskLater(this, configuration.commandCooldown()*20); // 20 ticks = 1 second
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to this command!");
                return false;
            }
        } else if (label.equalsIgnoreCase("tpoff")) {
            if (sender.hasPermission("tbh.tp.member.off")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!tpoff.contains(player)) {
                        tpoff.add(player);
                        sender.sendMessage(ChatColor.GREEN + "Successfully disabled teleport requests!");
                        sender.sendMessage(ChatColor.GREEN + "You can no longer receive any teleport requests.");
                        sender.sendMessage(ChatColor.AQUA + "If you want to receive teleport requests type " + ChatColor.YELLOW + "/tpon " + ChatColor.AQUA + "to enable it.");
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpon")) {
            if (sender.hasPermission("tbh.tp.member.on")) {
                if (sender instanceof Player) {
                    Player player = (Player)sender;
                    if (tpoff.contains(player)) {
                        tpoff.remove(player);
                        sender.sendMessage(ChatColor.GREEN + "Successfully enabled teleport requests!");
                        sender.sendMessage(ChatColor.GREEN + "You can now receive teleport requests.");
                    }
                }
            }
        } else if (label.equalsIgnoreCase("tpblock")) {
            if (sender.hasPermission("tbh.tp.member.block")) {
                if (sender instanceof Player){
                    Player player = (Player)sender;
                    if (args.length>0){
                        if (args[0].equalsIgnoreCase(player.getName())){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You cannot block yourself!");
                            return false;
                        }
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
            }
        } else if (label.equalsIgnoreCase("tpunblock")) {
            if (sender.hasPermission("tbh.tp.member.unblock")) {
                if (sender instanceof Player){
                    Player player = (Player)sender;
                    if (args.length>0){
                        if (args[0].equalsIgnoreCase(player.getName())){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You've never blocked yourself.");
                            return false;
                        }
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
        } else if (label.equalsIgnoreCase("tpalist")) {
            if (sender.hasPermission("tbh.tp.member.list")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    // If there are actually any pending teleport requests.
                    if (!TeleportRequest.getRequests(player).isEmpty()) {
                        if (args.length > 0) {
                            // Check if the argument can be parsed as an actual number.
                            // ^ means at the start of the string.
                            // [0-9] means any number in the range of 0 to 9.
                            // + means one or more of, allowing two or three digits.
                            // $ means the end of the string.
                            if (args[0].matches("^[0-9]+$")) {
                                // args[0] is officially an int.
                                int page = Integer.parseInt(args[0]);
                                PagedLists<TeleportRequest> requests = new PagedLists<>(TeleportRequest.getRequests(player), 8);
                                player.sendMessage(ChatColor.GREEN + "Click one of the following to accept:");
                                try {
                                    for (TeleportRequest request : requests.getContentsInPage(page)) {
                                        new FancyMessage()
                                                .command("/tpayes " + request.getRequester().getName())
                                                .color(ChatColor.AQUA)
                                                .text("> " + request.getRequester().getName())
                                                .send(player);
                                    }
                                } catch (IllegalArgumentException ex) {
                                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You've inserted an invalid page number!");
                                }

                            } else {
                                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You've inserted an invalid page number!");

                            }
                        } else {
                            PagedLists<TeleportRequest> requests = new PagedLists<>(TeleportRequest.getRequests(player), 8);
                            player.sendMessage(ChatColor.GREEN + "Click one of the following to accept:");
                            for (TeleportRequest request : requests.getContentsInPage(1)) {
                                new FancyMessage()
                                        .command("/tpayes " + request.getRequester().getName())
                                        .color(ChatColor.AQUA)
                                        .text("> " + request.getRequester().getName())
                                        .send(player);
                            }
                            return false;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You don't have any pending requests!");
                        return false;
                    }

                }
            }
        } else if (label.equalsIgnoreCase("tpohere")){
            if (sender.hasPermission("tbh.tp.admin.tpohere")){
                if (sender instanceof Player){
                    Player player = (Player)sender;
                    if (args.length>0){
                        if (args[0].equalsIgnoreCase(player.getName())){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You cannot teleport to yourself!");
                            return false;
                        }
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " Either the player is currently offline or doesn't exist.");
                            return false;
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Teleporting " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + " to you!");
                            target.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + "!");
                            target.teleport(player);
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                        return false;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to this command!");
                return false;
            }
        } else if (label.equalsIgnoreCase("tpr")){
            if (sender.hasPermission("tbh.tp.member.tpr")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cooldown.containsKey(player)) {
                        sender.sendMessage(ChatColor.RED + "This command has a cooldown of " + configuration.commandCooldown() + " seconds each use - Please wait!");
                        return false;
                    }
                    if (configuration.EXPPayment()){
                        if (player.getLevel()<configuration.EXPTPRCost()){
                            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You do not have enough EXP Levels to send a teleport request to someone else!");
                            player.sendMessage(ChatColor.RED + "You need atleast " + ChatColor.YELLOW + configuration.EXPTPRCost() + ChatColor.RED + " EXP Levels!");
                            return false;
                        }
                    }
                    if (Vault != null && configuration.useVault()) {
                        if (Vault.getBalance(player)<configuration.vaultTPRCost()){
                            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You do not have enough money to send a teleport request to someone else!");
                            player.sendMessage(ChatColor.RED + "You need atleast $" + ChatColor.YELLOW + configuration.vaultTPRCost() + ChatColor.RED + "!");
                            return false;
                        }
                    }
                    int x = getRandomCoords(configuration.minX(), configuration.maxX());
                    int z = getRandomCoords(configuration.minZ(), configuration.maxZ());
                    if (configuration.useWorldBorder() && worldBorder != null) {
                        BorderData border = Config.Border(player.getWorld().getName());
                        // If a border has been set
                        if (border != null) {
                            // Surprisingly, Java allows this.
                            x = getRandomCoords(-border.getRadiusX(), border.getRadiusX());
                            z = getRandomCoords(-border.getRadiusZ(), border.getRadiusZ());
                        }
                    }

                    int y = 256;
                    Location location = new Location(player.getWorld(), x, y, z);
                    player.sendMessage(ChatColor.GREEN + "Searching for a location...");
                    boolean validLocation = false;
                    while (!validLocation) {
                        while (location.getBlock().getType() == Material.AIR) {
                            location.subtract(0, 1, 0);
                        }
                        if (location.getBlock().getType() == Material.WATER || location.getBlock().getType() == Material.LAVA) {
                            location = new Location(player.getWorld(), x, y, z);
                        } else {
                            location.add(0, 1, 0);
                            validLocation = true;
                        }
                    }
                    Chunk chunk = player.getWorld().getChunkAt(location);
                    chunk.load(true);
                    BukkitRunnable cooldowntimer = new BukkitRunnable() {
                        @Override
                        public void run() {
                            cooldown.remove(player);
                        }
                    };
                    cooldown.put(player, cooldowntimer);
                    cooldowntimer.runTaskLater(this, configuration.commandCooldown() * 20); // 20 ticks = 1 second
                    Location loc = location;
                    BukkitRunnable movementtimer = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(loc);
                            movement.remove(player);
                            sender.sendMessage(ChatColor.GREEN + "You've been teleported to a random place!");
                            if (configuration.EXPPayment()) {
                                if (player.getLevel()>configuration.EXPTeleportPrice()){
                                    int currentLevel = player.getLevel();
                                   player.setLevel(currentLevel - configuration.EXPTeleportPrice());
                                    player.sendMessage(ChatColor.GREEN + "You have paid " + ChatColor.AQUA + configuration.EXPTPRCost() + ChatColor.GREEN + " EXP Levels for your teleportation request. You now have " + ChatColor.AQUA + player.getLevel() + ChatColor.GREEN + " EXP Levels!");
                                }
                            }
                            if  (Vault != null && configuration.useVault()) {
                                if (Vault.getBalance(player)>configuration.teleportPrice()){
                                    EconomyResponse payment = Vault.withdrawPlayer(player , configuration.vaultTPRCost());
                                    if (payment.transactionSuccess()){
                                        player.sendMessage(ChatColor.GREEN + "You have paid $" + ChatColor.AQUA + configuration.vaultTPRCost() + ChatColor.GREEN + " for your teleportation request. You now have $" + ChatColor.AQUA + Vault.getBalance(player) + ChatColor.GREEN + "!");
                                    }
                                }
                            }
                        }
                    };
                    movement.put(player, movementtimer);
                    movementtimer.runTaskLater(this, configuration.teleportTimer() * 20);
                    player.sendMessage(ChatColor.GREEN + "Teleporting in " + ChatColor.AQUA + configuration.teleportTimer() + " seconds" + ChatColor.GREEN + ", please don't move!");
                    return false;

                }
            }

        }
        return false;
    }

    private static int getRandomCoords(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min)+1)+min ;
    }

    private void acceptRequest(TeleportRequest request) {
        Player player = request.getResponder();
        request.getRequester().sendMessage(ChatColor.YELLOW + "" + player.getName() + ChatColor.GREEN + " has accepted your teleport request!");
        player.sendMessage(ChatColor.GREEN + "You've accepted the teleport request!");
        if (request.getType() == TeleportRequest.TeleportType.TPA_HERE) {
            BukkitRunnable movementtimer = new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(request.getRequester());
                    movement.remove(player);
                    player.sendMessage(configuration.eventTeleport());
                    if (configuration.EXPPayment()) {
                        if (request.getRequester().getLevel()>configuration.EXPTeleportPrice()){
                            int currentLevel = request.getRequester().getLevel();
                            request.getRequester().setLevel(currentLevel - configuration.EXPTeleportPrice());
                            request.getRequester().sendMessage(ChatColor.GREEN + "You have paid " + ChatColor.AQUA + configuration.EXPTeleportPrice() + ChatColor.GREEN + " EXP Levels for your teleportation request. You now have " + ChatColor.AQUA + request.getRequester().getLevel() + ChatColor.GREEN + " EXP Levels!");
                        }
                    }
                    if  (Vault != null && configuration.useVault()) {
                        if (Vault.getBalance(request.getRequester())>configuration.teleportPrice()){
                            EconomyResponse payment = Vault.withdrawPlayer(request.getRequester() , configuration.teleportPrice());
                            if (payment.transactionSuccess()){
                                request.getRequester().sendMessage(ChatColor.GREEN + "You have paid $" + ChatColor.AQUA + configuration.teleportPrice() + ChatColor.GREEN + " for your teleportation request. You now have $" + ChatColor.AQUA + Vault.getBalance(request.getRequester()) + ChatColor.GREEN + "!");
                            }
                        }
                    }

                }
            };
            movement.put(player, movementtimer);
            movementtimer.runTaskLater(this, configuration.teleportTimer()*20);
            player.sendMessage(configuration.eventBeforeTP().replaceAll("\\{countdown}" , String.valueOf(configuration.teleportTimer())));
        } else {
            BukkitRunnable movementtimer = new BukkitRunnable() {
                @Override
                public void run() {
                    request.getRequester().teleport(player);
                    movement.remove(request.getRequester());
                    request.getRequester().sendMessage(configuration.eventTeleport());
                    if (configuration.EXPPayment()) {
                        if (request.getRequester().getLevel()>configuration.EXPTeleportPrice()){
                            int currentLevel = request.getRequester().getLevel();
                            request.getRequester().setLevel(currentLevel - configuration.EXPTeleportPrice());
                            request.getRequester().sendMessage(ChatColor.GREEN + "You have paid " + ChatColor.AQUA + configuration.EXPTeleportPrice() + ChatColor.GREEN + " EXP Levels for your teleportation request. You now have " + ChatColor.AQUA + request.getRequester().getLevel() + ChatColor.GREEN + " EXP Levels!");
                        }
                    }
                    if  (Vault != null && configuration.useVault()) {
                        if (Vault.getBalance(request.getRequester())>=configuration.teleportPrice()){
                            EconomyResponse payment = Vault.withdrawPlayer(request.getRequester() , configuration.teleportPrice());
                            if (payment.transactionSuccess()){
                                request.getRequester().sendMessage(ChatColor.GREEN + "You have paid $" + ChatColor.AQUA + configuration.teleportPrice() + ChatColor.GREEN + " for your teleportation request. You now have $" + ChatColor.AQUA + Vault.getBalance(request.getRequester()) + ChatColor.GREEN + "!");
                            } else {
                                request.getRequester().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + payment.errorMessage);
                            }
                        }


                    }
                }
            };
            movement.put(request.getRequester(), movementtimer);
            movementtimer.runTaskLater(this, configuration.teleportTimer()*20);
            request.getRequester().sendMessage(configuration.eventBeforeTP().replaceAll("\\{countdown}" , String.valueOf(configuration.teleportTimer())));
        }
        request.destroy();
    }

    private boolean teleportTests(Player player, String[] args, String type) {
        // Checks if any players have sent a request at all.
        if (!TeleportRequest.getRequests(player).isEmpty()) {
            // Checks if there's more than one request.
            if (TeleportRequest.getRequests(player).size() > 1) {
                // If the player has specified the request they're accepting.
                if (args.length > 0) {
                    // Get the player.
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " Either the player is currently offline or doesn't exist.");
                        return false;
                    } else {
                        // Get the request that was sent by the target.
                        TeleportRequest request = TeleportRequest.getRequestByReqAndResponder(player, target);
                        if (request == null) {
                            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You don't have any pending requests from " + ChatColor.YELLOW + target.getName() + ChatColor.RED + "!");
                            return false;
                        } else {
                            // Yes, the teleport request can be accepted/declined/cancelled.
                            return true;
                        }
                    }
                } else {
                    // This utility helps in splitting lists into separate pages, like when you list your plots with PlotMe/PlotSquared.
                    PagedLists<TeleportRequest> requests = new PagedLists<>(TeleportRequest.getRequests(player), 8);
                    player.sendMessage(ChatColor.GREEN + "You have multiple teleport requests pending! Click one of the following to " + (type.equalsIgnoreCase("tpayes") ? "accept" : "deny") + ":");
                    for (TeleportRequest request : requests.getContentsInPage(1)) {
                        new FancyMessage()
                                .command("/" + type + " " + request.getRequester().getName())
                                .color(ChatColor.AQUA)
                                .text("> " + request.getRequester().getName())
                                .send(player);
                    }
                    if (requests.getTotalPages() > 1) {
                        player.sendMessage(ChatColor.GREEN + "Do /tpalist <Page Number> To check other requests.");
                    }

                }
            } else {
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You don't have any pending requests!");
            return false;
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
