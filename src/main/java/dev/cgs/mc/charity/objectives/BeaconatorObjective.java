package dev.cgs.mc.charity.objectives;

@Objective.Meta(
  key = "beaconator",
  name = "Beaconator",
  kind = Objective.Kind.PER_TEAM,
  worth = 100,
  advancement = "minecraft:nether/create_full_beacon"
)
public class BeaconatorObjective extends Objective {}
