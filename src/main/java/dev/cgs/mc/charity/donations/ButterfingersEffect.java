package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

@DonationEffect.Meta(key = "butterfingers", name = "Slippery Fingers", tier = Tier.TIER_1)
public class ButterfingersEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    Teams.get().getOnlinePlayers().forEach(player -> {
      ItemStack currentItem = player.getInventory().getItemInMainHand();

      if (currentItem != null && !currentItem.isEmpty() && !isPotato(currentItem)) {
        Item entity =
            player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), currentItem);

        entity.setPickupDelay(40);
        entity.setThrower(player.getUniqueId());
        entity.setVelocity(itemVelocity(player));

        player.getInventory().setItemInMainHand(ItemStack.empty());
        player.playSound(
            Sound.sound(Key.key("entity.breeze.wind_burst"), Sound.Source.MASTER, 1.0f, 1.5f),
            Sound.Emitter.self());
      }
    });
  }

  private boolean isPotato(ItemStack stack) {
    if (stack == null)
      return false;
    if (stack.getPersistentDataContainer().getOrDefault(
            CharityMain.potatoKey, PersistentDataType.BOOLEAN, false)) {
      return true;
    }
    return false;
  }

  private Vector itemVelocity(Player player) {
    return new Vector(0, 0, 0.5).rotateAroundY(-player.getYaw() * Math.PI / 180.0);
  }
}
