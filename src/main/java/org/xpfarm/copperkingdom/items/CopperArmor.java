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

public class CopperArmor {
    
    private static final Random random = new Random();
    private static final NamespacedKey COPPER_ARMOR_KEY = new NamespacedKey(CopperKingdom.getInstance(), "copper_armor");
    private static final NamespacedKey ENHANCED_PROTECTION_KEY = new NamespacedKey(CopperKingdom.getInstance(), "enhanced_protection");
    private static final NamespacedKey SPEED_BOOST_KEY = new NamespacedKey(CopperKingdom.getInstance(), "speed_boost");
    private static final NamespacedKey THORNS_EFFECT_KEY = new NamespacedKey(CopperKingdom.getInstance(), "thorns_effect");

    public enum ArmorType {
        COPPER_HELMET("copper_helmet", Material.LEATHER_HELMET, EquipmentSlotGroup.HEAD),
        COPPER_CHESTPLATE("copper_chestplate", Material.LEATHER_CHESTPLATE, EquipmentSlotGroup.CHEST),
        COPPER_LEGGINGS("copper_leggings", Material.LEATHER_LEGGINGS, EquipmentSlotGroup.LEGS),
        COPPER_BOOTS("copper_boots", Material.LEATHER_BOOTS, EquipmentSlotGroup.FEET);

        private final String configKey;
        private final Material baseMaterial;
        private final EquipmentSlotGroup slot;

        ArmorType(String configKey, Material baseMaterial, EquipmentSlotGroup slot) {
            this.configKey = configKey;
            this.baseMaterial = baseMaterial;
            this.slot = slot;
        }

        public String getConfigKey() { return configKey; }
        public Material getBaseMaterial() { return baseMaterial; }
        public EquipmentSlotGroup getSlot() { return slot; }
    }

