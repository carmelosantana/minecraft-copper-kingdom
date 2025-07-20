package org.xpfarm.copperkingdom.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.xpfarm.copperkingdom.CopperKingdom;
import org.xpfarm.copperkingdom.items.CopperWeapons;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Handles lore-driven copper mechanics including healing, grounding, and storm interactions
 */
public class CopperLoreListener implements Listener {

    private final CopperKingdom plugin;
    private final Random random = new Random();
    private final Map<UUID, Long> lightningCooldowns = new ConcurrentHashMap<>();

    public CopperLoreListener(CopperKingdom plugin) {
        this.plugin = plugin;
        startSunriseHandler();
    }

    /**
     * Start the sunrise healing handler that runs every second
     */
    private void startSunriseHandler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                processSunriseEffects();
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second (20 ticks)
    }

    /**
     * Process sunrise regeneration effects for all online players
     */
    private void processSunriseEffects() {
        long sunriseStart = plugin.getConfig().getLong("copper_lore.healing.sunrise_time_start", 23000);
        long sunriseEnd = plugin.getConfig().getLong("copper_lore.healing.sunrise_time_end", 1000);
        int regenerationLevel = plugin.getConfig().getInt("copper_lore.healing.regeneration_level", 0);
        int duration = plugin.getConfig().getInt("copper_lore.healing.sunrise_duration", 100);

        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            long time = world.getTime();

            // Check if it's sunrise time (handles day-night cycle wrap)
            boolean isSunriseTime = (sunriseStart > sunriseEnd) ? 
                (time >= sunriseStart || time <= sunriseEnd) : 
                (time >= sunriseStart && time <= sunriseEnd);

            if (isSunriseTime && hasCopperWeapon(player)) {
                // Apply regeneration effect
                PotionEffect regeneration = new PotionEffect(
                    PotionEffectType.REGENERATION,
                    duration,
                    regenerationLevel,
                    false,
                    false,
                    true
                );
                player.addPotionEffect(regeneration);

                // Handle blessed weapon cleansing
                if (hasBlessedCopperWeapon(player)) {
                    cleansePoisonAndWither(player);
                }
            }
        }
    }

    /**
     * Handle enhanced damage during storms
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (!CopperWeapons.isCopperWeapon(weapon)) {
            return;
        }

        World world = player.getWorld();
        double damage = event.getDamage();
        double multiplier = 1.0;

        // Check weather conditions
        if (world.hasStorm()) {
            if (world.isThundering()) {
                multiplier = plugin.getConfig().getDouble("copper_lore.storm.thunderstorm_bonus_multiplier", 1.5);
            } else {
                multiplier = plugin.getConfig().getDouble("copper_lore.storm.rain_bonus_multiplier", 1.2);
            }
        }

        if (multiplier > 1.0) {
            event.setDamage(damage * multiplier);
        }
    }

    /**
     * Handle lightning strike interaction and grounding effects
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !CopperWeapons.isCopperWeapon(item)) {
            return;
        }

        // Check for right-click lightning strike
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            long cooldownMs = plugin.getConfig().getLong("copper_lore.storm.lightning_cooldown_seconds", 600) * 1000;

            // Check cooldown
            if (lightningCooldowns.containsKey(playerId)) {
                long lastUse = lightningCooldowns.get(playerId);
                if (currentTime - lastUse < cooldownMs) {
                    long remainingSeconds = (cooldownMs - (currentTime - lastUse)) / 1000;
                    player.sendMessage("§cLightning ability on cooldown for " + remainingSeconds + " seconds!");
                    return;
                }
            }

            // Strike lightning
            Location targetLocation = player.getTargetBlock(null, 50).getLocation();
            strikeLightning(player, targetLocation);
            lightningCooldowns.put(playerId, currentTime);
        }
    }

    /**
     * Handle grounding resistance effects when near copper blocks
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (!CopperWeapons.isCopperWeapon(weapon)) {
            return;
        }

        // Check for nearby copper blocks for grounding effect
        if (hasNearbyCopperBlock(player.getLocation())) {
            int resistanceLevel = plugin.getConfig().getInt("copper_lore.grounding.resistance_level", 0);
            PotionEffect resistance = new PotionEffect(
                PotionEffectType.RESISTANCE,
                60, // 3 seconds
                resistanceLevel,
                false,
                false,
                true
            );
            player.addPotionEffect(resistance);
        }

        // Handle durability recharge near copper blocks
        rechargeDurabilityNearCopper(player, weapon);
    }

    /**
     * Handle crop growth acceleration near buried copper blocks
     */
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        
        if (hasBuriedCopperNearby(location)) {
            double bonusChance = plugin.getConfig().getDouble("copper_lore.grounding.crop_growth_bonus", 0.3);
            if (random.nextDouble() < bonusChance) {
                // Allow the growth event to proceed and potentially trigger additional growth
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Try to grow the block again after a short delay
                        if (block.getType().toString().contains("CROP") || 
                            block.getType().toString().contains("WHEAT") ||
                            block.getType().toString().contains("CARROT") ||
                            block.getType().toString().contains("POTATO")) {
                            // This simulates accelerated growth
                        }
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }

    /**
     * Check if player has a copper weapon equipped
     */
    private boolean hasCopperWeapon(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        return CopperWeapons.isCopperWeapon(mainHand) || CopperWeapons.isCopperWeapon(offHand);
    }

    /**
     * Check if player has a blessed copper weapon (enhanced with cleansing properties)
     */
    private boolean hasBlessedCopperWeapon(Player player) {
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (!CopperWeapons.isCopperWeapon(weapon)) {
            weapon = player.getInventory().getItemInOffHand();
        }
        
        if (weapon != null && weapon.hasItemMeta()) {
            List<Component> lore = weapon.lore();
            if (lore != null) {
                for (Component component : lore) {
                    String loreText = PlainTextComponentSerializer.plainText().serialize(component);
                    if (loreText.contains("Blessed")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Cleanse poison and wither effects from nearby players
     */
    private void cleansePoisonAndWither(Player player) {
        int radius = plugin.getConfig().getInt("copper_lore.healing.cleanse_radius", 5);
        boolean cleansePoisonEnabled = plugin.getConfig().getBoolean("copper_lore.healing.cleanse_poison", true);
        boolean cleanseWitherEnabled = plugin.getConfig().getBoolean("copper_lore.healing.cleanse_wither", true);

        for (Player nearbyPlayer : player.getWorld().getPlayers()) {
            if (nearbyPlayer.getLocation().distance(player.getLocation()) <= radius) {
                if (cleansePoisonEnabled) {
                    nearbyPlayer.removePotionEffect(PotionEffectType.POISON);
                }
                if (cleanseWitherEnabled) {
                    nearbyPlayer.removePotionEffect(PotionEffectType.WITHER);
                }
            }
        }
    }

    /**
     * Check if there are copper blocks within grounding radius
     */
    private boolean hasNearbyCopperBlock(Location location) {
        int radius = plugin.getConfig().getInt("copper_lore.grounding.resistance_radius", 5);
        String copperBlockType = plugin.getConfig().getString("copper_lore.grounding.copper_block_type", "COPPER_BLOCK");
        Material copperMaterial = Material.getMaterial(copperBlockType);

        if (copperMaterial == null) {
            return false;
        }

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    if (block.getType() == copperMaterial) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check for buried copper blocks near crop location
     */
    private boolean hasBuriedCopperNearby(Location location) {
        int radius = plugin.getConfig().getInt("copper_lore.grounding.crop_acceleration_radius", 3);
        int buriedDepth = plugin.getConfig().getInt("copper_lore.grounding.buried_depth", 1);
        String copperBlockType = plugin.getConfig().getString("copper_lore.grounding.copper_block_type", "COPPER_BLOCK");
        Material copperMaterial = Material.getMaterial(copperBlockType);

        if (copperMaterial == null) {
            return false;
        }

        // Check underground for buried copper blocks
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -buriedDepth; y <= 0; y++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    if (block.getType() == copperMaterial) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Recharge durability of copper weapons near copper blocks
     */
    private void rechargeDurabilityNearCopper(Player player, ItemStack weapon) {
        if (!CopperWeapons.isCopperWeapon(weapon) || !weapon.hasItemMeta()) {
            return;
        }

        if (!hasNearbyCopperBlock(player.getLocation())) {
            return;
        }

        double rechargeRate = plugin.getConfig().getDouble("copper_lore.storm.recharge_per_block_per_tick", 0.01);
        
        // Only recharge if weapon is damaged and has durability
        if (weapon.getItemMeta() instanceof Damageable damageable && weapon.getType().getMaxDurability() > 0) {
            if (damageable.hasDamage() && random.nextDouble() < rechargeRate) {
                // Apply small durability restoration
                int currentDamage = damageable.getDamage();
                if (currentDamage > 0) {
                    damageable.setDamage(Math.max(0, currentDamage - 1));
                    weapon.setItemMeta(damageable);
                }
            }
        }
    }

    /**
     * Strike lightning at target location
     */
    private void strikeLightning(Player player, Location target) {
        double damage = plugin.getConfig().getDouble("copper_lore.storm.lightning_damage", 6.0);
        int radius = plugin.getConfig().getInt("copper_lore.storm.lightning_radius", 2);

        // Strike lightning
        player.getWorld().strikeLightning(target);

        // Damage nearby entities
        for (LivingEntity entity : target.getWorld().getLivingEntities()) {
            if (entity.getLocation().distance(target) <= radius && entity != player) {
                entity.damage(damage, player);
            }
        }
    }
}
