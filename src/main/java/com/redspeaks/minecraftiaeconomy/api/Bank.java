package com.redspeaks.minecraftiaeconomy.api;

import org.bukkit.OfflinePlayer;

import java.util.Optional;

@SuppressWarnings("all")
public interface Bank {

    /**
     *
     * @param name name of the bank
     * @param amount amount to withdraw
     * @return false if transaction failed
     */
    boolean withdraw(String name, double amount);

    /**
     *
     * @param name name of the bank
     * @param amount amount to deposit
     */
    void deposit(String name, double amount);

    /**
     *
     * @param name name of the bank
     * @return false if name of an account does not exists
     */
    boolean delete(String name);

    /**
     *
     * @param name the source of transfer
     * @param target the recipient of transfer
     * @param amount amount to transfer
     * @return false if sender or receiver bank does not exist or sender have insufficient fund
     */
    boolean transfer(String name, String target, double amount);

    /**
     *
     * @param name the source of transfer
     * @param target the player recipient
     * @param amount amount to transfer
     * @return false if sender bank does not exist or the bank does not exist
     */
    boolean transfer(String name, OfflinePlayer target, double amount);

    /**
     *
     * @param name name of the bank
     * @return Optional object of the balance
     */
    Optional<Double> getBalance(String name);

    /**
     *
     * @param name name of the bank
     * @param amount amount to set
     */
    void setBalance(String name, double amount);

    /**
     *
     * @param name name of the bank
     * @param owner owner of the account
     * @return false if name already exists
     */
    boolean create(String name, OfflinePlayer owner);

    /**
     *
     * @param name name of the bank
     * @param newOwner new owner of the account
     * @return false if account does not exists
     */
    boolean transferOwnerShip(String name, OfflinePlayer newOwner);
}
