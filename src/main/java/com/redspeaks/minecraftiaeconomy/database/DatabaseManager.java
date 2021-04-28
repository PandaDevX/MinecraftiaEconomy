package com.redspeaks.minecraftiaeconomy.database;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    
    private final static String HOST = MinecraftiaEconomy.getInstance().getConfig().getString("MySQL.host");
    private final static String PORT = MinecraftiaEconomy.getInstance().getConfig().getString("MySQL.port");
    private final static String DATABASE = MinecraftiaEconomy.getInstance().getConfig().getString("MySQL.database");
    private final static String USER = MinecraftiaEconomy.getInstance().getConfig().getString("MySQL.user");
    private final static String PASS = MinecraftiaEconomy.getInstance().getConfig().getString("MySQL.pass");
    private static Connection connection = null;

    public static void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false",
                    USER, PASS);
    }

    public static Connection getConnection() {
        return connection;
    }

}
