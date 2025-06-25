package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@DonationEffect.Meta(key = "doggo", name = "Man's Best Friend", tier = Tier.TIER_1)
public class MansBestFriendEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    ItemStack bones = ItemStack.of(Material.BONE, 5);

    Teams.get().getOnlinePlayers().forEach(player -> {
      Location spawnLoc =
          player.getLocation().add(player.getLocation().getDirection().multiply(1.5).setY(0));

      player.getWorld().spawnEntity(spawnLoc, EntityType.WOLF);
      player.give(List.of(bones), true);
      player.playSound(Sound.sound(Key.key("entity.wolf.whine"), Sound.Source.MASTER, 1.0f, 1.0f),
          Sound.Emitter.self());
    });
  }
}
