package org.xpfarm.copperkingdom.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.xpfarm.copperkingdom.CopperKingdom;
import org.xpfarm.copperkingdom.items.CopperWeapons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CopperRecipes {
    
    private static final List<NamespacedKey> registeredRecipes = new ArrayList<>();

    public static void registerRecipes(CopperKingdom plugin) {
        registerCopperSwordRecipe(plugin);
        registerCopperAxeRecipe(plugin);
        registerCopperPickaxeRecipe(plugin);
    }

    private static void registerCopperSwordRecipe(CopperKingdom plugin) {
        ConfigurationSection recipeConfig = plugin.getConfig().getConfigurationSection("recipes.copper_sword");
        if (recipeConfig == null || !recipeConfig.getBoolean("enabled", true)) {
            return;
        }

        ItemStack copperSword = CopperWeapons.createCopperWeapon(CopperWeapons.WeaponType.COPPER_SWORD);
        if (copperSword == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "copper_sword");
        ShapedRecipe recipe = new ShapedRecipe(key, copperSword);
        
        recipe.shape("C", "C", "S");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
        registeredRecipes.add(key);
        plugin.getLogger().info("Registered Copper Sword recipe");
    }

    private static void registerCopperAxeRecipe(CopperKingdom plugin) {
        ConfigurationSection recipeConfig = plugin.getConfig().getConfigurationSection("recipes.copper_axe");
        if (recipeConfig == null || !recipeConfig.getBoolean("enabled", true)) {
            return;
        }

        ItemStack copperAxe = CopperWeapons.createCopperWeapon(CopperWeapons.WeaponType.COPPER_AXE);
        if (copperAxe == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "copper_axe");
        ShapedRecipe recipe = new ShapedRecipe(key, copperAxe);
        
        recipe.shape("CC", "CS", " S");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
        registeredRecipes.add(key);
        plugin.getLogger().info("Registered Copper Axe recipe");
    }

    private static void registerCopperPickaxeRecipe(CopperKingdom plugin) {
        ConfigurationSection recipeConfig = plugin.getConfig().getConfigurationSection("recipes.copper_pickaxe");
        if (recipeConfig == null || !recipeConfig.getBoolean("enabled", true)) {
            return;
        }

        ItemStack copperPickaxe = CopperWeapons.createCopperWeapon(CopperWeapons.WeaponType.COPPER_PICKAXE);
        if (copperPickaxe == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "copper_pickaxe");
        ShapedRecipe recipe = new ShapedRecipe(key, copperPickaxe);
        
        recipe.shape("CCC", " S ", " S ");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
        registeredRecipes.add(key);
        plugin.getLogger().info("Registered Copper Pickaxe recipe");
    }

    public static void unregisterRecipes() {
        for (NamespacedKey key : registeredRecipes) {
            Iterator<org.bukkit.inventory.Recipe> recipeIterator = Bukkit.recipeIterator();
            while (recipeIterator.hasNext()) {
                org.bukkit.inventory.Recipe recipe = recipeIterator.next();
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    if (shapedRecipe.getKey().equals(key)) {
                        recipeIterator.remove();
                        break;
                    }
                }
            }
        }
        registeredRecipes.clear();
        CopperKingdom.getInstance().getLogger().info("Unregistered all copper weapon recipes");
    }
}
