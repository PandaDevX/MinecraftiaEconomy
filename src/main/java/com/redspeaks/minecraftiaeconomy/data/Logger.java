package com.redspeaks.minecraftiaeconomy.data;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import org.bukkit.OfflinePlayer;

public class Logger {

    private static Logs logs = MinecraftiaEconomy.getInstance().getLogFile();

    public static MinecraftiaEconomyLogger log(Actions.BankAction bankAction, String name) {
        return new MinecraftiaEconomyLogger(logs, bankAction, name);
    }

    public static MinecraftiaEconomyLogger log(Actions.PlayerAction playerAction, OfflinePlayer player) {
        return new MinecraftiaEconomyLogger(logs, playerAction, player);
    }

}
