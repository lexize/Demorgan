package org.lexize.demorgan;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.lexize.demorgan.Commands.DemorganCommand;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

public final class Demorgan extends JavaPlugin {

    public static World DemorganWorld;
    public static Database database;
    public static Demorgan instance;
    public static String Prefix = ChatColor.translateAlternateColorCodes('&', "[&4Ульяновск&f]");
    @Override
    public void onEnable() {
        WorldCreator creator = new WorldCreator("demorgan");
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        creator.generatorSettings("{\"structures\": {\"structures\":{}}, \"layers\": [{\"block\": \"air\", \"height\": 1}], \"biome\":\"plains\"}");
        DemorganWorld = getServer().createWorld(creator);
        try {
            database = new Database("./plugins/Demorgan.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        var command = new DemorganCommand();
        new DemorganChecker().runTaskTimer(this, 40, 100);
        getServer().getPluginManager().registerEvents(new DemorganListener(), this);
        instance = this;
        getCommand("demorgan").setExecutor(command);
        getCommand("demorgan").setTabCompleter(command);

    }

    public class DemorganChecker extends BukkitRunnable {

        @Override
        public void run() {
            if (database.IsAnyPlayerInDemorgan()) {
                for (Player pl:
                     Bukkit.getOnlinePlayers()) {
                    if (database.IsPlayerInDemorgan(pl.getUniqueId().toString())) {
                        long until = database.GetPlayersUntil(pl.getUniqueId().toString());
                        long cur = Timestamp.from(Instant.now()).getTime();
                        if (cur > until & until > 0) {
                            Demorgan.database.TakeoutPlayerFromDemorgan(pl.getUniqueId().toString());
                            World overworld = Bukkit.getWorld("world");
                            pl.teleport(overworld.getSpawnLocation());
                            pl.setGameMode(GameMode.SURVIVAL);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
