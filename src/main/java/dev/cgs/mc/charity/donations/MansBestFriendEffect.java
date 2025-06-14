package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@DonationEffect.Meta(key="doggo", name="Man's Best Friend", tier=Tier.TIER_1)
public class MansBestFriendEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();
    ItemStack bones = ItemStack.of(Material.BONE, 5);

    onlinePlayers.forEach(player -> {
      Location playerLoc = player.getLocation();
      HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(bones);

      player.getWorld().spawnEntity(playerLoc, EntityType.WOLF);

      if(!leftover.isEmpty()) {
        for(ItemStack item : leftover.values()) {
          player.getWorld().dropItemNaturally(playerLoc, item);
        }
      }
    });
  }
}
