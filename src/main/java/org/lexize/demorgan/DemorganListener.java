package org.lexize.demorgan;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DemorganListener implements Listener {
    @EventHandler
    public void OnDeath(PlayerDeathEvent event) {
        if (Demorgan.database.IsPlayerInDemorgan(event.getPlayer().getUniqueId().toString())) {
            event.setCancelled(true);
            event.getPlayer().setHealth(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        }
    }

    @EventHandler
    public void OnDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player pl) {
            if (Demorgan.database.IsPlayerInDemorgan(pl.getUniqueId().toString())) {
                var cause = event.getCause();
                if (cause == EntityDamageEvent.DamageCause.WITHER | cause == EntityDamageEvent.DamageCause.LAVA | cause == EntityDamageEvent.DamageCause.HOT_FLOOR | cause == EntityDamageEvent.DamageCause.FIRE| cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
