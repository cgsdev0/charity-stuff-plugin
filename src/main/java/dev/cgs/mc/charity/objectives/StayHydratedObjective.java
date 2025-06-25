package dev.cgs.mc.charity.objectives;

@Objective.Meta(
  key = "stay_hydrated",
  name = "Stay Hydrated",
  kind = Objective.Kind.PER_TEAM,
  worth = 15,
  advancement = "minecraft:husbandry/place_dried_ghast_in_water"
)
public class StayHydratedObjective extends Objective {}
