package org.lexize.demorgan.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lexize.demorgan.EventManager;

import java.io.File;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "join":
                    if (sender instanceof Player pl) {
                        if (EventManager.opened) {
                            EventManager.OnPlayerJoin(pl);
                        }
                    }
                    break;
                case "open":
                    if (!sender.hasPermission("event.admin")) break;
                    EventManager.opened = true;
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6Вход на ивент успешно &aоткрыт&6."));
                    break;
                case "close":
                    if (!sender.hasPermission("event.admin")) break;
                    EventManager.opened = false;
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6Вход на ивент успешно &cзакрыт&6."));
                    break;
                case "reset":
                    if (!sender.hasPermission("event.admin")) break;
                    EventManager.Init();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6Ивент успешно сброшен."));
                case "setClass":
                    if (!sender.hasPermission("event.admin")) break;
                    if (args.length >= 2) {
                        try {
                            EventManager.SetListener("classes", args[1]);
                        } catch (MalformedURLException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&cПроизошла хуйня ебаная, скажи плагинеру чекнуть консоль."));
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&cКласс не найден."));
                            e.printStackTrace();
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&cНедостаточно аргументов."));
                    }
                    break;
                case "setPosition":
                    if (!sender.hasPermission("event.admin")) break;
                    if (sender instanceof Player pl) {
                        EventManager.SetLocation(pl);
                    }
                    else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&cТы консоль)"));
                    }
                    break;
                case "event":
                    if (!sender.hasPermission("event.admin")) break;
                    EventManager.EndEvent();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6Ивент закончен)"));
                    break;
            }
        }



        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> possibleValues = new LinkedList<>();
        switch (args.length) {
            case 1:
                if (sender.hasPermission("event.admin")) {
                    possibleValues.add("reset");
                    possibleValues.add("setClass");
                    possibleValues.add("setPosition");
                    possibleValues.add("open");
                    possibleValues.add("close");
                    possibleValues.add("event");
                }
                possibleValues.add("join");

                break;
            case 2:
                if (sender.hasPermission("event.admin") & args[1].equals("setClass")) {
                    for (String f:
                         new File("classes").list()) {
                        String regex = "(.+)\\.java$";
                        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                        Matcher matcher = pattern.matcher(f);
                        if (matcher.matches()) {
                            possibleValues.add(matcher.group(1));
                        }
                    }
                }
                break;
        }
        return possibleValues;
    }
}
