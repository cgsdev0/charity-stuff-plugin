package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@DonationEffect.Meta(key = "rotate", name = "Shuffle", tier = Tier.TIER_1)
public class RotateEffect extends DonationEffect {
  @Override
  public void start() {
    CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
    var players = plugin.getServer().getOnlinePlayers();
    Player p = null;
    Location first = null;
    for (Player player : players) {
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
  }
}
