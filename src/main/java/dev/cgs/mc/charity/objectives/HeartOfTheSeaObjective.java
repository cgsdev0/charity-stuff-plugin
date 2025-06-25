package dev.cgs.mc.charity.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Objective.Meta(key = "heart_of_the_sea", name = "Treasure Hunter", kind = Objective.Kind.PER_TEAM,
    worth = 20, desc = "Obtain a Heart of the Sea")
public class HeartOfTheSeaObjective extends Objective implements Listener {
  @EventHandler
  public void onPickup(EntityPickupItemEvent event) {
    if (event.getItem().getItemStack().getType() != Material.HEART_OF_THE_SEA)
      return;
    if (event.getEntity() instanceof Player player) {
      unlock(player);
    }
  }

  @EventHandler
  public void onPickup(InventoryClickEvent event) {
    ItemStack a = event.getCurrentItem();
    if (a == null || a.getType() != Material.HEART_OF_THE_SEA)
      return;
    if (event.getWhoClicked() instanceof Player player) {
      unlock(player);
    }
  }
}
