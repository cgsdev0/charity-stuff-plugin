package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@DonationEffect.Meta(key = "sweettooth", name = "Sweet Tooth", tier = Tier.TIER_1)
public class SweetToothEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();
    ItemStack[] desserts = {
      ItemStack.of(Material.CAKE, 1),
      ItemStack.of(Material.COOKIE, 16),
      ItemStack.of(Material.PUMPKIN_PIE, 16)
    };

    onlinePlayers.forEach(player -> {
      HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(desserts);

      if(!leftover.isEmpty()) {
        for(ItemStack item : leftover.values()) {
          player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
      }
    });
  }
}
