package saber.seedbasket.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import saber.seedbasket.SeedBasket;

import java.util.HashMap;


public class SeedBasketCommand implements CommandExecutor {

    private final SeedBasket plugin;

    public SeedBasketCommand(SeedBasket p1){

        plugin = p1;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Make sure it's the right command and they have perms
        if (!command.getName().equalsIgnoreCase("seedbasket")) return true;
        if (!sender.hasPermission("seedbasket.give")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("msgNoPerms")));
            return true;
        }

        if (args.length == 0){
            return false;
        }

        if (args.length > 1 && args[0].equalsIgnoreCase("give")){

            // Get player
            Player play = Bukkit.getPlayer(args[1]);

            // Check if player was found
            if (play == null){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("msgPlayerNotFound")));
                return true;
            }

            // Get a basket and set the stack to the amount given if it is a number
            ItemStack basket = plugin.getSeedBasket();
            if (args.length > 2 && args[2].matches("-?\\d+")) basket.setAmount(Integer.parseInt(args[2]));

            // Give the basket stack and throw the extra on the ground if there isn't enough inv space
            HashMap<Integer,ItemStack> noRoom = play.getInventory().addItem(basket);
            if (!noRoom.isEmpty()){
                play.getWorld().dropItemNaturally(play.getLocation(), noRoom.get(0));
            }
            return true;
        }

        return false;
    }


}
