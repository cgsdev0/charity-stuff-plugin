package dev.cgs.mc.charity.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

@Objective.Meta(key = "mine_diamonds", name = "Diamonds!", kind = Objective.Kind.PER_PLAYER,
    worth = 2, desc = "Mine diamond ore")
public class MineDiamondObjective extends Objective implements Listener {
  @EventHandler
  public void onBreak(BlockDropItemEvent event) {
    Player p = event.getPlayer();
    if (p == null)
      return;
    if (event.getBlock().getType() != Material.DIAMOND_ORE && event.getBlock().getType() != Material.DEEPSLATE_DIAMOND_ORE)
      return;
    // breaking with your hand dont count
    if (event.getItems().isEmpty())
      return;
    // silk touch dont count
    if (event.getItems().get(0).getItemStack().getType() == Material.DIAMOND_ORE || event.getItems().get(0).getItemStack().getType() == Material.DEEPSLATE_DIAMOND_ORE)
      return;
    unlock(p);
  }
}
