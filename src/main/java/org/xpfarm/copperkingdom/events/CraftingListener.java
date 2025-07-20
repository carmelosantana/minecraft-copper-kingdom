package org.xpfarm.copperkingdom.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xpfarm.copperkingdom.CopperKingdom;
import org.xpfarm.copperkingdom.items.CopperWeapons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CraftingListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        
        // Check if the crafted item is a copper weapon
        if (CopperWeapons.isCopperWeapon(result)) {
            // Create a new copper weapon with fresh enchantment rolls
            CopperWeapons.WeaponType weaponType = CopperWeapons.getWeaponType(result);
            if (weaponType != null) {
                ItemStack newWeapon = CopperWeapons.createCopperWeapon(weaponType);
                if (newWeapon != null) {
                    // Check for blessed weapon chance
                    applyBlessedChance(newWeapon);
                    event.getInventory().setResult(newWeapon);
                }
            }
        }
    }

    /**
     * Apply blessed weapon chance and modify lore accordingly
     */
    private void applyBlessedChance(ItemStack weapon) {
        double blessedChance = CopperKingdom.getInstance().getConfig()
            .getDouble("copper_lore.healing.blessed_weapon_chance", 5.0) / 100.0;
        
        if (random.nextDouble() < blessedChance) {
            ItemMeta meta = weapon.getItemMeta();
            if (meta != null) {
                List<Component> lore = meta.lore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                
                // Add blessed lore
                lore.add(Component.text("✨ Blessed by Ancient Copper Magic ✨", NamedTextColor.GOLD));
                lore.add(Component.text("Cleanses poison and wither from nearby allies", NamedTextColor.AQUA));
                
                meta.lore(lore);
                weapon.setItemMeta(meta);
            }
        }
    }
}
