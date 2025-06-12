package dev.cgs.mc.charity.objectives;

@Objective.Meta(
  key = "caves_and_cliffs",
  name = "Caves & Cliffs",
  kind = Objective.Kind.PER_TEAM,
  worth = 30,
  advancement = "minecraft:adventure/fall_from_world_height"
)
public class CavesAndCliffsObjective extends Objective {}
