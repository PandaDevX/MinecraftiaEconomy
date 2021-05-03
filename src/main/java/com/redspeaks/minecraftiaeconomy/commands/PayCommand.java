package com.redspeaks.minecraftiaeconomy.commands;

import com.redspeaks.minecraftiaeconomy.api.AbstractCommand;
import com.redspeaks.minecraftiaeconomy.api.ChatUtil;
import com.redspeaks.minecraftiaeconomy.api.CommandInfo;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import com.redspeaks.minecraftiaeconomy.data.Actions;
import com.redspeaks.minecraftiaeconomy.data.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandInfo(name = "pay", permission = "economy.command.pay", requiresPlayer = true)
public class PayCommand extends AbstractCommand {

    @Override
    public void execute(Player player, String[] args) {
        if(args.length < 3) {
            player.sendMessage(ChatUtil.colorize("&7Correct usage: &c/pay <player/bank> <name> <amount>"));
            return;
        }
        if(args[0].equalsIgnoreCase("player")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!ChatUtil.isInt(args[2])) {
                player.sendMessage(ChatUtil.colorize("&7Please enter numbers only for amount"));
                return;
            }
            if (!target.hasPlayedBefore()) {
                player.sendMessage(ChatUtil.colorize("&7Target player never played before"));
                return;
            }
            double amount = Double.parseDouble(args[2]);
            if (MinecraftiaEconomyManager.getEconomy().transferBalance(player, target, amount)) {
                sendMessage(player, "&7Transaction: &aSuccessful");
                sendMessage(player, "&7From: &a" + player.getDisplayName());
                sendMessage(player, "&7To: &a" + target.getName());
                sendMessage(player, "&7Amount: &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix());

                Logger.log(Actions.PlayerAction.SEND, player).info("&bRecipient: &6[&a" + target.getName() +"&6]");
                Logger.log(Actions.PlayerAction.RECEIVED, target).info("&bSender: &6[&a" + target.getName() + "&6]");
                if (target.isOnline()) {
                    sendMessage(target.getPlayer(), "&7Received: &f" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix());
                    sendMessage(target.getPlayer(), "&7Sender: &f" + player.getDisplayName());
                }
            } else {
                sendMessage(player, "&7Insufficient funds");
            }
        } else {
            String bank = args[1];
            if (!ChatUtil.isInt(args[2])) {
                player.sendMessage(ChatUtil.colorize("&7Please enter numbers only for amount"));
                return;
            }
            if(!MinecraftiaEconomyManager.bankExists(bank)) {
                player.sendMessage(ChatUtil.colorize("&7No matching &c" + bank + " &7from bank database"));
                return;
            }
            double amount = Double.parseDouble(args[2]);
            if (MinecraftiaEconomyManager.getEconomy().transferBalance(player, bank, amount)) {
                sendMessage(player, "&7Transaction: &aSuccessful");
                sendMessage(player, "&7From: &a" + player.getDisplayName());
                sendMessage(player, "&7To: &a" + bank);
                sendMessage(player, "&7Amount: &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix());
                Logger.log(Actions.PlayerAction.SEND, player).info("&bRecipient bank: &6[&a" + bank +"&6]");
                Logger.log(Actions.BankAction.RECEIVED_FROM_PLAYER, bank).info("&bSender: &6[&a" + player.getName() + "&6]");
            } else {
                sendMessage(player, "&7Insufficient funds");
            }
        }
    }
}
