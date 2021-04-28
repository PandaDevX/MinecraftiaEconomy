package com.redspeaks.minecraftiaeconomy.database;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import com.redspeaks.minecraftiaeconomy.api.Economy;
import com.redspeaks.minecraftiaeconomy.api.MinecraftiaEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class EconomyDatabase {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Economy economy = MinecraftiaEconomyManager.getEconomy();

    public EconomyDatabase() {
        if(DatabaseManager.getConnection() == null) return;
        try(PreparedStatement ps = preparedStatement("" +
                "CREATE TABLE IF NOT EXISTS economy " +
                "(uuid CHAR(36) NOT NULL, balance DOUBLE PRECISION, bank VARCHAR(100), extra_bank VARCHAR(100), PRIMARY KEY(uuid)" +
                ");"
        )) {
            ps.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveData(final HashMap<String, Double> map) {
        if(map.isEmpty())return;
        if(DatabaseManager.getConnection() == null) return;
        scheduler.runTaskAsynchronously(MinecraftiaEconomy.getInstance(), () -> {
           try(PreparedStatement ps = preparedStatement("INSERT INTO economy(uuid,balance,bank,extra_bank) VALUES (?, ?, ?, ?) ON DUPLICATE KEY REPLACE economy(uuid,balance,bank,extra_bank) VALUES (?, ?, ?, ?);")) {
               for(String uuid : map.keySet()) {
                   ps.setString(1, uuid);
                   ps.setDouble(2, map.getOrDefault(uuid, 0D));
                   OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                   if(economy.hasBankAccount(player)) {
                       ps.setString(3, economy.getBank(player).get());
                   } else {
                       ps.setNull(3, Types.NULL);
                   }
                   if(economy.hasExtraBankAccount(player)) {
                       ps.setString(4, economy.getExtraBank(player).get());
                   } else {
                       ps.setNull(4, Types.NULL);
                   }

                   ps.setString(5, uuid);
                   ps.setDouble(6, map.getOrDefault(uuid, 0D));
                   if(economy.hasBankAccount(player)) {
                       ps.setString(7, economy.getBank(player).get());
                   } else {
                       ps.setNull(7, Types.NULL);
                   }
                   if(economy.hasExtraBankAccount(player)) {
                       ps.setString(8, economy.getExtraBank(player).get());
                   } else {
                       ps.setNull(8, Types.NULL);
                   }
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
            try(PreparedStatement ps = preparedStatement("SELECT * FROM economy")) {
                final ResultSet resultSet = ps.executeQuery();
                scheduler.runTask(MinecraftiaEconomy.getInstance(), () -> {
                   dataHandler.onQueryDone(resultSet);
                });
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Optional<String> getBank(OfflinePlayer player) {
        try(PreparedStatement ps = preparedStatement("SELECT * FROM economy WHERE uuid=?")) {
            ps.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()) {
                return Optional.ofNullable(resultSet.getString("bank"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<String> getExtraBank(OfflinePlayer player) {
        try(PreparedStatement ps = preparedStatement("SELECT * FROM economy WHERE uuid=?")) {
            ps.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()) {
                return Optional.ofNullable(resultSet.getString("extra_bank"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean isEmpty() {
        try(Statement s = DatabaseManager.getConnection().createStatement()) {
            try(ResultSet r = s.executeQuery("SELECT COUNT(*) AS rowcount FROM economy")) {
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
