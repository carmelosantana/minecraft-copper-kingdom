package org.xpfarm.copperkingdom.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xpfarm.copperkingdom.CopperKingdom;
import org.xpfarm.copperkingdom.items.CopperWeapons;

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
            sender.sendMessage(Component.text("Usage: /copperkingdom give <weapon>", NamedTextColor.YELLOW));
            sender.sendMessage(Component.text("Available weapons: ", NamedTextColor.AQUA)
                .append(Component.text("copper_sword, copper_axe, copper_pickaxe", NamedTextColor.WHITE)));
            return true;
        }

        CopperWeapons.WeaponType weaponType;
        try {
            weaponType = CopperWeapons.WeaponType.valueOf("COPPER_" + args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid weapon type! Available weapons: ", NamedTextColor.RED)
                .append(Component.text("copper_sword, copper_axe, copper_pickaxe", NamedTextColor.WHITE)));
            return true;
        }

        ItemStack weapon = CopperWeapons.createCopperWeapon(weaponType);
        if (weapon == null) {
            sender.sendMessage(Component.text("Failed to create weapon! Check the configuration.", NamedTextColor.RED));
            return true;
        }

        player.getInventory().addItem(weapon);
        sender.sendMessage(Component.text("Given you a ", NamedTextColor.GREEN)
            .append(Component.text(args[1].replace("_", " "), NamedTextColor.GOLD))
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

    private void showHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== Copper Kingdom Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/copperkingdom give <weapon>", NamedTextColor.YELLOW)
            .append(Component.text(" - Give yourself a copper weapon", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/copperkingdom reload", NamedTextColor.YELLOW)
            .append(Component.text(" - Reload plugin configuration", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/copperkingdom help", NamedTextColor.YELLOW)
            .append(Component.text(" - Show this help message", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("Available weapons: ", NamedTextColor.AQUA)
            .append(Component.text("copper_sword, copper_axe, copper_pickaxe", NamedTextColor.WHITE)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument - subcommands
            List<String> subcommands = Arrays.asList("give", "reload", "help");
            for (String subcommand : subcommands) {
                if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subcommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            // Second argument for give command - weapon types
            List<String> weapons = Arrays.asList("copper_sword", "copper_axe", "copper_pickaxe");
            for (String weapon : weapons) {
                if (weapon.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(weapon);
                }
            }
        }

        return completions;
    }
}
