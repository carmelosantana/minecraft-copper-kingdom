package org.xpfarm.copperkingdom.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
import org.xpfarm.copperkingdom.util.PlayerLookup;

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

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /copperkingdom give <item> [player]", NamedTextColor.YELLOW));
            sender.sendMessage(Component.text("Available weapons: ", NamedTextColor.AQUA)
                .append(Component.text("copper_sword, copper_axe, copper_pickaxe", NamedTextColor.WHITE)));
            sender.sendMessage(Component.text("Available armor: ", NamedTextColor.AQUA)
                .append(Component.text("copper_helmet, copper_chestplate, copper_leggings, copper_boots", NamedTextColor.WHITE)));
            return true;
        }

        // Resolve who receives the item: the sender (self-give) or a named target. When
        // an explicit target is present the sender may be the console; without one, the
        // console gets a friendly instruction rather than an exception.
        Player recipient = resolveRecipient(sender, args, 2, "Usage: /copperkingdom give <item> <player>");
        if (recipient == null) {
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

        recipient.getInventory().addItem(item);
        announceGift(sender, recipient, itemName.replace("_", " "));

        return true;
    }

    /**
     * The three ways a give-style subcommand can pick a recipient, decided purely from
     * whether the sender is a player and whether an explicit target argument was supplied.
     */
    enum RecipientMode {
        /** No target argument and a player sender: give to the sender. */
        SELF,
        /** A target argument is present: resolve and give to that named player. */
        RESOLVE_TARGET,
        /** No target argument and a console sender: nobody to give to. */
        CONSOLE_NEEDS_TARGET
    }

    /**
     * Decides how a give-style subcommand should pick its recipient. Pure and Bukkit-free
     * so the console/self/target branching can be unit tested without a running server.
     *
     * @param senderIsPlayer whether the command sender is an in-game player
     * @param hasTargetArg   whether an explicit target-player argument was supplied
     */
    static RecipientMode recipientMode(boolean senderIsPlayer, boolean hasTargetArg) {
        if (hasTargetArg) {
            return RecipientMode.RESOLVE_TARGET;
        }
        return senderIsPlayer ? RecipientMode.SELF : RecipientMode.CONSOLE_NEEDS_TARGET;
    }

    /**
     * Resolves the recipient for a give-style subcommand, messaging the sender and
     * returning {@code null} when no recipient can be determined (unknown target, or a
     * console sender that supplied no target).
     *
     * @param sender         the command sender
     * @param args           the full argument array
     * @param targetArgIndex the index at which an optional target name would appear
     * @param consoleUsage   the usage line shown to a console sender with no target
     * @return the resolved recipient, or {@code null} (a message has already been sent)
     */
    private Player resolveRecipient(CommandSender sender, String[] args, int targetArgIndex, String consoleUsage) {
        boolean hasTargetArg = args.length > targetArgIndex;
        switch (recipientMode(sender instanceof Player, hasTargetArg)) {
            case RESOLVE_TARGET -> {
                Player target = PlayerLookup.resolveAllowingPartial(args[targetArgIndex]).orElse(null);
                if (target == null) {
                    sender.sendMessage(Component.text(
                        PlayerLookup.noSuchPlayerMessage(args[targetArgIndex], PlayerLookup.onlineNames()),
                        NamedTextColor.RED));
                }
                return target;
            }
            case SELF -> {
                return (Player) sender;
            }
            default -> {
                sender.sendMessage(Component.text("Console must specify a target player!", NamedTextColor.RED));
                sender.sendMessage(Component.text(consoleUsage, NamedTextColor.YELLOW));
                return null;
            }
        }
    }

    /**
     * Tells the sender (and, for a cross-player gift, the recipient) that an item changed
     * hands. {@code prettyName} is the human-readable item name with underscores removed.
     */
    private void announceGift(CommandSender sender, Player recipient, String prettyName) {
        if (recipient.equals(sender)) {
            sender.sendMessage(Component.text("Given you a ", NamedTextColor.GREEN)
                .append(Component.text(prettyName, NamedTextColor.GOLD))
                .append(Component.text("!", NamedTextColor.GREEN)));
            return;
        }
        sender.sendMessage(Component.text("Given a ", NamedTextColor.GREEN)
            .append(Component.text(prettyName, NamedTextColor.GOLD))
            .append(Component.text(" to " + recipient.getName() + "!", NamedTextColor.GREEN)));
        recipient.sendMessage(Component.text("You have received a ", NamedTextColor.GREEN)
            .append(Component.text(prettyName, NamedTextColor.GOLD))
            .append(Component.text("!", NamedTextColor.GREEN)));
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

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /copperkingdom blessed <weapon_type> [player]", NamedTextColor.YELLOW));
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

        Player recipient = resolveRecipient(sender, args, 2, "Usage: /copperkingdom blessed <weapon_type> <player>");
        if (recipient == null) {
            return true;
        }

        ItemStack weapon = CopperWeapons.createCopperWeapon(weaponType);
        if (weapon == null) {
            sender.sendMessage(Component.text("Failed to create item! Check the configuration.", NamedTextColor.RED));
            return true;
        }

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

        recipient.getInventory().addItem(weapon);
        announceGift(sender, recipient, "blessed " + weaponName.replace("_", " "));

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== Copper Kingdom Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/copperkingdom give <item> [player]", NamedTextColor.YELLOW)
            .append(Component.text(" - Give a copper item to yourself or another player", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/copperkingdom blessed <weapon> [player]", NamedTextColor.YELLOW)
            .append(Component.text(" - Give a blessed copper weapon to yourself or another player", NamedTextColor.GRAY)));
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
        } else if (args.length == 3) {
            // Optional target player for give/blessed
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("blessed")) {
                String partial = args[2].toLowerCase();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().toLowerCase().startsWith(partial)) {
                        completions.add(online.getName());
                    }
                }
            }
        }

        return completions;
    }
}
