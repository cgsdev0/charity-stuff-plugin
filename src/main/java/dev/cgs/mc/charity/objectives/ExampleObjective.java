package dev.cgs.mc.charity.objectives;

import dev.cgs.mc.charity.donations.*;
import dev.cgs.mc.charity.teams.*;
import org.bukkit.event.Listener;

@Objective.Meta(key = "example", name = "Example", kind = Objective.Kind.PER_TEAM, worth = 5)
public class ExampleObjective implements Objective, Listener {}
