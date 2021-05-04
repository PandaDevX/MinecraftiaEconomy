package com.redspeaks.minecraftiaeconomy.database;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import com.redspeaks.minecraftiaeconomy.api.Bank;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class BankDatabase {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Bank bank = MinecraftiaEconomyManager.getBank();

    public BankDatabase() {
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

    public Set<String> getBanks(OfflinePlayer player) {
        Set<String> set = new HashSet<>();
        try (PreparedStatement ps = preparedStatement("SELECT * FROM banks WHERE owner=?")) {
            ps.setString(1, player.getUniqueId().toString());
            try(ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    set.add(resultSet.getString("name"));
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }

    public double getBalance(String name) {
        try(PreparedStatement ps = preparedStatement("SELECT * FROM banks WHERE name=?")) {
            ps.setString(1, name);
            try(ResultSet resultSet = ps.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getDouble("balance");
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    public String getOwner(String name) {
        try(PreparedStatement ps = preparedStatement("SELECT * FROM banks WHERE name=?")) {
            ps.setString(1, name);
            try(ResultSet resultSet = ps.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getString("owner");
                }
                return null;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void changeOwner(String name, OfflinePlayer owner) {
        try(PreparedStatement ps = preparedStatement("UPDATE banks SET owner=? WHERE name=?")) {
            ps.setString(1, owner.getUniqueId().toString());
            ps.setString(2, name);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMoney(String name, double amount) {
        try(PreparedStatement ps = preparedStatement("INSERT INTO banks(name,balance) VALUES (?,?) ON DUPLICATE KEY UPDATE name balance=?")) {
            ps.setString(1, name);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(String name, OfflinePlayer owner) {
        try(PreparedStatement ps = preparedStatement("INSERT INTO banks(name,balance,owner) VALUES (?,?,?)")) {
            ps.setString(1, name);
            ps.setDouble(2, 0);
            ps.setString(3, owner.getUniqueId().toString());
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String name) {
        try(PreparedStatement ps = preparedStatement("SELECT * FROM banks WHERE name=?")) {
            ps.setString(1, name);
            try(ResultSet resultSet = ps.executeQuery()) {
                if(resultSet.next()) {
                    return true;
                }
                return false;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean delete(String name) {
        try(PreparedStatement ps = preparedStatement("DELETE FROM banks WHERE name=?")) {
            ps.setString(1, name);
            ps.executeUpdate();
            return true;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public PreparedStatement preparedStatement(String statement) throws SQLException {
        return MinecraftiaEconomy.getInstance().getDatabaseManager().getConnection().prepareStatement(statement);
    }
}
