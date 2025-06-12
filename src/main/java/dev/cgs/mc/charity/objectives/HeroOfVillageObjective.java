package dev.cgs.mc.charity.objectives;

@Objective.Meta(
  key = "hero_of_the_village",
  name = "Hero of the Village",
  kind = Objective.Kind.PER_TEAM,
  worth = 30,
  advancement = "minecraft:adventure/hero_of_the_village"
)
public class HeroOfVillageObjective implements Objective {}
