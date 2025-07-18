package org.xpfarm.copperkingdom.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.xpfarm.copperkingdom.CopperKingdom;
import org.xpfarm.copperkingdom.items.CopperWeapons;
import org.xpfarm.copperkingdom.items.CopperArmor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CopperRecipes {
    
    private static final List<NamespacedKey> registeredRecipes = new ArrayList<>();

    public static void registerRecipes(CopperKingdom plugin) {
        registerCopperSwordRecipe(plugin);
        registerCopperAxeRecipe(plugin);
        registerCopperPickaxeRecipe(plugin);
        registerCopperHelmetRecipe(plugin);
        registerCopperChestplateRecipe(plugin);
        registerCopperLeggingsRecipe(plugin);
        registerCopperBootsRecipe(plugin);
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

    private static void registerCopperHelmetRecipe(CopperKingdom plugin) {
        ConfigurationSection recipeConfig = plugin.getConfig().getConfigurationSection("recipes.copper_helmet");
        if (recipeConfig == null || !recipeConfig.getBoolean("enabled", true)) {
            return;
        }

        ItemStack copperHelmet = CopperArmor.createCopperArmor(CopperArmor.ArmorType.COPPER_HELMET);
        if (copperHelmet == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "copper_helmet");
        ShapedRecipe recipe = new ShapedRecipe(key, copperHelmet);
        
        recipe.shape("CCC", "L L", "   ");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('L', Material.LEATHER);

        Bukkit.addRecipe(recipe);
        registeredRecipes.add(key);
        plugin.getLogger().info("Registered Copper Helmet recipe");
    }

    private static void registerCopperChestplateRecipe(CopperKingdom plugin) {
        ConfigurationSection recipeConfig = plugin.getConfig().getConfigurationSection("recipes.copper_chestplate");
        if (recipeConfig == null || !recipeConfig.getBoolean("enabled", true)) {
            return;
        }

        ItemStack copperChestplate = CopperArmor.createCopperArmor(CopperArmor.ArmorType.COPPER_CHESTPLATE);
        if (copperChestplate == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "copper_chestplate");
        ShapedRecipe recipe = new ShapedRecipe(key, copperChestplate);
        
        recipe.shape("C C", "CLC", "CCC");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('L', Material.LEATHER);

        Bukkit.addRecipe(recipe);
        registeredRecipes.add(key);
        plugin.getLogger().info("Registered Copper Chestplate recipe");
    }

    private static void registerCopperLeggingsRecipe(CopperKingdom plugin) {
        ConfigurationSection recipeConfig = plugin.getConfig().getConfigurationSection("recipes.copper_leggings");
        if (recipeConfig == null || !recipeConfig.getBoolean("enabled", true)) {
            return;
        }

        ItemStack copperLeggings = CopperArmor.createCopperArmor(CopperArmor.ArmorType.COPPER_LEGGINGS);
        if (copperLeggings == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "copper_leggings");
        ShapedRecipe recipe = new ShapedRecipe(key, copperLeggings);
        
        recipe.shape("CCC", "L L", "C C");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('L', Material.LEATHER);

        Bukkit.addRecipe(recipe);
        registeredRecipes.add(key);
        plugin.getLogger().info("Registered Copper Leggings recipe");
    }

    private static void registerCopperBootsRecipe(CopperKingdom plugin) {
        ConfigurationSection recipeConfig = plugin.getConfig().getConfigurationSection("recipes.copper_boots");
        if (recipeConfig == null || !recipeConfig.getBoolean("enabled", true)) {
            return;
        }

        ItemStack copperBoots = CopperArmor.createCopperArmor(CopperArmor.ArmorType.COPPER_BOOTS);
        if (copperBoots == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "copper_boots");
        ShapedRecipe recipe = new ShapedRecipe(key, copperBoots);
        
        recipe.shape("   ", "L L", "C C");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('L', Material.LEATHER);

        Bukkit.addRecipe(recipe);
        registeredRecipes.add(key);
        plugin.getLogger().info("Registered Copper Boots recipe");
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
