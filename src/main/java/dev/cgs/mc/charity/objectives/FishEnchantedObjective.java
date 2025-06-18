package dev.cgs.mc.charity.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

@Objective.Meta(key = "fish_enchanted", name = "Secrets of the Sea",
    kind = Objective.Kind.PER_PLAYER, worth = 5)
public class FishEnchantedObjective extends Objective implements Listener {
  @EventHandler
  public void onFish(PlayerFishEvent event) {
    Player player = event.getPlayer();
    Entity what = event.getCaught();
    if (what == null)
      return;
    if (what instanceof Item item) {
      var stack = item.getItemStack();
      if (stack.getType() == Material.ENCHANTED_BOOK) {
        unlock(player);
      }
    }
  }
}
