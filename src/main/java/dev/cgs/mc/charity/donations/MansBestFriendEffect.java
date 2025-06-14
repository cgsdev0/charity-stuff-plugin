package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@DonationEffect.Meta(key="doggo", name="Man's Best Friend", tier=Tier.TIER_1)
public class MansBestFriendEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();
    ItemStack bones = ItemStack.of(Material.BONE, 5);

    onlinePlayers.forEach(player -> {
      Location spawnLoc = player.getLocation().add(player.getLocation().getDirection().multiply(1.5).setY(0));

      player.getWorld().spawnEntity(spawnLoc, EntityType.WOLF);
      player.give(List.of(bones), true);
    });
  }
}
