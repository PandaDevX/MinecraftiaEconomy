package com.redspeaks.minecraftiaeconomy.api;

import org.bukkit.OfflinePlayer;

import java.util.Optional;


@SuppressWarnings("all")
public interface Economy {

    /**
     *
     * @param player get amount from player wallet
     * @return Optional object
     */
    Optional<Double> getBalance(OfflinePlayer player);

    /**
     *
     * @param player get amount from player wallet and banks
     * @return Optional object
     */
    Optional<Double> getTotalBalance(OfflinePlayer player);

    /**
     *
     * @param player player included from this event
     * @param withBank set to true if you want to compute balance including bank accounts
     * @return get the balance to text with commas
     */
    String getBalanceFormat(OfflinePlayer player, boolean withBank);

    /**
     *
     * @param player player included from this event
     * @param amount amount to withdraw
     * @return true if succeeded
     */
    boolean withdraw(OfflinePlayer player, double amount);

    /**
     *
     * @param player recipient
     * @param amount amount to deposit
     */
    void deposit(OfflinePlayer player, double amount);

    /**
     *
     * @return economy default suffix which is e
     */
    default String getSuffix() {
        return "e";
    }

    /**
     *
     * @param player player included from this event
     * @return true if bank found
     */
    boolean hasBankAccount(OfflinePlayer player);

    /**
     *
     * @param player player included from this event
     * @return true if extra bank found
     */
    boolean hasExtraBankAccount(OfflinePlayer player);

    /**
     *
     * @param player player included from this event
     * @param amount amount to set
     */
    void setBalance(OfflinePlayer player, double amount);

    /**
     *
     * @param player player included from this event
     * @return Optional object that holds name of player bank account
     */
    default Optional<String> getBank(OfflinePlayer player) {
        return MinecraftiaEconomyManager.getBank(player);
    }

    /**
     *
     * @param player player included from this
     * @return Optional object that holds name of player extra bank account
     */
    default Optional<String> getExtraBank(OfflinePlayer player) {
        return MinecraftiaEconomyManager.getExtraBank(player);
    }

    /**
     *
     * @param sender sender
     * @param recipient recipient
     * @param amount amount to transfer
     * @return true if successful
     */
    boolean transferBalance(OfflinePlayer sender, OfflinePlayer recipient, double amount);
}
