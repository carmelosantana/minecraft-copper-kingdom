package org.xpfarm.copperkingdom.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.xpfarm.copperkingdom.items.CopperWeapons;

public class CraftingListener implements Listener {

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
                    event.getInventory().setResult(newWeapon);
                }
            }
        }
    }
}
