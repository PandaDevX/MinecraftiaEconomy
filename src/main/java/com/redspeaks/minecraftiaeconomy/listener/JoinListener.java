package com.redspeaks.minecraftiaeconomy.listener;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    public JoinListener(MinecraftiaEconomy plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(MinecraftiaEconomy.getInstance().getEconomyDatabase().exists(e.getPlayer())) return;

        MinecraftiaEconomy.getInstance().getEconomyDatabase().setMoney(e.getPlayer(), MinecraftiaEconomy.getInstance().getConfig().getDouble("starter"));
    }
}
