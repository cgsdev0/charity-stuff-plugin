package dev.cgs.mc.charity.donations;

import org.bukkit.event.Listener;

import dev.cgs.mc.charity.donations.DonationEffect.Target;
import dev.cgs.mc.charity.donations.DonationEffect.Kind;

@DonationEffect.Meta(
  key="example",
  name="Example",
  targets=Target.TEAM,
  kind=Kind.NEGATIVE
)
public class ExampleEffect extends DonationEffect implements Listener {
  @Override
  public void start() {
    // TODO
  }
}
