package com.redspeaks.minecraftiaeconomy.database;

import com.redspeaks.minecraftiaeconomy.api.Economy;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;
import java.sql.*;
import java.util.HashMap;

public class EconomyDatabase {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Economy economy = MinecraftiaEconomyManager.getEconomy();

    public EconomyDatabase() {
        if(DatabaseManager.getConnection() == null) return;
        try(PreparedStatement ps = preparedStatement("" +
                "CREATE TABLE IF NOT EXISTS economy " +
                "(uuid CHAR(36) NOT NULL, balance DOUBLE PRECISION, PRIMARY KEY(uuid)" +
                ");"
        )) {
            ps.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Double> retrieveDatas() {
        HashMap<String, Double> hashMap = new HashMap<>();
        try (PreparedStatement ps = preparedStatement("SELECT * FROM economy")) {
            try(ResultSet resultSet = ps.executeQuery()) {
                while(resultSet.next()) {
                    hashMap.put(resultSet.getString("uuid"), resultSet.getDouble("balance"));
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return hashMap;
    }
    public double getBalance(OfflinePlayer player) {
        try(PreparedStatement ps = preparedStatement("SELECT * FROM economy WHERE uuid=?")) {
            ps.setString(1, player.getUniqueId().toString());
            try(ResultSet resultSet = ps.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getDouble("balance");
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setMoney(OfflinePlayer player, double amount) {
        if(!exists(player)) {
            try(PreparedStatement ps = preparedStatement("INSERT INTO economy(uuid,balance) VALUES (?,?)")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setDouble(2, amount);
                ps.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try(PreparedStatement ps = preparedStatement("UPDATE economy SET balance=? WHERE uuid=?")) {
                ps.setDouble(1, amount);
                ps.setString(2, player.getUniqueId().toString());
                ps.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean exists(OfflinePlayer player) {
        try(PreparedStatement ps = preparedStatement("SELECT * FROM economy WHERE uuid=?")) {
            ps.setString(1, player.getUniqueId().toString());
            if(ps.executeQuery().next()) {
                return true;
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
