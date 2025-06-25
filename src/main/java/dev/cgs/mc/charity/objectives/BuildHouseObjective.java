package dev.cgs.mc.charity.objectives;

@Objective.Meta(key = "build_house", name = "Build a House", kind = Objective.Kind.PER_PLAYER,
    worth = 10, desc = "At least a bed, chest, and furnace")
public class BuildHouseObjective extends Objective {}
