package com.redspeaks.minecraftiaeconomy;

import com.redspeaks.minecraftiaeconomy.api.*;
import com.redspeaks.minecraftiaeconomy.commands.BalTopCommand;
import com.redspeaks.minecraftiaeconomy.commands.BalanceCommand;
import com.redspeaks.minecraftiaeconomy.commands.PayCommand;
import com.redspeaks.minecraftiaeconomy.commands.admin.BankCommand;
import com.redspeaks.minecraftiaeconomy.commands.admin.MoneyCommand;
import com.redspeaks.minecraftiaeconomy.data.Logs;
import com.redspeaks.minecraftiaeconomy.database.BankDatabase;
import com.redspeaks.minecraftiaeconomy.database.DatabaseManager;
import com.redspeaks.minecraftiaeconomy.database.EconomyDatabase;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.*;


@SuppressWarnings("all")
public final class MinecraftiaEconomy extends JavaPlugin {

    private static MinecraftiaEconomy instance = null;
    private Bank bank = null;
    private Economy economy = null;
    private Logs logs = null;
    private BankDatabase bankDatabase = null;
    private EconomyDatabase economyDatabase = null;

    @Override
    public void onEnable() {
        getLogger().info("Trying to connect to database");

        try {
            DatabaseManager.setup();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        getLogger().info("Successfully connected to database");

        // instance
        getLogger().info("Initializing instances...");
        instance = this;
        bankDatabase = new BankDatabase();
        economyDatabase = new EconomyDatabase();

        // load config and mysql data
        getLogger().info("Loading config files...");
        saveDefaultConfig();
        logs = new Logs(this);
        logs.setup();

        // setup bank and economy
        getLogger().info("Setting up economy...");
        setupBank();
        setupEconomy();

        getLogger().info("Registering commands...");
        registerCommands();


        getLogger().info("Everything has been loaded");
    }

    private void registerCommands() {
        List<AbstractCommand> list = Arrays.asList(new BalanceCommand(), new BalTopCommand(), new PayCommand(), new BankCommand(), new MoneyCommand());

        Iterator<AbstractCommand> iterator = list.iterator();
        while (iterator.hasNext()) {
            AbstractCommand command = iterator.next();
            getCommand(command.getInfo().name()).setExecutor(command);
        }
        list = null;
        iterator = null;
    }

    @Override
    public void onDisable() {

        getLogger().info("Closing storage...");
        instance = null;
        bank = null;
        logs = null;
        economy = null;
        bankDatabase =null;
        economyDatabase = null;
        getLogger().info("Successfully closed");
    }

     public Logs getLogFile() {
        return logs;
     }

    public void setupBank() {
        bank = new Bank() {
            @Override
            public boolean withdraw(String name, double amount) {
                if(!MinecraftiaEconomyManager.bankExists(name)) {
                    return false;
                }
                double balance = getBalance(name).orElse(0D);
                if(balance < amount || (balance-amount) < 0) {
                    if(getConfig().getBoolean("loan")) {
                        setBalance(name, getBalance(name).orElse(0D) - amount);
                        return true;
                    }
                    return false;
                }
                setBalance(name, balance - amount);
                return true;
            }

            @Override
            public void deposit(String name, double amount) {
                setBalance(name, getBalance(name).orElse(0D) + amount);
            }

            @Override
            public boolean delete(String name) {
                if(MinecraftiaEconomyManager.bankExists(name)) {
                    return getBankDatabase().delete(name);
                }
                return false;
            }

            @Override
            public boolean transfer(String name, String target, double amount) {
                if(withdraw(name, amount)) {
                    if(MinecraftiaEconomyManager.bankExists(target)) {
                        deposit(target, amount);
                        return true;
                    }
                    return false;
                }
                return false;
            }

            @Override
            public boolean transfer(String name, OfflinePlayer target, double amount) {
                if(withdraw(name, amount)) {
                    MinecraftiaEconomyManager.getEconomy().deposit(target, amount);
                    return true;
                }
                return false;
            }

            @Override
            public Optional<Double> getBalance(String name) {
                return Optional.of(getBankDatabase().getBalance(name));
            }

            @Override
            public void setBalance(String name, double amount) {
                getBankDatabase().setMoney(name, amount);
            }

            @Override
            public boolean create(String name, OfflinePlayer owner) {
                if(MinecraftiaEconomyManager.bankExists(name)) {
                   return false;
                }
                if(getEconomy().hasExtraBankAccount(owner)) {
                    return false;
                }
                getBankDatabase().create(name,owner);
                return true;
            }

            @Override
            public boolean transferOwnerShip(String name, OfflinePlayer newOwner) {
                return MinecraftiaEconomyManager.transferOwnership(name, newOwner);
            }
        };
    }

    private void setupEconomy() {
        economy = new Economy() {
            @Override
            public Optional<Double> getBalance(OfflinePlayer player) {
                return Optional.of(getEconomyDatabase().getBalance(player));
            }

            @Override
            public Optional<Double> getTotalBalance(OfflinePlayer player) {
                double wallet = getBalance(player).orElse(0D);
                Set<String> banks = getBankDatabase().getBanks(player);
                double bankTotal = 0;
                if(!banks.isEmpty()) {
                    for(String bank : banks) {
                        bankTotal += getBankDatabase().getBalance(bank);
                    }
                }
                return Optional.of(bankTotal + wallet);
            }

            @Override
            public String getBalanceFormat(OfflinePlayer player) {
                return ChatUtil.commas(getBalance(player).orElse(0D)) + getSuffix();
            }

            @Override
            public boolean withdraw(OfflinePlayer player, double amount) {
                double balance = getBalance(player).orElse(0D);
                if(balance < amount || (balance - amount) < 0) {
                    if(getConfig().getBoolean("loans")) {
                        setBalance(player, balance - amount);
                        return true;
                    }
                    return false;
                }
                setBalance(player, balance - amount);
                return true;
            }

            @Override
            public void deposit(OfflinePlayer player, double amount) {
                setBalance(player, getBalance(player).orElse(0D) + amount);
            }

            @Override
            public boolean hasBankAccount(OfflinePlayer player) {
                return !getBankDatabase().getBanks(player).isEmpty();
            }

            @Override
            public boolean hasExtraBankAccount(OfflinePlayer player) {
                return getBankDatabase().getBanks(player).size() > 1;
            }

            @Override
            public void setBalance(OfflinePlayer player, double amount) {
                getEconomyDatabase().setMoney(player, amount);
            }

            @Override
            public boolean transferBalance(OfflinePlayer sender, OfflinePlayer recipient, double amount) {
                if(withdraw(sender, amount)) {
                    deposit(recipient, amount);
                    return true;
                }
                return false;
            }

            @Override
            public boolean transferBalance(OfflinePlayer sender, String recipient, double amount) {
                if(withdraw(sender, amount)) {
                    MinecraftiaEconomyManager.getBank().deposit(recipient, amount);
                    return true;
                }
                return false;
            }
        };
    }




    public Bank getBank() {
        return this.bank;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public static MinecraftiaEconomy getInstance() {
        return instance;
    }

    public BankDatabase getBankDatabase() {
        return bankDatabase;
    }

    public EconomyDatabase getEconomyDatabase() {
        return economyDatabase;
    }
}
