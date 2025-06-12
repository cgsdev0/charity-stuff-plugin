package dev.cgs.mc.charity.objectives;

@Objective.Meta(
  key = "local_brewery",
  name = "Local Brewery",
  kind = Objective.Kind.PER_TEAM,
  worth = 20,
  advancement = "minecraft:nether/brew_potion"
)
public class LocalBreweryObjective implements Objective {}
