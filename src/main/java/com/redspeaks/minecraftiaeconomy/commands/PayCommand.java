package com.redspeaks.minecraftiaeconomy.commands;

import com.redspeaks.minecraftiaeconomy.api.AbstractCommand;
import com.redspeaks.minecraftiaeconomy.api.ChatUtil;
import com.redspeaks.minecraftiaeconomy.api.CommandInfo;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandInfo(name = "pay", permission = "economy.command.pay", requiresPlayer = true)
public class PayCommand extends AbstractCommand {

    @Override
    public void execute(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.colorize("&7Correct usage: &c/pay <player / bank name> <amount> <your bank Optional>"));
            return;
        }
        Optional<OfflinePlayer> target = Optional.ofNullable(Bukkit.getOfflinePlayer(args[0]));
        Optional<String> bank = Optional.empty();
        if(target.isPresent()) {
            if(!target.get().hasPlayedBefore()) {
                bank = Optional.ofNullable(args[0]);
            }
        }
        if(!ChatUtil.isInt(args[1])) {
            player.sendMessage(ChatUtil.colorize("&7Please enter numbers only for amount"));
            return;
        }
        double amount = Double.parseDouble(args[1]);
        if(!bank.isPresent()) {
            if(args.length != 3) {
                if (MinecraftiaEconomyManager.getEconomy().transferBalance(player, target.get(), amount)) {
                    player.sendMessage(ChatUtil.colorize("&7Successfully sent &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix() + " &7to player: &f" + target.get().getName()));
                    if (target.get().isOnline()) {
                        target.get().getPlayer().sendMessage(ChatUtil.colorize("&7Player: &f" + player.getDisplayName() + " &7sent you &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix()));
                    }
                } else {
                    player.sendMessage(ChatUtil.colorize("&7Insufficient &cbalance"));
                }
            }
            String bankName = args[2];
            Optional<String> playerBank = MinecraftiaEconomyManager.getBank(player);
            Optional<String> playerExtraBank = MinecraftiaEconomyManager.getExtraBank(player);
            if(!playerBank.orElse("").equalsIgnoreCase(bankName) && !playerExtraBank.orElse("").equalsIgnoreCase(bankName)) {
                player.sendMessage(ChatUtil.colorize("&7No matching &c" + args[2] + " &7from your bank accounts"));
                return;
            }
            if (MinecraftiaEconomyManager.getBank().transfer(bankName, target.get(), amount)) {
                player.sendMessage(ChatUtil.colorize("&7Successfully sent &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix() + " &7to player: &f" + target.get().getName()));
                if (target.get().isOnline()) {
                    target.get().getPlayer().sendMessage(ChatUtil.colorize("&7Player: &f" + player.getDisplayName() + " &7sent you &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix()));
                }
            } else {
                player.sendMessage(ChatUtil.colorize("&7Insufficient &cbalance"));
            }
        } else {
            if(args.length != 3) {
                if (MinecraftiaEconomyManager.getEconomy().transferBalance(player, bank.get(), amount)) {
                    player.sendMessage(ChatUtil.colorize("&7Successfully sent &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix() + " &7to bank: &f" + bank.get()));
                    if (target.get().isOnline()) {
                        target.get().getPlayer().sendMessage(ChatUtil.colorize("&7Player: &f" + player.getDisplayName() + " &7sent you &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix()));
                    }
                } else {
                    player.sendMessage(ChatUtil.colorize("&7Insufficient &cbalance"));
                }
                return;
            }
            String bankName = args[2];
            Optional<String> playerBank = MinecraftiaEconomyManager.getBank(player);
            Optional<String> playerExtraBank = MinecraftiaEconomyManager.getExtraBank(player);
            if(!playerBank.orElse("").equalsIgnoreCase(bankName) && !playerExtraBank.orElse("").equalsIgnoreCase(bankName)) {
                player.sendMessage(ChatUtil.colorize("&7No matching &c" + args[2] + " &7from your bank accounts"));
                return;
            }
            if (MinecraftiaEconomyManager.getBank().transfer(bankName, bank.get(), amount)) {
                player.sendMessage(ChatUtil.colorize("&7Successfully sent &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix() + " &7to player: &f" + target.get().getName()));
                if (target.get().isOnline()) {
                    target.get().getPlayer().sendMessage(ChatUtil.colorize("&7Player: &f" + player.getDisplayName() + " &7sent you &a" + ChatUtil.commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix()));
                }
            } else {
                player.sendMessage(ChatUtil.colorize("&7Insufficient &cbalance"));
            }
        }
        return;
    }
}
