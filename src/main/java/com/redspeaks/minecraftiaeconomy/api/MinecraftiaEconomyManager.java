package com.redspeaks.minecraftiaeconomy.api;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

@SuppressWarnings("all")
public class MinecraftiaEconomyManager {


    public static Bank getBank() {
        return MinecraftiaEconomy.getInstance().getBank();
    }

    public static Economy getEconomy() {
        return MinecraftiaEconomy.getInstance().getEconomy();
    }

    public static Optional<String> getBank(OfflinePlayer player) {
        return MinecraftiaEconomy.getInstance().getEconomyDatabase().getBank(player);
    }

    public static Optional<String> getExtraBank(OfflinePlayer player) {
        return MinecraftiaEconomy.getInstance().getEconomyDatabase().getExtraBank(player);
    }

    public static boolean bankExists(String name) {
        return MinecraftiaEconomy.bankMap.containsKey(name);
    }

    public static Optional<OfflinePlayer> getBankOwner(String name) {
        return Optional.ofNullable(Bukkit.getOfflinePlayer(MinecraftiaEconomy.bankOwnership.get(name)));
    }

    public static boolean transferOwnership(String name, OfflinePlayer newOwner) {
        if(!bankExists(name)) {
            return false;
        }
        if(!getBankOwner(name).isPresent()) {
            return false;
        }
        if(newOwner == null || !newOwner.hasPlayedBefore()) {
            return false;
        }
        if(getBank(newOwner).isPresent() && getExtraBank(newOwner).isPresent()) {
            return false;
        }
        MinecraftiaEconomy.bankOwnership.put(name, newOwner.getUniqueId().toString());
        return true;
    }

}
