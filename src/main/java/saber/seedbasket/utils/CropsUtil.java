package saber.seedbasket.utils;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CropsUtil {

    // Holds crops in form: Lore name, block name, item name
    private static final String[][] crops = {
            {"Carrots","CARROTS","CARROT"},
            {"Potatoes","POTATOES","POTATO"},
            {"Beetroot Seeds","BEETROOTS","BEETROOT_SEEDS"},
            {"Pumpkin Seeds","PUMPKIN_STEM","PUMPKIN_SEEDS"},
            {"Wheat Seeds","WHEAT","WHEAT_SEEDS"},
            {"Melon Seeds","MELON_STEM","MELON_SEEDS"}
    };

    public static String getLoreFromItem(Material item){
        // Get the appropriate lore name from the given item name
        if (item == null) return null;
        for (String[] crop : crops) {
            if (crop[2].equalsIgnoreCase(item.toString())) return crop[0];
        }
        return null;
    }

    public static Material getItemFromLore(String lore){
        // Get the appropriate item name from the given lore name
        if (lore == null) return null;
        for (String[] crop : crops) {
            if (crop[0].equalsIgnoreCase(lore)) return Material.getMaterial(crop[2]);
        }
        return null;
    }

    public static Material getBlockFromLore(String lore){
        // Get the appropriate block name from the given lore name
        if (lore == null) return null;
        for (String[] crop : crops) {
            if (crop[0].equalsIgnoreCase(lore)) return Material.getMaterial(crop[1]);
        }
        return null;
    }

    public static boolean isCropItem(Material material){
        // Check if the given material is a valid crop item
        if (material == null) return false;
        for (String[] crop : crops) {
            if (crop[2].equalsIgnoreCase(material.toString())) return true;
        }
        return false;
    }

    public static boolean isCropBlock(Material material){
        // Check if the given material is a valid crop block
        if (material == null) return false;
        for (String[] crop : crops) {
            if (crop[1].equalsIgnoreCase(material.toString())) return true;
        }
        return false;
    }

    // Checks if the given item is the same material as mat, if mat is null, compares to list of crops
    public static boolean isAllowed(ItemStack check, Material mat){
        if (check == null) return false;
        if (mat == null) return isCropItem(check.getType());
        return (check.getType() == mat);
    }

    // Checks if the given item is air, null, mat or if mat is null, a crop
    public static boolean isAllowedorAirorNull(ItemStack check, Material mat){
        if (check == null) return true;
        if (check.getType() == Material.AIR) return true;
        if (mat == null) return isCropItem(check.getType());
        return (check.getType() == mat);
    }

}
