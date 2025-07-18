package org.xpfarm.copperkingdom;

import org.bukkit.plugin.java.JavaPlugin;
import org.xpfarm.copperkingdom.commands.CopperKingdomCommand;
import org.xpfarm.copperkingdom.events.CraftingListener;
import org.xpfarm.copperkingdom.events.WeaponListener;
import org.xpfarm.copperkingdom.recipes.CopperRecipes;

public final class CopperKingdom extends JavaPlugin {

    private static CopperKingdom instance;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Register recipes
        CopperRecipes.registerRecipes(this);
        
        // Register events
        getServer().getPluginManager().registerEvents(new CraftingListener(), this);
        getServer().getPluginManager().registerEvents(new WeaponListener(), this);
        
        // Register commands
        getCommand("copperkingdom").setExecutor(new CopperKingdomCommand());
        
        getLogger().info("Copper Kingdom has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Copper Kingdom has been disabled!");
    }

    public static CopperKingdom getInstance() {
        return instance;
    }
    
    public void reloadPluginConfig() {
        reloadConfig();
        // Re-register recipes with new config values
        CopperRecipes.unregisterRecipes();
        CopperRecipes.registerRecipes(this);
    }
}
