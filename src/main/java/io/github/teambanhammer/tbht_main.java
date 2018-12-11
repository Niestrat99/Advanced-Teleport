package io.github.teambanhammer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class tbht_main extends JavaPlugin {

    @Override
    public void onEnable (){
        System.out.println("TBH_Teleport is now enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("tphelp")){
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
        }
    }
}