    public static ItemStack createCopperArmor(ArmorType type) {
        ConfigurationSection armorConfig = CopperKingdom.getInstance().getConfig()
            .getConfigurationSection("armor." + type.getConfigKey());
        ConfigurationSection enchantConfig = CopperKingdom.getInstance().getConfig()
            .getConfigurationSection("enchantments." + type.getConfigKey());
        
        if (armorConfig == null) {
            return null;
        }

        ItemStack item = new ItemStack(type.getBaseMaterial());
        ItemMeta meta = item.getItemMeta();
        
        if (meta == null) {
            return null;
        }

        // Set display name
        String displayName = armorConfig.getString("display_name", type.name());
        meta.displayName(Component.text(displayName, NamedTextColor.GOLD)
            .decoration(TextDecoration.ITALIC, false));

        // Set lore
        List<String> configLore = armorConfig.getStringList("lore");
        List<Component> lore = new ArrayList<>();
        for (String line : configLore) {
            lore.add(Component.text(line, NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        }

        // Mark as copper armor
        meta.getPersistentDataContainer().set(COPPER_ARMOR_KEY, PersistentDataType.STRING, type.getConfigKey());

        // Apply base armor value
        double baseArmor = armorConfig.getDouble("base_armor", 2.0);
        AttributeModifier armorModifier = new AttributeModifier(
            NamespacedKey.minecraft("copper_armor_protection"),
            baseArmor,
            AttributeModifier.Operation.ADD_NUMBER,
            type.getSlot()
        );
        meta.addAttributeModifier(Attribute.ARMOR, armorModifier);

        // Apply base armor toughness
        double baseToughness = armorConfig.getDouble("base_toughness", 0.0);
        if (baseToughness > 0) {
            AttributeModifier toughnessModifier = new AttributeModifier(
                NamespacedKey.minecraft("copper_armor_toughness"),
                baseToughness,
                AttributeModifier.Operation.ADD_NUMBER,
                type.getSlot()
            );
            meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, toughnessModifier);
        }

        // Apply base durability
        int baseDurability = armorConfig.getInt("base_durability", 100);
        
        // Check for enchantment rolls
        if (enchantConfig != null) {
            boolean hasEnhancedProtection = rollEnchantment(enchantConfig.getInt("enhanced_protection", 0));
            boolean hasSpeedBoost = rollEnchantment(enchantConfig.getInt("speed_boost", 0));
            boolean hasThornsEffect = rollEnchantment(enchantConfig.getInt("thorns_effect", 0));

            if (hasEnhancedProtection) {
                meta.getPersistentDataContainer().set(ENHANCED_PROTECTION_KEY, PersistentDataType.BYTE, (byte) 1);
                double protectionMultiplier = CopperKingdom.getInstance().getConfig()
                    .getDouble("enhancements.enhanced_protection_multiplier", 1.5);
                baseDurability = (int) (baseDurability * protectionMultiplier);
                
                // Add extra protection
                double extraProtection = CopperKingdom.getInstance().getConfig()
                    .getDouble("enhancements.extra_protection_amount", 2.0);
                AttributeModifier extraProtectionModifier = new AttributeModifier(
                    NamespacedKey.minecraft("copper_extra_protection"),
                    extraProtection,
                    AttributeModifier.Operation.ADD_NUMBER,
                    type.getSlot()
                );
                meta.addAttributeModifier(Attribute.ARMOR, extraProtectionModifier);
                
                lore.add(Component.text("✦ Enhanced Protection", NamedTextColor.BLUE)
                    .decoration(TextDecoration.ITALIC, false));
            }

            if (hasSpeedBoost) {
                meta.getPersistentDataContainer().set(SPEED_BOOST_KEY, PersistentDataType.BYTE, (byte) 1);
                double speedAmount = CopperKingdom.getInstance().getConfig()
                    .getDouble("enhancements.speed_boost_amount", 0.1);
                
                AttributeModifier speedModifier = new AttributeModifier(
                    NamespacedKey.minecraft("copper_speed_boost"),
                    speedAmount,
                    AttributeModifier.Operation.ADD_NUMBER,
                    type.getSlot()
                );
                meta.addAttributeModifier(Attribute.MOVEMENT_SPEED, speedModifier);
                
                lore.add(Component.text("⚡ Speed Boost", NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));
            }

            if (hasThornsEffect) {
                meta.getPersistentDataContainer().set(THORNS_EFFECT_KEY, PersistentDataType.BYTE, (byte) 1);
                int thornsLevel = CopperKingdom.getInstance().getConfig()
                    .getInt("enhancements.thorns_level", 1);
                meta.addEnchant(Enchantment.THORNS, thornsLevel, true);
                
                lore.add(Component.text("⚡ Thorns Effect", NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false));
            }
        }

        // Apply durability using Unbreaking enchantment level calculation
        int unbreakingLevel = Math.max(1, (int) Math.log(baseDurability / 100.0) + 1);
        meta.addEnchant(Enchantment.UNBREAKING, unbreakingLevel, true);

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private static boolean rollEnchantment(int chance) {
        return random.nextInt(100) < chance;
    }

    public static boolean isCopperArmor(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(COPPER_ARMOR_KEY, PersistentDataType.STRING);
    }

    public static boolean hasSpeedBoost(ItemStack item) {
        if (!isCopperArmor(item)) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(SPEED_BOOST_KEY, PersistentDataType.BYTE);
    }

    public static boolean hasThornsEffect(ItemStack item) {
        if (!isCopperArmor(item)) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(THORNS_EFFECT_KEY, PersistentDataType.BYTE);
    }

    public static ArmorType getArmorType(ItemStack item) {
        if (!isCopperArmor(item)) {
            return null;
        }
        String typeKey = item.getItemMeta().getPersistentDataContainer()
            .get(COPPER_ARMOR_KEY, PersistentDataType.STRING);
        
        for (ArmorType type : ArmorType.values()) {
            if (type.getConfigKey().equals(typeKey)) {
                return type;
            }
        }
        return null;
    }
}
