package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@DonationEffect.Meta(key = "rotate", name = "Shuffle", tier = Tier.TIER_1)
public class RotateEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    Player p = null;
    Location first = null;
    for (Player player : Teams.get().getOnlinePlayers()) {
      Location next = player.getLocation();
      if (p != null) {
        p.teleport(next);
      } else {
        first = next;
      }
      p = player;
    }
    if (p != null) {
      p.teleport(first);
    }
    p.playSound(Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f),
        Sound.Emitter.self());
  }
}
