package com.redspeaks.minecraftiaeconomy;

import com.redspeaks.minecraftiaeconomy.api.*;
import com.redspeaks.minecraftiaeconomy.commands.BalTopCommand;
import com.redspeaks.minecraftiaeconomy.commands.BalanceCommand;
import com.redspeaks.minecraftiaeconomy.commands.PayCommand;
import com.redspeaks.minecraftiaeconomy.commands.admin.BankCommand;
import com.redspeaks.minecraftiaeconomy.commands.admin.MoneyCommand;
import com.redspeaks.minecraftiaeconomy.data.Logs;
import com.redspeaks.minecraftiaeconomy.database.BankDatabase;
import com.redspeaks.minecraftiaeconomy.database.DataHandler;
import com.redspeaks.minecraftiaeconomy.database.DatabaseManager;
import com.redspeaks.minecraftiaeconomy.database.EconomyDatabase;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@SuppressWarnings("all")
public final class MinecraftiaEconomy extends JavaPlugin {

    private static MinecraftiaEconomy instance = null;
    private Bank bank = null;
    private Economy economy = null;
    private Logs logs = null;
    public static HashMap<String, Double> map = new HashMap<>();
    public static HashMap<String, Double> bankMap = new HashMap<>();
    public static HashMap<String, String> bankOwnership = new HashMap<>();
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
        loadData();

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
        saveData();
        map.clear();
        bankMap.clear();
        bankOwnership.clear();
        instance = null;
        bank = null;
        logs = null;
        economy = null;
        bankDatabase =null;
        economyDatabase = null;
        bankMap = null;
        bankOwnership = null;

        getLogger().info("Successfully closed");
    }

     public Logs getLogFile() {
        return logs;
     }

    public void setupBank() {
        bank = new Bank() {
            @Override
            public boolean withdraw(String name, double amount) {
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
                    bankMap.remove(name);
                    bankOwnership.remove(name);
                    return true;
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
                return Optional.of(bankMap.getOrDefault(name, 0D));
            }

            @Override
            public void setBalance(String name, double amount) {
                bankMap.put(name, amount);
            }

            @Override
            public boolean create(String name, OfflinePlayer owner) {
                if(MinecraftiaEconomyManager.bankExists(name)) {
                   return false;
                }
                if(getEconomy().hasBankAccount(owner) && getEconomy().hasExtraBankAccount(owner)) {
                    return false;
                }
                bankMap.put(name, 0D);
                bankOwnership.put(name, owner.getUniqueId().toString());
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
                return Optional.of(map.getOrDefault(player.getUniqueId().toString(), 0D));
            }

            @Override
            public String getBalanceFormat(OfflinePlayer player, boolean withBank) {
                if(withBank) {
                    return ChatUtil.commas(getTotalBalance(player).orElse(0D)) + getSuffix();
                }
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
                return getBank(player).isPresent();
            }

            @Override
            public boolean hasExtraBankAccount(OfflinePlayer player) {
                return getExtraBank(player).isPresent();
            }

            @Override
            public void setBalance(OfflinePlayer player, double amount) {
                map.put(player.getUniqueId().toString(), amount);
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

            @Override
            public Optional<Double> getTotalBalance(OfflinePlayer player) {
                double wallet = getBalance(player).orElse(0D);
                if(hasBankAccount(player)) {
                    wallet += MinecraftiaEconomyManager.getBank().getBalance(getBank(player).get()).orElse(0D);
                }
                if(hasExtraBankAccount(player)) {
                    wallet += MinecraftiaEconomyManager.getBank().getBalance(getExtraBank(player).get()).orElse(0D);
                }
                return Optional.of(wallet);
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

    public void loadData() {
        if(!bankDatabase.isEmpty()) {
            bankDatabase.loadData(new DataHandler() {
                @Override
                public void onQueryDone(ResultSet resultSet) {
                    try {
                        while (resultSet.next()) {
                            bankMap.put(resultSet.getString("name"), resultSet.getDouble("balance"));
                            bankOwnership.put(resultSet.getString("name"), resultSet.getString("owner"));
                        }
                        resultSet.close();
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if(!economyDatabase.isEmpty()) {
            economyDatabase.loadData(new DataHandler() {
                @Override
                public void onQueryDone(ResultSet resultSet) {
                    try {
                        while (resultSet.next()) {
                            map.put(resultSet.getString("uuid"), resultSet.getDouble("balance"));
                        }
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void saveData() {
        economyDatabase.saveData(map);
        bankDatabase.saveData(bankMap, bankOwnership);
    }

    public BankDatabase getBankDatabase() {
        return bankDatabase;
    }

    public EconomyDatabase getEconomyDatabase() {
        return economyDatabase;
    }
}
