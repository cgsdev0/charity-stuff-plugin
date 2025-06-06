package dev.cgs.mc.charity.donations;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import dev.cgs.mc.charity.Team;
import dev.cgs.mc.charity.donations.DonationEffect.Target;
import dev.cgs.mc.charity.donations.DonationEffect.Kind;

@DonationEffect.Meta(
  name="Example",
  targets=Target.PLAYER,
  kind=Kind.NEGATIVE
)
public class ExampleEffect extends DonationEffect implements Listener {
  @Override
  public void start(Team team, List<Player> affected) {
    // TODO
  }
}
