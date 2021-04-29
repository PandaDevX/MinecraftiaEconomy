package com.redspeaks.minecraftiaeconomy.commands;

import com.redspeaks.minecraftiaeconomy.api.AbstractCommand;
import com.redspeaks.minecraftiaeconomy.api.ChatUtil;
import com.redspeaks.minecraftiaeconomy.api.CommandInfo;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.Set;

@CommandInfo(name = "balance", permission = "economy.command.balance", requiresPlayer = true)
public class BalanceCommand extends AbstractCommand {

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0) {
            player.sendMessage(ChatUtil.colorize("&aBalance: &c" + MinecraftiaEconomyManager.getEconomy().getBalanceFormat(player)));
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
        player.sendMessage(ChatUtil.colorize("&aBalance: &c" + MinecraftiaEconomyManager.getEconomy().getBalanceFormat(target)));
        Set<String> getBanks = MinecraftiaEconomyManager.getBanks(player);
        if(!getBanks.isEmpty()) {
            sendMessage(player, "&7Available banks:");
            for(String bank : getBanks) {
                sendMessage(player, "  &f- &b" + bank + ": &f" + ChatUtil.commas(MinecraftiaEconomyManager.getBank().getBalance(bank).get()) + MinecraftiaEconomyManager.getEconomy().getSuffix());
            }
        }
    }
}
