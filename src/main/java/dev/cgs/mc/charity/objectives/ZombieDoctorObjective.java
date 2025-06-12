package dev.cgs.mc.charity.objectives;

@Objective.Meta(
  key = "zombie_doctor",
  name = "Zombie Doctor",
  kind = Objective.Kind.PER_TEAM,
  worth = 25,
  advancement = "minecraft:story/cure_zombie_villager"
)
public class ZombieDoctorObjective implements Objective {}
