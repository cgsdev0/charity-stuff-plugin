package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;

@DonationEffect.Meta(key="onehp", name="1 HP and a Dream", tier=Tier.TIER_1)
public class OneHealthEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();

    onlinePlayers.forEach(player -> player.setHealth(2.0));
  }
}
