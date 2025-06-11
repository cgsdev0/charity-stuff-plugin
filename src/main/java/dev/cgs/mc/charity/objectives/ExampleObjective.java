package dev.cgs.mc.charity.objectives;

import org.bukkit.event.Listener;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;

@Objective.Meta(
  key="example",
  name="Example",
  kind=Objective.Kind.PER_TEAM,
  worth=5
)
public class ExampleObjective implements Objective, Listener {
}
