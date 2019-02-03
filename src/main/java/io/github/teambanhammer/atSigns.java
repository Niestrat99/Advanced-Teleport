package io.github.teambanhammer;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class atSigns implements Listener {

    @EventHandler
    public void atSigns (PlayerInteractEvent Sign){
        Player player = Sign.getPlayer();
        Block clickedBlock = Sign.getClickedBlock();
        BlockState state = clickedBlock.getState();

        if (state instanceof org.bukkit.block.Sign) {
            Sign sign = (Sign) state;
            String line1 = sign.getLine(0);
            if (line1.equalsIgnoreCase("[RandomTP]")){
                player.performCommand("tpr");
            }
        }
    }
    @EventHandler
    public void placeSign (BlockPlaceEvent Place){
        Block placeBlock = Place.getBlockPlaced();
        BlockState state = placeBlock.getState();

        if (state instanceof Sign) {
            Player placer = Place.getPlayer();
            Sign sign = (Sign) state;
            String line1 = sign.getLine(0);
            if (line1.equalsIgnoreCase("[RandomTP]")) {
                if (!placer.hasPermission("tbh.tp.admin.tprsign")){
                    placer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You do not have permission to make this sign!");
                    Place.setCancelled(true);
                } else {
                    sign.setLine(0, ChatColor.BLUE + "" + ChatColor.BOLD + "[RandomTP]");
                    sign.setLine(1, ChatColor.ITALIC + "Click me!");
                    sign.update();
                    placer.sendMessage(ChatColor.GREEN + "Successfully created the RandomTP sign!");
                }
            }
        }
    }
}
