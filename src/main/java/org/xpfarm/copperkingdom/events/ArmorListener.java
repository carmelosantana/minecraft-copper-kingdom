package org.xpfarm.copperkingdom.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.xpfarm.copperkingdom.items.CopperArmor;

public class ArmorListener implements Listener {

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        // This event is mainly for weapons, but we could add armor-specific logic here if needed
        // For now, armor effects are handled through attribute modifiers
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Handle armor special effects when the player wearing armor takes damage
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Check all armor pieces for special effects
        ItemStack[] armor = player.getInventory().getArmorContents();
        boolean hasSpeedBoostArmor = false;

        for (ItemStack piece : armor) {
            if (piece != null && CopperArmor.isCopperArmor(piece)) {
                // Check if any armor piece has speed boost
                if (CopperArmor.hasSpeedBoost(piece)) {
                    hasSpeedBoostArmor = true;
                }
            }
        }

        // Apply speed boost effect if wearing copper armor with speed boost
        if (hasSpeedBoostArmor) {
            int duration = 100; // 5 seconds
            int amplifier = 0;  // Speed I
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
        }
    }
}
