package com.redspeaks.minecraftiaeconomy.data;

import com.redspeaks.minecraftiaeconomy.api.ChatUtil;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MinecraftiaEconomyLogger {

    private Logs logs;
    private Actions.BankAction bankAction = null;
    private String bankName = null;
    private OfflinePlayer player = null;
    private Actions.PlayerAction playerAction = null;

    public MinecraftiaEconomyLogger(Logs logs, Actions.BankAction bankAction, String bankName) {
        this.logs = logs;
        this.bankAction = bankAction;
        this.bankName = bankName;
    }

    public MinecraftiaEconomyLogger(Logs logs, Actions.PlayerAction playerAction, OfflinePlayer player) {
        this.logs = logs;
        this.playerAction = playerAction;
        this.player = player;
    }

    public void info(String info) {
        if(player != null) {
            if (bankName == null) {
                logs.addLog(player.getUniqueId().toString(), "&7Action: " + playerAction.getAction() + " &7Initiator: &b" + player.getName() + info);
            } else {
                logs.addLog(player.getUniqueId().toString(), "&7Action: " + playerAction.getAction() + " &7Initiator: &b" + player.getName() + info);
                logs.addLog(player.getUniqueId().toString(), "&7Bank name: &b" + bankName);
            }
        } else {
            logs.addLog(bankName, "&7Action: " + bankAction.getAction() + info + "&7Bank name: &b" + bankName);
        }

    }
}
