package dev.cgs.mc.charity.objectives;

import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

@Objective.Meta(key = "diamond_gift", name = "Friendly Gesture", kind = Objective.Kind.PER_PLAYER,
    worth = 2, desc = "Give a diamond to the opposing team")
public class FriendlyGestureObjective extends Objective implements Listener {
  @EventHandler
  public void onPickup(EntityPickupItemEvent event) {
    if (event.getItem().getItemStack().getType() != Material.DIAMOND)
      return;

    UUID thrower = event.getItem().getThrower();
    if (event.getEntity() instanceof Player b) {
      if (thrower == null)
        return;
      Player a = Bukkit.getServer().getPlayer(thrower);
      if (a == null)
        return;
      Team ta = Teams.get().fromPlayer(a);
      Team tb = Teams.get().fromPlayer(b);
      if (a == null || b == null)
        return;
      if (ta.getLeader() != tb.getLeader()) {
        unlock(a);
      }
    }
  }
}
