package com.redspeaks.minecraftiaeconomy.api;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings("all")
public class ChatUtil {

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String strip(String text) {
        return ChatColor.stripColor(text);
    }

    public static String commas(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat.format(amount);
    }

    public static void log(OfflinePlayer player, int amount, Transaction transaction) {
        switch (transaction) {
            case DEPOSIT:
                System.out.println(player.getUniqueId().toString() + " " +  player.getName());
                System.out.println("Deposited amount: " + amount);
                break;
            default:
                System.out.println(player.getUniqueId().toString() + " " +  player.getName());
                System.out.println("Withdrawn amount: " + amount);
                break;
        }
    }

    public static boolean isInt(String text) {
        try {
            Double.parseDouble(text);
        }catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static HashMap<String, Double> reverseOrder(HashMap<String, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(o1.getKey()));
                OfflinePlayer offlinePlayer2 = Bukkit.getOfflinePlayer(UUID.fromString(o2.getKey()));
                return (MinecraftiaEconomyManager.getEconomy().getTotalBalance(offlinePlayer2).orElse(0D).compareTo(
                        MinecraftiaEconomyManager.getEconomy().getTotalBalance(offlinePlayer).orElse(0D)
                ));
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static String getEconomyFormat(double amount) {
        return commas(amount) + MinecraftiaEconomyManager.getEconomy().getSuffix();
    }
}
