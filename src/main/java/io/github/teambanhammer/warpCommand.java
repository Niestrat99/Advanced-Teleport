package io.github.teambanhammer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class warpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getLabel().equalsIgnoreCase("warp")){
            if (commandSender.hasPermission("tbh.tp.member.warph")){
                if (args.length == 0) {
                    commandSender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Warp Commands");
                    commandSender.sendMessage(ChatColor.AQUA + "/warp - " + ChatColor.GREEN + "Gives you a list of sub commands from this command.");
                    commandSender.sendMessage(ChatColor.AQUA + "/warp <warp name> - " + ChatColor.GREEN + "Teleports you to a warp point you've set.");
                    commandSender.sendMessage(ChatColor.AQUA + "/warps - " + ChatColor.GREEN + "Gives you a list of warps.");
                    if (commandSender.hasPermission("tbh.tp.admin.warph")){
                        commandSender.sendMessage(ChatColor.AQUA + "/warp set - " + ChatColor.GREEN + "Sets a warp point at the place you are.");
                        commandSender.sendMessage(ChatColor.AQUA + "/warp delete - " + ChatColor.GREEN + "Deletes a warp point you've set.");
                        return false;
                    }
                    return false;
            }

            } else {
                if (args[0].equalsIgnoreCase("set")){
                    if (commandSender.hasPermission("tbh.tp.admin.warpset")){
                        if (args.length>1){
                            //TODO Finish code and create a config for warp storage
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + "You have to give this warp a name!");
                            return false;
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to this command!");
                        return false;
                    }
                }
            }
        }
        return false;
    }
}

//TODO Commands checklist:
// •warp set (WIP)
// •warp delete ()
// •warp <warp name> ()
// •warps ()