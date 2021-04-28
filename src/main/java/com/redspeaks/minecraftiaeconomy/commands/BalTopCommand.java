package com.redspeaks.minecraftiaeconomy.commands;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import com.redspeaks.minecraftiaeconomy.api.AbstractCommand;
import com.redspeaks.minecraftiaeconomy.api.ChatUtil;
import com.redspeaks.minecraftiaeconomy.api.CommandInfo;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

@CommandInfo(name = "balance", permission = "economy.command.balancetop", requiresPlayer = true)
public class BalTopCommand extends AbstractCommand {

    @Override
    public void execute(Player player, String[] args) {
        HashMap<String, Double> sortedMap = ChatUtil.reverseOrder(MinecraftiaEconomy.map);
        int count = 1;
        player.sendMessage(ChatUtil.colorize("&7---- &6Top Balance Holders &7----"));
        for(String uuid : sortedMap.keySet()) {
            if(count == 11) {
                break;
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            player.sendMessage(ChatUtil.colorize("&7&l" + count + ". " + offlinePlayer.getName() + " &6- " + sortedMap.get(offlinePlayer.getUniqueId().toString()) + MinecraftiaEconomyManager.getEconomy().getSuffix()));
            count++;
        }
    }
}
