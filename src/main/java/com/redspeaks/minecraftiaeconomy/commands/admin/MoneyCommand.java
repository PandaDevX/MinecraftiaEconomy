package com.redspeaks.minecraftiaeconomy.commands.admin;

import com.redspeaks.minecraftiaeconomy.api.*;
import com.redspeaks.minecraftiaeconomy.data.Actions;
import com.redspeaks.minecraftiaeconomy.data.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.Random;

@CommandInfo(name = "money", requiresPlayer = false)
public class MoneyCommand extends AbstractCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        String node = "economy.command.";
        if(args.length < 3) {
            if(!sender.hasPermission(node + "give") || !sender.hasPermission(node + "set") || !sender.hasPermission(node + "take")) {
                sendNoPermission(sender);
                return;
            }
            sendMessage(sender, "&7Correct usage: &b/money give <player> <amount>", "economy.command.give");
            sendMessage(sender, "&7Correct usage: &b/money take <player> <amount>", "economy.command.take");
            sendMessage(sender, "&7Correct usage: &b/money set <player> <amount>", "economy.command.set");
            return;
        }
        String argument = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if(!target.hasPlayedBefore()) {
            sendMessage(sender, "&7Player &c" + args[1] + " never played the server before");
            return;
        }
        if(!ChatUtil.isInt(args[2])) {
            sendMessage(sender, "&7Incorrect argument: &camount must be a number");
            return;
        }
        double amount = Double.parseDouble(args[2]);
        Economy economy = MinecraftiaEconomyManager.getEconomy();
        switch (argument.toLowerCase()) {
            case "give":
                if(!sender.hasPermission(node + argument.toLowerCase())) {
                    sendNoPermission(sender);
                    break;
                }
                economy.deposit(target, amount);
                sendMessage(sender, "&7Successfully given money to: &f" + target.getName());
                sendMessage(sender, "&7Amount: &f" + ChatUtil.commas(amount) + economy.getSuffix());

                Logger.log(Actions.PlayerAction.SET_BALANCE, target.getPlayer()).info("&7Admin: &6[&a" + sender.getName() + "&6], &7Amount: &a+&f" + amount);

                if(target.isOnline()) {
                    sendMessage(target.getPlayer(), "&f+&a" + ChatUtil.commas(amount) + economy.getSuffix());
                    Random random = new Random();
                    target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.values()[random.nextInt(Sound.values().length)], 1.0f, 1.0f);
                    random = null;
                }
                break;
            case "take":
                if(!sender.hasPermission(node + argument.toLowerCase())) {
                    sendNoPermission(sender);
                    break;
                }
                economy.setBalance(target, economy.getBalance(target).orElse(0D) - amount);
                sendMessage(sender, "&7Successfully taken money from: &f" + target.getName());
                sendMessage(sender, "&7Amount: &f" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix());
                Logger.log(Actions.PlayerAction.SET_BALANCE, target.getPlayer()).info("&7Admin: &6[&a" + sender.getName() + "&6], &7Amount: &c-&f" + amount);
                if(target.isOnline()) {
                    sendMessage(target.getPlayer(), "&f-&c" + ChatUtil.commas(amount) + economy.getSuffix());
                    Random random = new Random();
                    target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.values()[random.nextInt(Sound.values().length)], 1.0f, 1.0f);
                    random = null;
                }
                break;
            case "set":
                if(!sender.hasPermission(node + argument.toLowerCase())) {
                    sendNoPermission(sender);
                    break;
                }
                economy.setBalance(target, amount);
                sendMessage(sender, "&7Successfully set player balance of: &f" + target.getName());
                sendMessage(sender, "&7Amount: &f" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix());
                Logger.log(Actions.PlayerAction.SET_BALANCE, target.getPlayer()).info("&7Admin: &6[&a" + sender.getName() + "&6], &7New Balance: &f" + amount);
                if(target.isOnline()) {
                    sendMessage(target.getPlayer(), "&fNew Balance: &6" + ChatUtil.commas(amount) + economy.getSuffix());
                    Random random = new Random();
                    target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.values()[random.nextInt(Sound.values().length)], 1.0f, 1.0f);
                    random = null;
                }
                break;
            default:
                if(!sender.hasPermission(node + argument.toLowerCase())) {
                    sendNoPermission(sender);
                    break;
                }
                sendUnknownMessage(sender);
                break;
        }
    }
}
