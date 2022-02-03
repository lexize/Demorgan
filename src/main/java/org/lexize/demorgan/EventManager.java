package org.lexize.demorgan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventManager implements Listener {
    public static List<Player> joinedPlayers;
    public static Listener currentListener;
    public static HashMap<String, Location> playerJoinLocation;
    public static Location playerTeleportLocation;
    public static boolean opened = false;

    public static void Init() {
        if (currentListener != null) {
            HandlerList.unregisterAll(currentListener);
        }
        if (joinedPlayers != null & !joinedPlayers.isEmpty()) {
            for (Player pl :
                 joinedPlayers) {
                pl.teleport(playerJoinLocation.get(pl.getUniqueId().toString()));
            }
        }
        opened = false;
        playerJoinLocation = new HashMap<>();
        playerTeleportLocation = null;
        Bukkit.getPluginManager().registerEvents(new EventManager(), Demorgan.instance);

    }

    public static void SetListener(String path, String listenerClassName) throws MalformedURLException, ClassNotFoundException {
        URL url = new File(path).toURI().toURL();
        URL[] urls = new URL[]{url};
        ClassLoader cl = new URLClassLoader(urls);
        Listener listener = (Listener) cl.loadClass(listenerClassName).cast(Listener.class);
        HandlerList.unregisterAll(currentListener);
        Bukkit.getPluginManager().registerEvents(listener, Demorgan.instance);
    }

    public static void SetLocation(Player pl) {
        playerTeleportLocation = pl.getLocation();
    }

    public static void OnPlayerJoin(Player pl) {
        playerJoinLocation.put(pl.getUniqueId().toString(), pl.getLocation());
        pl.teleport(playerTeleportLocation);
    }

    public static void EndEvent() {
        for (Map.Entry<String, Location> entry:
                playerJoinLocation.entrySet()) {
            Bukkit.getPlayer(UUID.fromString(entry.getKey())).teleport(entry.getValue());
        }
    }

    @EventHandler
    public void OnLeave(PlayerQuitEvent event) {
        if (playerJoinLocation.containsKey(event.getPlayer().getUniqueId().toString())) {
            event.getPlayer().teleport(playerJoinLocation.get(event.getPlayer().getUniqueId().toString()));
        }
    }
}
