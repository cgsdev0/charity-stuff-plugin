package dev.cgs.mc.charity.objectives;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@Objective.Meta(key = "wither", name = "Wither Slayer", kind = Objective.Kind.PER_TEAM, worth = 100,
    desc = "Kill the wither")
public class WitherObjective extends Objective implements Listener {
  @EventHandler
  public void onKill(EntityDeathEvent event) {
    if (event.getEntityType() != EntityType.WITHER)
      return;
    if (event.getDamageSource().getCausingEntity() instanceof Player player) {
      unlock(player);
    }
  }
}
