package org.xpfarm.copperkingdom.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.xpfarm.copperkingdom.CopperKingdom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CopperWeapons {
    
    private static final Random random = new Random();
    private static final NamespacedKey COPPER_WEAPON_KEY = new NamespacedKey(CopperKingdom.getInstance(), "copper_weapon");
    private static final NamespacedKey HIGH_DURABILITY_KEY = new NamespacedKey(CopperKingdom.getInstance(), "high_durability");
    private static final NamespacedKey POISON_ON_HIT_KEY = new NamespacedKey(CopperKingdom.getInstance(), "poison_on_hit");
    private static final NamespacedKey BONUS_DAMAGE_KEY = new NamespacedKey(CopperKingdom.getInstance(), "bonus_damage");

    public enum WeaponType {
        COPPER_SWORD("copper_sword", Material.IRON_SWORD),
        COPPER_AXE("copper_axe", Material.IRON_AXE),
        COPPER_PICKAXE("copper_pickaxe", Material.IRON_PICKAXE);

        private final String configKey;
        private final Material baseMaterial;

        WeaponType(String configKey, Material baseMaterial) {
            this.configKey = configKey;
            this.baseMaterial = baseMaterial;
        }

        public String getConfigKey() { return configKey; }
        public Material getBaseMaterial() { return baseMaterial; }
    }

    public static ItemStack createCopperWeapon(WeaponType type) {
        ConfigurationSection weaponConfig = CopperKingdom.getInstance().getConfig()
            .getConfigurationSection("weapons." + type.getConfigKey());
        ConfigurationSection enchantConfig = CopperKingdom.getInstance().getConfig()
            .getConfigurationSection("enchantments." + type.getConfigKey());
        
        if (weaponConfig == null) {
            return null;
        }

        ItemStack item = new ItemStack(type.getBaseMaterial());
        ItemMeta meta = item.getItemMeta();
        
        if (meta == null) {
            return null;
        }

        // Set display name
        String displayName = weaponConfig.getString("display_name", type.name());
        meta.displayName(Component.text(displayName, NamedTextColor.GOLD)
            .decoration(TextDecoration.ITALIC, false));

        // Set lore
        List<String> configLore = weaponConfig.getStringList("lore");
        List<Component> lore = new ArrayList<>();
        for (String line : configLore) {
            lore.add(Component.text(line, NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        }

        // Mark as copper weapon
        meta.getPersistentDataContainer().set(COPPER_WEAPON_KEY, PersistentDataType.STRING, type.getConfigKey());

        // Apply base damage
        double baseDamage = weaponConfig.getDouble("base_damage", 5.0);
        AttributeModifier damageModifier = new AttributeModifier(
            NamespacedKey.minecraft("copper_weapon_damage"),
            baseDamage - 1, // Subtract 1 because base tool damage is usually 1
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.MAINHAND
        );
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, damageModifier);

        // Apply base durability
        int baseDurability = weaponConfig.getInt("base_durability", 100);
        
        // Check for enchantment rolls
        if (enchantConfig != null) {
            boolean hasHighDurability = rollEnchantment(enchantConfig.getInt("high_durability", 0));
            boolean hasPoisonOnHit = rollEnchantment(enchantConfig.getInt("poison_on_hit", 0));
            boolean hasBonusDamage = rollEnchantment(enchantConfig.getInt("bonus_damage", 0));

            if (hasHighDurability) {
                meta.getPersistentDataContainer().set(HIGH_DURABILITY_KEY, PersistentDataType.BYTE, (byte) 1);
                double multiplier = CopperKingdom.getInstance().getConfig()
                    .getDouble("enhancements.high_durability_multiplier", 3.0);
                baseDurability = (int) (baseDurability * multiplier);
                lore.add(Component.text("✦ Enhanced Durability", NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false));
            }

            if (hasPoisonOnHit) {
                meta.getPersistentDataContainer().set(POISON_ON_HIT_KEY, PersistentDataType.BYTE, (byte) 1);
                lore.add(Component.text("☠ Poison on Hit", NamedTextColor.DARK_GREEN)
                    .decoration(TextDecoration.ITALIC, false));
            }

            if (hasBonusDamage) {
                meta.getPersistentDataContainer().set(BONUS_DAMAGE_KEY, PersistentDataType.BYTE, (byte) 1);
                double bonusDamage = CopperKingdom.getInstance().getConfig()
                    .getDouble("enhancements.bonus_damage_amount", 3.0);
                
                AttributeModifier bonusModifier = new AttributeModifier(
                    NamespacedKey.minecraft("copper_bonus_damage"),
                    bonusDamage,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.MAINHAND
                );
                meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, bonusModifier);
                
                lore.add(Component.text("⚔ Bonus Damage", NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false));
            }
        }

        // Apply durability using Unbreaking enchantment level calculation
        // Higher unbreaking = effectively higher durability
        int unbreakingLevel = Math.max(1, (int) Math.log(baseDurability / 100.0) + 1);
        meta.addEnchant(Enchantment.UNBREAKING, unbreakingLevel, true);

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private static boolean rollEnchantment(int chance) {
        return random.nextInt(100) < chance;
    }

    public static boolean isCopperWeapon(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(COPPER_WEAPON_KEY, PersistentDataType.STRING);
    }

    public static boolean hasPoisonOnHit(ItemStack item) {
        if (!isCopperWeapon(item)) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(POISON_ON_HIT_KEY, PersistentDataType.BYTE);
    }

    public static WeaponType getWeaponType(ItemStack item) {
        if (!isCopperWeapon(item)) {
            return null;
        }
        String typeKey = item.getItemMeta().getPersistentDataContainer()
            .get(COPPER_WEAPON_KEY, PersistentDataType.STRING);
        
        for (WeaponType type : WeaponType.values()) {
            if (type.getConfigKey().equals(typeKey)) {
                return type;
            }
        }
        return null;
    }
}
