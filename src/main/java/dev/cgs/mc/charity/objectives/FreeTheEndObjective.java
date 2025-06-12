package dev.cgs.mc.charity.objectives;

@Objective.Meta(
  key = "free_the_end",
  name = "Free The End",
  kind = Objective.Kind.PER_TEAM,
  worth = 100,
  advancement = "minecraft:end/kill_dragon"
)
public class FreeTheEndObjective implements Objective {}
