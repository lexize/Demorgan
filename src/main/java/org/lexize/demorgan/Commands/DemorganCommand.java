package org.lexize.demorgan.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lexize.demorgan.Database;
import org.lexize.demorgan.Demorgan;
import org.lexize.demorgan.TimeEnum;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class DemorganCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "jail":
                    {
                        if (args.length > 3) {
                            Player pl = Bukkit.getPlayer(args[1]);
                            if (!Demorgan.database.IsPlayerInDemorgan(pl.getUniqueId().toString())) {
                                if (pl == null) {
                                    sender.sendMessage(Component.text("Игрок с этим никнеймом не найден").color(TextColor.color(155,0,0)));
                                    return false;
                                }
                                long milliseconds;
                                String reason;
                                if (args.length > 4) {
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 4; i < args.length; i++) {
                                        sb.append(args[i] + " ");
                                    }
                                    reason = sb.toString();
                                }
                                else {
                                    reason = "Без причины";
                                }
                                try {
                                    milliseconds = GetSeconds(Double.parseDouble(args[2]), TimeEnum.valueOf(args[3]));
                                } catch (IllegalArgumentException e) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Неправильный формат или тип времени. &6Пример написания команды: &a/demorgan jail Zalupok 7 days Загриферил админа"));
                                    return false;
                                }
                                long curtime = Timestamp.from(Instant.now()).getTime();
                                if (milliseconds > 0) {
                                    Demorgan.database.PutPlayerInDemorgan(pl.getUniqueId().toString(), reason, curtime+milliseconds);
                                }
                                else {
                                    Demorgan.database.PutPlayerInDemorgan(pl.getUniqueId().toString(), reason, 0);
                                }

                                pl.setGameMode(GameMode.ADVENTURE);
                                pl.teleport(new Location(Demorgan.DemorganWorld, -1, 1, 5));
                                if (milliseconds > 0) {
                                    Bukkit.broadcast(Component.text(Demorgan.Prefix +

                                            ChatColor.translateAlternateColorCodes('&',
                                                    String.format("Игрок %s отправлен в &4Ульяновск&f на &e%s&f минут. Причина: &e%s",
                                                            pl.getName(),
                                                            ((double)Math.round((((double) milliseconds) / (1000*60))*10))/10, reason)
                                            )));
                                }
                                else {
                                    Bukkit.broadcast(Component.text(Demorgan.Prefix +

                                            ChatColor.translateAlternateColorCodes('&',
                                                    String.format("Игрок %s отправлен в &4Ульяновск&f &4Н А В С Е Г Д А&f. Причина: &e%s",
                                                            pl.getName(),
                                                            ((double)Math.round((((double) milliseconds) / (1000*60))*10))/10, reason)
                                            )));
                                }

                            }
                            else {
                                sender.sendMessage(Demorgan.Prefix + ChatColor.translateAlternateColorCodes('&', "&4Игрок уже в деморгане."));
                            }
                        }
                        else {
                            sender.sendMessage(Demorgan.Prefix
                                    + ChatColor.translateAlternateColorCodes('&', "&4Недостаточно аргументов."));
                        }
                    }
                    break;
                case "worlds":
                    for (World w:
                         Bukkit.getWorlds()) {
                        sender.sendMessage(w.getName());
                    }
                    break;
                case "takeout":
                    if (args.length == 2) {
                        Player pl = Bukkit.getPlayer(args[1]);
                        if (Demorgan.database.IsPlayerInDemorgan(pl.getUniqueId().toString())) {
                            Demorgan.database.TakeoutPlayerFromDemorgan(pl.getUniqueId().toString());
                            World overworld = Bukkit.getWorld("world");
                            pl.teleport(overworld.getSpawnLocation());
                            pl.setGameMode(GameMode.SURVIVAL);
                        }
                    }
                    else {
                        sender.sendMessage(Demorgan.Prefix
                                + ChatColor.translateAlternateColorCodes('&', "&4Недостаточно аргументов."));
                    }
                    break;
                case "visit":
                    if (sender instanceof Player player) {
                        player.teleport(new Location(Demorgan.DemorganWorld, -1, 1, 5));
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> possibleValues = new LinkedList<>();
        switch (args.length) {
            case 1:
                possibleValues.add("jail");
                possibleValues.add("visit");
                possibleValues.add("takeout");
                break;
            case 2:
                switch (args[0]) {
                    case "jail":
                        for (Player pl: Bukkit.getOnlinePlayers()) {
                            if (!Demorgan.database.IsPlayerInDemorgan(pl.getUniqueId().toString())) {
                                possibleValues.add(pl.getName());
                            }
                        }
                        break;
                    case "takeout":
                        for (Player pl: Bukkit.getOnlinePlayers()) {
                            if (Demorgan.database.IsPlayerInDemorgan(pl.getUniqueId().toString())) {
                                possibleValues.add(pl.getName());
                            }
                        }
                        break;
                }
                break;
            case 4:
                if (args[0].equals("jail")) {
                    for (TimeEnum t:
                         TimeEnum.values()) {
                        possibleValues.add(t.name());
                    }
                }
                break;
        }
        return possibleValues;
    }

    public long GetSeconds(double multiplier, TimeEnum timeEnum) {
        switch (timeEnum) {
            default:
                return (long) Math.floor(multiplier * 1000);
            case minutes:
                return (long) Math.floor(multiplier * 1000 * 60);
            case hours:
                return (long) Math.floor(multiplier * 1000 * 60 * 60);
            case days:
                return (long) Math.floor(multiplier * 1000 * 60 * 60 * 24);
            case week:
                return (long) Math.floor(multiplier * 1000 * 60 * 60 * 24 * 7);
            case month:
                return (long) Math.floor(multiplier * 1000 * 60 * 60 * 24 * 31);
        }
    }
}
