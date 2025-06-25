package dev.cgs.mc.charity.objectives;

@Objective.Meta(
  key = "new_look",
  name = "Crafting a New Look",
  kind = Objective.Kind.PER_TEAM,
  worth = 15,
  advancement = "minecraft:adventure/trim_with_any_armor_pattern"
)
public class CraftingNewLookObjective extends Objective {}
