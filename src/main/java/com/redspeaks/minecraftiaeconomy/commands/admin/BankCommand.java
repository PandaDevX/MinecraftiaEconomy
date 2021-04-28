package com.redspeaks.minecraftiaeconomy.commands.admin;

import com.redspeaks.minecraftiaeconomy.api.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.Random;

@CommandInfo(name = "bank", requiresPlayer = false)
public class BankCommand extends AbstractCommand {

    private Bank bank = MinecraftiaEconomyManager.getBank();

    @Override
    public void execute(CommandSender sender, String[] args) {
        String node = "economy.command.bank";
        if(!hasPermissionFrom(sender, node, "info", "create", "delete", "assign")) {
            sendNoPermission(sender);
            return;
        }
        if(args.length < 2) {
            sendCorrectArguments(sender, "bank info <owner / name>", "bank create <owner> <name>", "bank delete <name>", "bank assign <player> <name>");
            return;
        }

        String subCommand = args[0];

        if(subCommand.equalsIgnoreCase("create")) {
            if(!sender.hasPermission(node + subCommand.toLowerCase())) {
                sendNoPermission(sender);
                return;
            }
            if(args.length != 3) {
                sendCorrectUsage(sender, "bank create <owner> <name>");
                return;
            }
            OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
            String name = args[2];

            if(!owner.hasPlayedBefore()) {
                sendMessage(sender, "&7Player &c" + args[1] + " &7never played the server before");
                return;
            }

            if(bank.create(name, owner)) {
                sendMessage(sender, "&7Creation: &aSuccessful");
                sendMessage(sender, "&7Bank: &6" + name);
                sendMessage(sender, "&7Assigned to: &6" + owner.getName());

                if(owner.isOnline()) {
                    sendMessage(owner.getPlayer(), "&7Bank: &6" + name + " &7has been assigned to you");
                    Random random = new Random();
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.values()[random.nextInt(Sound.values().length)], 1.0f, 1.0f);
                    random = null;
                }
            } else {
                sendMessage(sender, "&7Creation: &cUnsuccessful");
                sendMessage(sender, "&7Possible errors:");
                sendMessage(sender, "  &f- &cBank already exists");
                sendMessage(sender, "  &f- &cTarget player have two bank accounts");
            }
            return;
        }

        if(subCommand.equalsIgnoreCase("assign")) {

            if(!sender.hasPermission(node + subCommand.toLowerCase())) {
                sendNoPermission(sender);
                return;
            }

            if(args.length != 3) {
                sendCorrectUsage(sender, "bank assign <owner> <name>");
                return;
            }
            OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
            String name = args[2];

            if(!owner.hasPlayedBefore()) {
                sendMessage(sender, "&7Player &c" + args[1] + " &7never played the server before");
                return;
            }

            Optional<OfflinePlayer> previousOwner = MinecraftiaEconomyManager.getBankOwner(name);
            if(bank.transferOwnerShip(name, owner)) {
                sendMessage(sender, "&7Assign: &aSuccessful");
                sendMessage(sender, "&7Bank: &6" + name);
                sendMessage(sender, "&7Assigned to: &6" + owner.getName());
                sendMessage(sender, "&7Previous owner: &6" + previousOwner.get().getName());

                if(owner.isOnline()) {
                    sendMessage(owner.getPlayer(), "&7Bank: &6" + name + " &7has been assigned to you");
                    Random random = new Random();
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.values()[random.nextInt(Sound.values().length)], 1.0f, 1.0f);
                    random = null;
                }
            } else {
                sendMessage(sender, "&7Assign: &cUnsuccessful");
                sendMessage(sender, "&7Possible errors:");
                sendMessage(sender, "  &f- &cBank does not exist");
                sendMessage(sender, "  &f- &cTarget player have two bank accounts");
            }
            return;
        }

        if(subCommand.equalsIgnoreCase("delete")) {
            if(!sender.hasPermission(node + subCommand.toLowerCase())) {
                sendNoPermission(sender);
                return;
            }
            String name = args[1];
            Optional<OfflinePlayer> owner = MinecraftiaEconomyManager.getBankOwner(name);
            if(bank.delete(name)) {
                if(owner.isPresent() && owner.get().isOnline()) {
                    sendMessage(owner.get().getPlayer(), "&7Bank: &6" + name + " &7has been unassigned to you");
                    Random random = new Random();
                    owner.get().getPlayer().playSound(owner.get().getPlayer().getLocation(), Sound.values()[random.nextInt(Sound.values().length)], 1.0f, 1.0f);
                    random = null;
                }
                sendMessage(sender, "&7Delete: &aSuccessful");
                sendMessage(sender, "&7Bank: &6" + name);
                sendMessage(sender, "&7Assigned to: &6" + owner.get().getName());
            } else {
                sendMessage(sender, "&7Assign: &cUnsuccessful");
                sendMessage(sender, "&7Possible errors:");
                sendMessage(sender, "  &f- &cBank does not exist");
            }
            return;
        }
        sendCorrectArguments(sender, "bank info <owner / name>", "bank create <owner> <name>", "bank delete <name>", "bank assign <player> <name>");
    }
}
