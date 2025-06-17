package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@DonationEffect.Meta(key="lookout", name="Hey, Look Out Behind You!", tier=Tier.TIER_1)
public class LookoutEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();

    onlinePlayers.forEach(player -> {
      Location playerLoc = player.getLocation();
      Vector direction = playerLoc.getDirection();
      direction.setY(0).normalize();
      Location behindYou = playerLoc.clone().subtract(direction);

      player.playSound(
        Sound.sound(Key.key("entity.creeper.primed"), Sound.Source.MASTER, 1f, 0.5f),
        behindYou.x(), behindYou.y(), behindYou.z()
      );
    });
  }
}
