package com.redspeaks.minecraftiaeconomy.database;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final String HOST;
    private final String PORT;
    private final String DATABASE;
    private final String USER;
    private final String PASS;
    public DatabaseManager(MinecraftiaEconomy plugin) {
        this.HOST = plugin.getConfig().getString("MySQL.host");
        this.PORT = plugin.getConfig().getString("MySQL.port");
        this.DATABASE = plugin.getConfig().getString("MySQL.database");
        this.USER = plugin.getConfig().getString("MySQL.user");
        this.PASS = plugin.getConfig().getString("MySQL.pass");
    }


    private Connection connection = null;

    public Connection getConnection() throws SQLException {
        if(connection == null) {
            return connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false",
                    USER, PASS);
        }
        return connection;
    }


}
