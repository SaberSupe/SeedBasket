package saber.seedbasket;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import saber.seedbasket.commands.SeedBasketCommand;
import saber.seedbasket.events.SeedBasketListeners;

import java.util.logging.Level;

public final class SeedBasket extends JavaPlugin {

    public NamespacedKey BasketKey;
    public String BasketName;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Load Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Initialize Global variables
        BasketKey = new NamespacedKey(this, "SeedBasket");
        BasketName = ChatColor.translateAlternateColorCodes('&',getConfig().getString("seedBasketName"));

        // Register listener
        getServer().getPluginManager().registerEvents(new SeedBasketListeners(this), this);

        // Register Command
        getCommand("seedbasket").setExecutor(new SeedBasketCommand(this));

        // Log successful launch
        this.getLogger().log(Level.INFO, "SeedBasket loaded Successfully");
    }

    // Makes a Seed Basket
    public ItemStack getSeedBasket(){

        ItemStack basket = new ItemStack(Material.COMPOSTER);
        ItemMeta basketmeta = basket.getItemMeta();
        basketmeta.addEnchant(Enchantment.LUCK, 1, true);
        basketmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        basketmeta.setDisplayName(BasketName);
        basketmeta.getPersistentDataContainer().set(BasketKey, PersistentDataType.BYTE, (byte) 1);
        basket.setItemMeta(basketmeta);

        return basket;
    }
}
