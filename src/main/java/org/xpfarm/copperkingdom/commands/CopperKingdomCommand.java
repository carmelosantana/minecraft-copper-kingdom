package org.xpfarm.copperkingdom.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xpfarm.copperkingdom.CopperKingdom;
import org.xpfarm.copperkingdom.items.CopperWeapons;
import org.xpfarm.copperkingdom.items.CopperArmor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CopperKingdomCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                return handleGiveCommand(sender, args);
            case "reload":
                return handleReloadCommand(sender);
            case "test":
                return handleTestCommand(sender, args);
            case "blessed":
                return handleBlessedCommand(sender, args);
            case "help":
            default:
                showHelp(sender);
                return true;
        }
    }

    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("copperkingdom.give")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /copperkingdom give <item>", NamedTextColor.YELLOW));
            sender.sendMessage(Component.text("Available weapons: ", NamedTextColor.AQUA)
                .append(Component.text("copper_sword, copper_axe, copper_pickaxe", NamedTextColor.WHITE)));
            sender.sendMessage(Component.text("Available armor: ", NamedTextColor.AQUA)
                .append(Component.text("copper_helmet, copper_chestplate, copper_leggings, copper_boots", NamedTextColor.WHITE)));
            return true;
        }

        String itemName = args[1].toLowerCase();
        ItemStack item = null;

        // Try to create as weapon first
        try {
            CopperWeapons.WeaponType weaponType = CopperWeapons.WeaponType.valueOf("COPPER_" + itemName.toUpperCase().replace("COPPER_", ""));
            item = CopperWeapons.createCopperWeapon(weaponType);
        } catch (IllegalArgumentException e) {
            // Try to create as armor
            try {
                CopperArmor.ArmorType armorType = CopperArmor.ArmorType.valueOf("COPPER_" + itemName.toUpperCase().replace("COPPER_", ""));
                item = CopperArmor.createCopperArmor(armorType);
            } catch (IllegalArgumentException e2) {
                sender.sendMessage(Component.text("Invalid item type! Available items: ", NamedTextColor.RED));
                sender.sendMessage(Component.text("Weapons: ", NamedTextColor.AQUA)
                    .append(Component.text("copper_sword, copper_axe, copper_pickaxe", NamedTextColor.WHITE)));
                sender.sendMessage(Component.text("Armor: ", NamedTextColor.AQUA)
                    .append(Component.text("copper_helmet, copper_chestplate, copper_leggings, copper_boots", NamedTextColor.WHITE)));
                return true;
            }
        }

        if (item == null) {
            sender.sendMessage(Component.text("Failed to create item! Check the configuration.", NamedTextColor.RED));
            return true;
        }

        player.getInventory().addItem(item);
        sender.sendMessage(Component.text("Given you a ", NamedTextColor.GREEN)
            .append(Component.text(itemName.replace("_", " "), NamedTextColor.GOLD))
            .append(Component.text("!", NamedTextColor.GREEN)));
        
        return true;
    }

    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("copperkingdom.reload")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }

        try {
            CopperKingdom.getInstance().reloadPluginConfig();
            sender.sendMessage(Component.text("Copper Kingdom configuration reloaded successfully!", NamedTextColor.GREEN));
        } catch (Exception e) {
            sender.sendMessage(Component.text("Failed to reload configuration: " + e.getMessage(), NamedTextColor.RED));
            CopperKingdom.getInstance().getLogger().severe("Failed to reload configuration: " + e.getMessage());
        }

        return true;
    }

    private boolean handleTestCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("copperkingdom.test")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /copperkingdom test <copperblocks|storm|lightning>", NamedTextColor.YELLOW));
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "copperblocks":
                // Place copper blocks around the player for testing grounding effects
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        if (x == 0 && z == 0) continue; // Skip player position
                        player.getLocation().add(x, -1, z).getBlock().setType(Material.COPPER_BLOCK);
                    }
                }
                sender.sendMessage(Component.text("Placed copper blocks around you for grounding testing!", NamedTextColor.GREEN));
                break;
            
            case "storm":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                sender.sendMessage(Component.text("Started a thunderstorm for storm bonus testing!", NamedTextColor.GREEN));
                break;
                
            case "lightning":
                player.getWorld().strikeLightning(player.getTargetBlock(null, 10).getLocation());
                sender.sendMessage(Component.text("Struck lightning for testing!", NamedTextColor.GREEN));
                break;
                
            default:
                sender.sendMessage(Component.text("Invalid test type! Available: copperblocks, storm, lightning", NamedTextColor.RED));
                break;
        }

        return true;
    }

    private boolean handleBlessedCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("copperkingdom.blessed")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /copperkingdom blessed <weapon_type>", NamedTextColor.YELLOW));
            sender.sendMessage(Component.text("Available: copper_sword, copper_axe, copper_pickaxe", NamedTextColor.AQUA));
            return true;
        }

        String weaponName = args[1].toLowerCase();
        CopperWeapons.WeaponType weaponType;
        
        try {
            weaponType = CopperWeapons.WeaponType.valueOf("COPPER_" + weaponName.toUpperCase().replace("COPPER_", ""));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid weapon type! Available: copper_sword, copper_axe, copper_pickaxe", NamedTextColor.RED));
            return true;
        }

        ItemStack weapon = CopperWeapons.createCopperWeapon(weaponType);
        if (weapon != null) {
            // Force blessed status
            ItemMeta meta = weapon.getItemMeta();
            if (meta != null) {
                List<Component> lore = meta.lore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                
                lore.add(Component.text("✨ Blessed by Ancient Copper Magic ✨", NamedTextColor.GOLD));
                lore.add(Component.text("Cleanses poison and wither from nearby allies", NamedTextColor.AQUA));
                
                meta.lore(lore);
                weapon.setItemMeta(meta);
            }
            
            player.getInventory().addItem(weapon);
            sender.sendMessage(Component.text("Given you a blessed ", NamedTextColor.GREEN)
                .append(Component.text(weaponName.replace("_", " "), NamedTextColor.GOLD))
                .append(Component.text("!", NamedTextColor.GREEN)));
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== Copper Kingdom Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/copperkingdom give <item>", NamedTextColor.YELLOW)
            .append(Component.text(" - Give yourself a copper item", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/copperkingdom blessed <weapon>", NamedTextColor.YELLOW)
            .append(Component.text(" - Give yourself a blessed copper weapon", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/copperkingdom test <type>", NamedTextColor.YELLOW)
            .append(Component.text(" - Test lore mechanics", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/copperkingdom reload", NamedTextColor.YELLOW)
            .append(Component.text(" - Reload plugin configuration", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/copperkingdom help", NamedTextColor.YELLOW)
            .append(Component.text(" - Show this help message", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("Available weapons: ", NamedTextColor.AQUA)
            .append(Component.text("copper_sword, copper_axe, copper_pickaxe", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("Available armor: ", NamedTextColor.AQUA)
            .append(Component.text("copper_helmet, copper_chestplate, copper_leggings, copper_boots", NamedTextColor.WHITE)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument - subcommands
            List<String> subcommands = Arrays.asList("give", "blessed", "test", "reload", "help");
            for (String subcommand : subcommands) {
                if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subcommand);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                // Items for give command
                List<String> items = Arrays.asList("copper_sword", "copper_axe", "copper_pickaxe", 
                                                 "copper_helmet", "copper_chestplate", "copper_leggings", "copper_boots");
                for (String item : items) {
                    if (item.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(item);
                    }
                }
            } else if (args[0].equalsIgnoreCase("blessed")) {
                // Weapons for blessed command
                List<String> weapons = Arrays.asList("copper_sword", "copper_axe", "copper_pickaxe");
                for (String weapon : weapons) {
                    if (weapon.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(weapon);
                    }
                }
            } else if (args[0].equalsIgnoreCase("test")) {
                // Test types
                List<String> testTypes = Arrays.asList("copperblocks", "storm", "lightning");
                for (String testType : testTypes) {
                    if (testType.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(testType);
                    }
                }
            }
        }

        return completions;
    }
}
