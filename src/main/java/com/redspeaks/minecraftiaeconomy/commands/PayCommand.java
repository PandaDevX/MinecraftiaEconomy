package com.redspeaks.minecraftiaeconomy.commands;

import com.redspeaks.minecraftiaeconomy.api.AbstractCommand;
import com.redspeaks.minecraftiaeconomy.api.ChatUtil;
import com.redspeaks.minecraftiaeconomy.api.CommandInfo;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandInfo(name = "pay", permission = "economy.command.pay", requiresPlayer = true)
public class PayCommand extends AbstractCommand {

    @Override
    public void execute(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.colorize("&7Correct usage: &c/pay <player> <amount>"));
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if(!ChatUtil.isInt(args[1])) {
            player.sendMessage(ChatUtil.colorize("&7Please enter numbers only for amount"));
            return;
        }
        if(!target.hasPlayedBefore()) {
            player.sendMessage(ChatUtil.colorize("&7Target player never played before"));
            return;
        }
        double amount = Double.parseDouble(args[1]);
        if(MinecraftiaEconomyManager.getEconomy().transferBalance(player, target, amount)) {
            sendMessage(player, "&7Transaction: &aSuccessful");
            sendMessage(player, "&7From: &a" + player.getDisplayName());
            sendMessage(player, "&7To: &a" + target.getName());
            sendMessage(player, "&7Amount: &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix());
            if(target.isOnline()) {
                sendMessage(target.getPlayer(), "&7Received: &f" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix());
                sendMessage(target.getPlayer(), "&7Sender: &f" + player.getDisplayName());
            }
        }
    }
}
