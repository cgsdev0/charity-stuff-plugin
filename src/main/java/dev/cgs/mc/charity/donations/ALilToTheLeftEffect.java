package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;

@DonationEffect.Meta(key = "left", name = "A Lil to the Left", tier = Tier.TIER_1)
public class ALilToTheLeftEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    Teams.get().getOnlinePlayers().forEach(player -> {
      Location currentLoc = player.getLocation();
      float yaw = currentLoc.getYaw();
      double left = Math.toRadians(yaw - 90);
      int blocksToMove = ThreadLocalRandom.current().nextInt(1, 6);
      double x = -Math.sin(left) * blocksToMove;
      double z = Math.cos(left) * blocksToMove;
      Location newLoc = currentLoc.clone().add(x, 0, z);
      player.teleport(newLoc);
      player.playSound(
          Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f),
          Sound.Emitter.self());
    });
  }
}
