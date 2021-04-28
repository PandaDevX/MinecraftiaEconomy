package com.redspeaks.minecraftiaeconomy.database;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import com.redspeaks.minecraftiaeconomy.api.Bank;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class BankDatabase {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Bank bank = MinecraftiaEconomyManager.getBank();

    public BankDatabase() {
        if(DatabaseManager.getConnection() == null) return;
        try(PreparedStatement ps = preparedStatement("" +
                "CREATE TABLE IF NOT EXISTS banks " +
                "(name VARCHAR(100) NOT NULL, balance DOUBLE PRECISION, owner VARCHAR(100), PRIMARY KEY(name)" +
                ");"
        )) {
            ps.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveData(final HashMap<String, Double> map, final HashMap<String, String> bankOwnership) {
        if(map.isEmpty())return;
        if(DatabaseManager.getConnection() == null) return;
        scheduler.runTaskAsynchronously(MinecraftiaEconomy.getInstance(), () -> {
           try(PreparedStatement ps = preparedStatement("INSERT INTO banks(name,balance,owner) VALUES (?, ?, ?) ON DUPLICATE KEY REPLACE banks(name,balance,owner) VALUES (?, ?, ?);")) {
               for(String name : map.keySet()) {
                   ps.setString(1, name);
                   ps.setDouble(2, map.getOrDefault(name, 0D));
                   ps.setString(3, bankOwnership.get(name));
                   ps.setString(4, name);
                   ps.setDouble(5, map.getOrDefault(name, 0D));
                   ps.setString(6, bankOwnership.get(name));
                   ps.executeUpdate();
               }
               map.clear();
           }catch (SQLException e) {
               e.printStackTrace();
           }
        });
    }

    public void loadData(final DataHandler dataHandler) {
        if(DatabaseManager.getConnection() == null) return;
        scheduler.runTaskAsynchronously(MinecraftiaEconomy.getInstance(), () -> {
            try(PreparedStatement ps = preparedStatement("SELECT * FROM banks")) {
                final ResultSet resultSet = ps.executeQuery();
                scheduler.runTask(MinecraftiaEconomy.getInstance(), () -> {
                   dataHandler.onQueryDone(resultSet);
                });
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isEmpty() {
        try(Statement s = DatabaseManager.getConnection().createStatement()) {
            try(ResultSet r = s.executeQuery("SELECT COUNT(*) AS rowcount FROM banks")) {
                r.next();
                int count = r.getInt("rowcount");
                if(count > 0) {
                    return true;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }




    public PreparedStatement preparedStatement(String statement) throws SQLException {
        return DatabaseManager.getConnection().prepareStatement(statement);
    }
}
