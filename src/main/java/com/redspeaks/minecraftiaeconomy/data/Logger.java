package com.redspeaks.minecraftiaeconomy.data;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import com.redspeaks.minecraftiaeconomy.api.ChatUtil;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Logger {

    private static Logs logs = MinecraftiaEconomy.getInstance().getLogFile();

    public static MinecraftiaEconomyLogger log(Actions.BankAction bankAction, String name) {
        return new MinecraftiaEconomyLogger(logs, bankAction, name);
    }

    public static MinecraftiaEconomyLogger log(Actions.PlayerAction playerAction, OfflinePlayer player) {
        return new MinecraftiaEconomyLogger(logs, playerAction, player);
    }

    public static void showLogs(CommandSender player, String path) {
        if(!MinecraftiaEconomyManager.bankExists(path)) {
            logs.set(path, null);
            player.sendMessage(ChatUtil.colorize("&7No info for &c" + path + "&7 found"));
            return;
        }
        if(!logExist(path)) {
            player.sendMessage(ChatUtil.colorize("&7No info for &c" + path + "&7 found"));
            return;
        }
        player.sendMessage(ChatUtil.colorize("&7Bank: &6" + path));
        player.sendMessage(ChatUtil.colorize("&7Current Balance: &f" + ChatUtil.getEconomyFormat(MinecraftiaEconomyManager.getBank().getBalance(path).orElse(0D))));
        player.sendMessage(ChatUtil.colorize("&7Showing logs of bank: &6" + path));
        player.sendMessage(ChatUtil.colorize("&7Assigned to: &6" + MinecraftiaEconomyManager.getBankOwner(path).getName()));
        player.sendMessage(ChatUtil.colorize("&7Last &65 &7transactions:"));
        for(String content : logs.get().getStringList(path)) {
            player.sendMessage(ChatUtil.colorize("&6 * " + content));
        }
    }

    public static void showLogs(CommandSender player, OfflinePlayer target) {
        if(!logExist(target.getUniqueId().toString())) {
            player.sendMessage(ChatUtil.colorize("&7No info for &c" + target.getName() + "&7 found"));
            return;
        }
        player.sendMessage(ChatUtil.colorize("&7Player: &6" + target.getName()));
        player.sendMessage(ChatUtil.colorize("&7Current Balance: &f" + ChatUtil.getEconomyFormat(MinecraftiaEconomyManager.getEconomy().getBalance(target).orElse(0D))));
        player.sendMessage(ChatUtil.colorize("&7Showing logs of player: &6" + target.getName()));
        if(!MinecraftiaEconomyManager.getBanks(target).isEmpty()) {
            player.sendMessage(ChatUtil.colorize("&7Available banks: "));
            MinecraftiaEconomyManager.getBanks(target).stream().forEach(bank -> player.sendMessage(ChatUtil.colorize(" &6* &f" + bank)));
        }
        player.sendMessage(ChatUtil.colorize("&7Last &65 &7transactions:"));
        for(String content : logs.get().getStringList(target.getUniqueId().toString())) {
            player.sendMessage(ChatUtil.colorize("&6 * " + content));
        }
    }

    public static boolean logExist(String name) {
        return logs.get().get(name) != null;
    }

}
