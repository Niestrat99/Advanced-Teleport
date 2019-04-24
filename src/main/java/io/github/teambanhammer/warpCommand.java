package io.github.teambanhammer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class warpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getLabel().matches("(advancedteleport:)?warp")) {
            if (configuration.featWarps()) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (commandSender.hasPermission("tbh.tp.admin.warpset")) {
                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;
                            Location warp = player.getLocation();
                            if (args.length > 1) {
                                try {
                                    Warps.setWarp(args[1], warp);
                                    commandSender.sendMessage(ChatColor.GREEN + "Successfully created the warp " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "!");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You have to give this warp a name!");
                                return false;
                            }
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to use this command!");
                        return false;
                    }
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (commandSender.hasPermission("tbh.tp.admin.warpdel")) {
                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;
                            if (args.length > 1) {
                                if (Warps.getWarps().containsKey(args[1])) {
                                    try {
                                        commandSender.sendMessage(ChatColor.GREEN + "Successfully deleted the warp " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "!");
                                        Warps.delWarp(args[1]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " This warp doesn't exist!");
                                }
                            } else {
                                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You have to include the warp's name!");
                                return false;
                            }
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to use this command!");
                        return false;
                    }
                } else if (commandSender.hasPermission("tbh.tp.member.warp")) {
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        if (Warps.getWarps().containsKey(args[0])) {
                            ((Player) commandSender).teleport(Warps.getWarps().get(args[0]));
                            commandSender.sendMessage(ChatColor.GREEN + "Successfully teleported to " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + "!");
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " This warp doesn't exist!");
                            return false;
                        }
                    }
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Warps " + ChatColor.RED + "is disabled!");
                return false;
            }
        } else if (command.getLabel().matches("(advancedteleport:)?warps")){
            if (configuration.featWarps()) {
                if (commandSender.hasPermission("tbh.tp.member.warps")){
                    StringBuilder wList = new StringBuilder();
                    wList.append(ChatColor.AQUA + "" + ChatColor.BOLD + "Warps: " + ChatColor.YELLOW);
                    for (String warp:Warps.getWarps().keySet()) {
                        wList.append(warp + ", ");
                    }
                    commandSender.sendMessage(wList.toString());
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Warps " + ChatColor.RED + "is disabled!");
                return false;
            }
        }
        return false;
        }
    }