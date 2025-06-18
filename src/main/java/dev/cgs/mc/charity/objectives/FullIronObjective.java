package dev.cgs.mc.charity.objectives;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@Objective.Meta(key = "full_iron", name = "Full Iron", kind = Objective.Kind.PER_PLAYER, worth = 5)
public class FullIronObjective extends Objective implements Listener {
  @EventHandler
  public void onEquip(EntityEquipmentChangedEvent event) {
    if (event.getEntity() instanceof Player player) {
      ItemStack[] items = player.getInventory().getArmorContents();
      for (var item : items) {
        if (item == null)
          return;
        var m = item.getType();
        if (m == Material.IRON_BOOTS || m == Material.IRON_LEGGINGS || m == Material.IRON_CHESTPLATE
            || m == Material.IRON_HELMET)
          continue;
        return;
      }
      unlock(player);
    }
  }
}
