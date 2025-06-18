package dev.cgs.mc.charity.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

@Objective.Meta(key = "eat_glowberry", name = "Light Snack", kind = Objective.Kind.PER_PLAYER,
    worth = 1, desc = "Eat some glowberries.")
public class GlowberryObjective extends Objective implements Listener {
  @EventHandler
  public void onEat(PlayerItemConsumeEvent event) {
    Player player = event.getPlayer();
    ItemStack food = event.getItem();
    if (food.getType() == Material.GLOW_BERRIES) {
      unlock(player);
    }
  }
}
