package org.xpfarm.copperkingdom.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.xpfarm.copperkingdom.CopperKingdom;
import org.xpfarm.copperkingdom.items.CopperWeapons;

public class WeaponListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check if attacker is a player
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        // Check if victim is a living entity
        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }

        // Get the weapon used
        ItemStack weapon = player.getInventory().getItemInMainHand();
        
        // Check if it's a copper weapon with poison on hit
        if (CopperWeapons.hasPoisonOnHit(weapon)) {
            // Get poison settings from config
            int duration = CopperKingdom.getInstance().getConfig().getInt("enhancements.poison_duration", 60);
            int amplifier = CopperKingdom.getInstance().getConfig().getInt("enhancements.poison_amplifier", 1);
            
            // Apply poison effect
            PotionEffect poisonEffect = new PotionEffect(
                PotionEffectType.POISON,
                duration,
                amplifier,
                false,
                true,
                true
            );
            
            victim.addPotionEffect(poisonEffect);
        }
    }
}
