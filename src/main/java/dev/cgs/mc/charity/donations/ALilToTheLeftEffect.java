package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import org.bukkit.Location;

import java.util.concurrent.ThreadLocalRandom;

@DonationEffect.Meta(key="left", name="A Lil to the Left", tier= Tier.TIER_1)
public class ALilToTheLeftEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();

    onlinePlayers.forEach(player -> {
      Location currentLoc = player.getLocation();
      float yaw = currentLoc.getYaw();
      double left = Math.toRadians(yaw - 90);
      int blocksToMove = ThreadLocalRandom.current().nextInt(1, 6);
      double x = -Math.sin(left) * blocksToMove;
      double z = Math.cos(left) * blocksToMove;
      Location newLoc = currentLoc.clone().add(x, 0 , z);

      player.teleport(newLoc);
    });
  }
}
