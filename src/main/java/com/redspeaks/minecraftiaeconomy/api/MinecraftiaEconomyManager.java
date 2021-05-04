package com.redspeaks.minecraftiaeconomy.api;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("all")
public class MinecraftiaEconomyManager {


    public static Bank getBank() {
        return MinecraftiaEconomy.getInstance().getBank();
    }

    public static Economy getEconomy() {
        return MinecraftiaEconomy.getInstance().getEconomy();
    }

    public static Optional<String> getBank(OfflinePlayer player, String name) {
        Set<String> set = MinecraftiaEconomy.getInstance().getBankDatabase().getBanks(player);
        if(set.contains(name)) {
            return Optional.ofNullable(name);
        }
        return Optional.empty();
    }

    public static Set<String> getBanks(OfflinePlayer player) {
        return MinecraftiaEconomy.getInstance().getBankDatabase().getBanks(player);
    }

    public static boolean bankExists(String name) {
        return MinecraftiaEconomy.getInstance().getBankDatabase().exists(name);
    }

    public static OfflinePlayer getBankOwner(String name) {
        String owner = MinecraftiaEconomy.getInstance().getBankDatabase().getOwner(name);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(MinecraftiaEconomy.getInstance().getBankDatabase().getOwner(name)));
        return offlinePlayer;
    }

    public static boolean transferOwnership(String name, OfflinePlayer newOwner) {
        if(!bankExists(name)) {
            return false;
        }
        if(newOwner == null || !newOwner.hasPlayedBefore()) {
            return false;
        }
        if(MinecraftiaEconomy.getInstance().getBankDatabase().getBanks(newOwner).size() > 1) {
            return false;
        }
        MinecraftiaEconomy.getInstance().getBankDatabase().changeOwner(name, newOwner);
        return true;
    }

}
