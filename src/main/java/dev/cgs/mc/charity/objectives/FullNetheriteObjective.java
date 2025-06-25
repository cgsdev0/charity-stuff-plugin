package dev.cgs.mc.charity.objectives;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import dev.cgs.mc.charity.CharityMain;

@Objective.Meta(key = "full_netherite", name = "Full Netherite", kind = Objective.Kind.PER_PLAYER, worth = 100,
    desc = "Equip full netherite armor")
public class FullNetheriteObjective extends Objective implements Listener {
  @EventHandler
  public void onEquip(EntityEquipmentChangedEvent event) {
    if (event.getEntity() instanceof Player player) {
      ItemStack[] items = player.getInventory().getArmorContents();
      for (var item : items) {
        if (item == null)
          return;
        var m = item.getType();
        if (item.getPersistentDataContainer().getOrDefault(CharityMain.armorKey, PersistentDataType.BOOLEAN, false)) {
          return;
        }
        if (m == Material.NETHERITE_BOOTS || m == Material.NETHERITE_LEGGINGS || m == Material.NETHERITE_CHESTPLATE
            || m == Material.NETHERITE_HELMET)
          continue;
        return;
      }
      if (unlock(player)) {
        // tag all the armor
        for (var item : items) {
          item.editPersistentDataContainer(pdc -> {
            pdc.set(CharityMain.armorKey, PersistentDataType.BOOLEAN, true);
          });
          item.editMeta(meta -> {
            var lore = new ArrayList<TextComponent>();
            lore.add(Component.text("Already used to redeem an objective.").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED));
            meta.lore(lore);
          });
        }
      }
    }
  }
}
