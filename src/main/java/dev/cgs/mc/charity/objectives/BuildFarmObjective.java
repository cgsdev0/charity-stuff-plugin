package dev.cgs.mc.charity.objectives;

@Objective.Meta(key = "build_farm", name = "Build a Farm", kind = Objective.Kind.PER_TEAM,
    worth = 10, desc = "Build a farm with at least 3 types of animal")
public class BuildFarmObjective extends Objective {}
