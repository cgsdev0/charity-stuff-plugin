package dev.cgs.mc.charity.donations;

import org.bukkit.event.Listener;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;

@DonationEffect.Meta(
  key="example",
  name="Example",
  tier=Tier.TIER_1
)
public class ExampleEffect extends DonationEffect implements Listener {
  @Override
  public void start() {
    // TODO
  }
}
