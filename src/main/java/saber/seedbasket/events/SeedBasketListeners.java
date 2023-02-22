package saber.seedbasket.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import saber.seedbasket.SeedBasket;
import saber.seedbasket.utils.CropsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SeedBasketListeners implements Listener {

    private final SeedBasket plugin;

    public SeedBasketListeners(SeedBasket p1){
        plugin = p1;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){

        // Check is it is the Seed Basket
        if (e.getItem() == null || !e.getItem().getItemMeta().getPersistentDataContainer().has(plugin.BasketKey, PersistentDataType.BYTE)) return;


        // Check for left click, on left click plant the crops
        if (e.getAction() == Action.LEFT_CLICK_BLOCK){

            // Don't let the basket break crops to avoid breaking crops that were just planted
            if (CropsUtil.isCropBlock(e.getClickedBlock().getType())) {
                e.setCancelled(true);
                return;
            }

            // Check perms
            if (!e.getPlayer().hasPermission("seedbasket.use")) return;

            // Only do stuff if farmland is clicked
            if (e.getClickedBlock().getType() != Material.FARMLAND) return;
            Block clicked = e.getClickedBlock();

            // Get the direction the player is looking and simplify to +X, -X, +Z, or -Z
            int xoff = 0;
            int zoff = 0;
            double x = e.getPlayer().getLocation().getDirection().getX();
            double z = e.getPlayer().getLocation().getDirection().getZ();
            if (Math.abs(x) > Math.abs(z)){
                if (x>0) xoff=1;
                else xoff=-1;
            }else{
                if (z>0) zoff=1;
                else zoff=-1;
            }

            // Extract what is currently in the seed basket from the lore
            String croplore = e.getItem().getItemMeta().getLore().get(0);
            int amount = Integer.parseInt(e.getItem().getItemMeta().getLore().get(1));
            Material crop = CropsUtil.getBlockFromLore(croplore);

            // Loop through the blocks in front and plant any places that have air above and farmland below
            Block cur, above;
            for (int i = 0; i < plugin.getConfig().getInt("cropsPlanted"); i++){
                cur = clicked.getRelative(xoff*i,0,zoff*i);
                above = cur.getRelative(0,1,0);
                if (amount > 0 && cur.getType() == Material.FARMLAND && above.getType() == Material.AIR) {
                    amount--;
                    above.setType(crop);
                }
            }

            // Update the basket and place back in player's hand
            ItemStack basket = e.getItem();
            ItemStack clone = basket.clone();
            basket.setAmount(1);

            if (e.getHand() == EquipmentSlot.OFF_HAND) e.getPlayer().getInventory().setItemInOffHand(setBasketLore(basket, croplore, amount));
            else e.getPlayer().getInventory().setItemInMainHand(setBasketLore(basket,croplore,amount));

            // If the player was using a stack of baskets, give the rest back without changing them
            if (clone.getAmount() != 1){
                clone.setAmount(clone.getAmount()-1);
                HashMap<Integer,ItemStack> noRoom = e.getPlayer().getInventory().addItem(clone);
                if (!noRoom.isEmpty()){
                    e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), noRoom.get(0));
                }
            }


        }
        // on Right click open the seed basket inventory
        else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){

            // Don't let them place the basket
            e.setCancelled(true);

            // Check perms
            if (!e.getPlayer().hasPermission("seedbasket.use")) return;

            // Basket can become invisible when using in off-hand so don't allow it
            if (e.getHand() == EquipmentSlot.OFF_HAND) return;

            Inventory BasketInv = Bukkit.createInventory(e.getPlayer(), 54, plugin.BasketName);

            // Extract the contents of the seed basket and open an inventory with that stuff
            if (e.getItem().getItemMeta().hasLore()){
                List<String> lore = e.getItem().getItemMeta().getLore();
                Material seeds = CropsUtil.getItemFromLore(lore.get(0));
                if (seeds != null) {
                    int amount = Integer.parseInt(lore.get(1));
                    ItemStack seedstack = new ItemStack(seeds);
                    seedstack.setAmount(amount);
                    BasketInv.addItem(seedstack);
                }
            }
            e.getPlayer().openInventory(BasketInv);
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){

        // Make sure it's the seed basket inventory
        if (e.getView().getTitle().equalsIgnoreCase(plugin.BasketName)){

            // Stop them from using number keys
            if (e.getClick() == ClickType.NUMBER_KEY){
                e.setCancelled(true);
                return;
            }

            // loop through the inventory to see what crop is inside it
            Material inside = null;
            if (!e.getInventory().isEmpty()) {
                for (ItemStack x : e.getInventory()){
                    if (x != null){
                        inside = x.getType();
                        break;
                    }
                }
            }

            // Check what item the player is attempting to move, stop them if it isn't either the crop that is already there or if the basket is empty, make sure it is a valid crop
            if (!((CropsUtil.isAllowed(e.getCursor(),inside) && CropsUtil.isAllowedorAirorNull(e.getCurrentItem(),inside))
                || (CropsUtil.isAllowed(e.getCurrentItem(),inside) && CropsUtil.isAllowedorAirorNull(e.getCursor(),inside)))){
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){

        // Check that it is the seed basket inventory
        if (e.getView().getTitle().equalsIgnoreCase(plugin.BasketName)){

            // Loop through the inventory to get the crop type and add up the total amount
            Material mat = null;
            int amount = 0;
            for (ItemStack x : e.getInventory()){
                if (x != null){
                    if (mat == null) mat = x.getType();
                    amount += x.getAmount();
                }
            }

            // Get the basket and update the lore with the contents found in the inventory
            ItemStack basket = e.getPlayer().getInventory().getItemInMainHand();
            ItemStack clone = basket.clone();
            basket.setAmount(1);
            e.getPlayer().getInventory().setItemInMainHand(setBasketLore(basket,CropsUtil.getLoreFromItem(mat),amount));

            // Give back the rest of the stack if the player right-clicked with a stack
            if (clone.getAmount() != 1){
                clone.setAmount(clone.getAmount()-1);
                HashMap<Integer,ItemStack> noRoom = e.getPlayer().getInventory().addItem(clone);
                if (!noRoom.isEmpty()){
                    e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), noRoom.get(0));
                }
            }

        }
    }

    // Used to update the basket lore
    private ItemStack setBasketLore(ItemStack basket, String crop, int amount){
        List<String> lore = new ArrayList<>();
        if (amount > 0) {
            lore.add(crop);
            lore.add(String.valueOf(amount));
        }
        ItemMeta basketmeta = basket.getItemMeta();
        basketmeta.setLore(lore);
        basket.setItemMeta(basketmeta);
        return basket;
    }
}
