package com.redspeaks.minecraftiaeconomy.data;

import org.bukkit.OfflinePlayer;

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
                logs.addLog(player.getUniqueId().toString(), "&bAction: &7" + playerAction.getAction() + " by " + info);
            } else {
                logs.addLog(player.getUniqueId().toString(), "&bAction: &7" + playerAction.getAction() + " by " + info);
            }
        } else {
            logs.addLog(bankName, "&bAction: &7" + bankAction.getAction() + info);
        }

    }
}
