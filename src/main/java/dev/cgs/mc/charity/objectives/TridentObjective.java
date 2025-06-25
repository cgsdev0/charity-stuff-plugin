package dev.cgs.mc.charity.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

@Objective.Meta(key = "trident", name = "Poseidon's Apprentice", kind = Objective.Kind.PER_TEAM,
    worth = 50, desc = "Obtain a trident")
public class TridentObjective extends Objective implements Listener {
  @EventHandler
  public void onPickup(EntityPickupItemEvent event) {
    if (event.getItem().getItemStack().getType() != Material.TRIDENT)
      return;
    if (event.getEntity() instanceof Player player) {
      unlock(player);
    }
  }
}
