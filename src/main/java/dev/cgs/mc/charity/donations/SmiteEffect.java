package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import org.bukkit.Location;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;

@DonationEffect.Meta(key="smite", name="Zeus is Not Happy", tier=Tier.TIER_2)
public class SmiteEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();

    onlinePlayers.forEach(player -> {
      Location playerLoc = player.getLocation();

      player.getWorld().strikeLightning(playerLoc);
    });
  }
}
