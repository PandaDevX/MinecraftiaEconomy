package com.redspeaks.minecraftiaeconomy.database;

import org.bukkit.Bukkit;

import java.sql.ResultSet;

public interface DataHandler {

    void onQueryDone(ResultSet resultSet);
}
