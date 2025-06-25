package dev.cgs.mc.charity.objectives;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

@Objective.Meta(key = MaxEnchantObjective.key, name = "Lvl 30 Enchant",
    kind = Objective.Kind.PER_TEAM, worth = 40, desc = "Enchant with level 30")
public class MaxEnchantObjective extends Objective implements Listener {
  public static final String key = "max_enchant";

  @EventHandler
  public void onEnchant(EnchantItemEvent event) {
    Player p = event.getEnchanter();
    if (event.getExpLevelCost() < 30)
      return;
    unlock(p);
  }
}
