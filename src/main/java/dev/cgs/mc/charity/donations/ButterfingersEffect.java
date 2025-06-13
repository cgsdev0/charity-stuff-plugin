package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import static dev.cgs.mc.charity.donations.HotPotatoEffect.potatoKey;

@DonationEffect.Meta(
  key="butterfingers",
  name="Slippery Fingers",
  tier=Tier.TIER_1
)
public class ButterfingersEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();

    onlinePlayers.forEach(player -> {
      ItemStack currentItem = player.getInventory().getItemInMainHand();

      if(currentItem != null && !currentItem.isEmpty() && !isPotato(currentItem)) {
        Item entity = player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), currentItem);

        entity.setPickupDelay(40);
        entity.setThrower(player.getUniqueId());
        entity.setVelocity(itemVelocity(player));

        player.getInventory().setItemInMainHand(ItemStack.empty());
      }
    });
  }

  private boolean isPotato(ItemStack stack) {
    if (stack == null)
      return false;
    if (stack.getPersistentDataContainer().getOrDefault(
      potatoKey, PersistentDataType.BOOLEAN, false)) {
      return true;
    }
    return false;
  }

  private Vector itemVelocity(Player player) {
    return new Vector(0, 0, 0.5).rotateAroundY(-player.getYaw() * Math.PI / 180.0);
  }
}

//new CommandAPICommand("butterfingers")
//        .withPermission(CommandPermission.OP) // Required permissions
//        .executes((sender, args) -> {
//getServer().getOnlinePlayers().forEach(player -> {
//ItemStack item = player.getInventory().getItemInMainHand();
//            if (item != null && !item.isEmpty() && item.getType() != Material.POTATO) {
//Item e =
//  player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), item);
//              e.setPickupDelay(40);
//              e.setThrower(player.getUniqueId());
//  // e.setOwner(player.getUniqueId());
//  e.setVelocity(
//                  (new Vector(0, 0, 0.5)).rotateAroundY(-player.getYaw() * 3.1415926535 / 180.0));
//  player.getInventory().setItemInMainHand(ItemStack.empty());
//  }
//  });
//  })
//  .register();
