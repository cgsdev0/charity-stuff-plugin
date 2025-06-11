package dev.cgs.mc.charity.objectives;

import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

@Objective.Meta(
    key = MineDiamondObjective.key, name = "Diamonds!", kind = Objective.Kind.PER_PLAYER, worth = 2)
public class MineDiamondObjective implements Objective, Listener {
  public static final String key = "mine-diamonds";

  @EventHandler
  public void onBreak(BlockDropItemEvent event) {
    Player p = event.getPlayer();
    if (p == null)
      return;
    if (event.getBlock().getType() != Material.DIAMOND_ORE)
      return;
    // breaking with your hand dont count
    if (event.getItems().isEmpty())
      return;
    // silk touch dont count
    if (event.getItems().get(0).getItemStack().getType() == Material.DIAMOND_ORE)
      return;
    Team t = Teams.get().fromPlayer(p);
    if (t == null)
      return;
    t.unlock(key, p);
  }
}
