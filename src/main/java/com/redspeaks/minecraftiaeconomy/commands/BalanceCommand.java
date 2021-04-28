package com.redspeaks.minecraftiaeconomy.commands;

import com.redspeaks.minecraftiaeconomy.api.AbstractCommand;
import com.redspeaks.minecraftiaeconomy.api.ChatUtil;
import com.redspeaks.minecraftiaeconomy.api.CommandInfo;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandInfo(name = "balance", permission = "economy.command.balance", requiresPlayer = true)
public class BalanceCommand extends AbstractCommand {

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0) {
            player.sendMessage(ChatUtil.colorize("&aBalance: &c" + MinecraftiaEconomyManager.getEconomy().getBalanceFormat(player, true)));
            return;
        }
        if(!player.hasPermission("economy.command.balanceuser")) {
            player.sendMessage(ChatUtil.colorize("&7Missing required &cpermission"));
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if(!target.hasPlayedBefore()) {
            player.sendMessage(ChatUtil.colorize("&7Player &b" + args[0] + " &7haven't played before"));
            return;
        }
        player.sendMessage(ChatUtil.colorize("&7Showing balance of &b" + args[0] + ":"));
        player.sendMessage(ChatUtil.colorize("&aBalance: &c" + MinecraftiaEconomyManager.getEconomy().getBalanceFormat(target, true)));
        Optional<String> bank = MinecraftiaEconomyManager.getEconomy().getBank(player);
        Optional<String> extraBank = MinecraftiaEconomyManager.getExtraBank(player);
        if(bank.isPresent() || extraBank.isPresent()) {
            sendMessage(player, "&7Available banks:");
            bank.ifPresent(s -> sendMessage(player, "   &f- &b" + s + ": &f" + ChatUtil.commas(MinecraftiaEconomyManager.getBank().getBalance(s).orElse(0D)) + MinecraftiaEconomyManager.getEconomy().getSuffix()));
            extraBank.ifPresent(s-> sendMessage(player, "   &f- &b" + s + ": &f" + ChatUtil.commas(MinecraftiaEconomyManager.getBank().getBalance(s).orElse(0D)) + MinecraftiaEconomyManager.getEconomy().getSuffix()));
        }
    }
}
