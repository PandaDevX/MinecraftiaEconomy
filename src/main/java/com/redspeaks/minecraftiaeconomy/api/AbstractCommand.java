package com.redspeaks.minecraftiaeconomy.api;

import com.redspeaks.minecraftiaeconomy.MinecraftiaEconomy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCommand implements CommandExecutor {
    private final CommandInfo info;

    public AbstractCommand() {
        info = getClass().getDeclaredAnnotation(CommandInfo.class);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MinecraftiaEconomy.getInstance().reloadConfig();
        if(!sender.hasPermission(info.permission())) {
            sender.sendMessage(ChatUtil.colorize("&7Missing required &cpermission"));
            return true;
        }
        if(info.requiresPlayer()) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(ChatUtil.colorize("&cYou must be a player to do that"));
                return true;
            }
            execute((Player) sender, args);
            return true;
        }
        execute(sender, args);
        return false;
    }

    public CommandInfo getInfo() {
        return info;
    }

    public void execute(Player player, String[] args) {}
    public void execute(CommandSender sender, String[] args) {}


    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatUtil.colorize(message));
    }

    public void sendMessage(CommandSender sender, String message, String permission) {
        if(sender.hasPermission(permission)) {
            sendMessage(sender, message);
        }
    }

    public void sendUnknownMessage(CommandSender sender) {
        sendMessage(sender, "&7Unknown command");
    }

    public void sendNoPermission(CommandSender sender) {
        sendMessage(sender, "&7You have no permission to do that");
    }

    public void sendCorrectArguments(CommandSender sender, String... args) {
        for(String arg : args) {
            if(sender.hasPermission("economy.command." + arg.split(" ")[1])) {
                sendMessage(sender, "&7Correct usage: &b/" + arg);
            }
        }
    }

    public void sendCorrectUsage(CommandSender sender, String usage) {
        sendMessage(sender, "&7Correct usage: &b/" + usage);
    }

    public boolean hasPermissionFrom(CommandSender sender, String prefix, String... nodes) {
        for(String node : nodes) {
            if(sender.hasPermission(prefix + node)) {
                return true;
            }
        }
        return false;
    }

}
