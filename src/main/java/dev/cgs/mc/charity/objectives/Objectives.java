package dev.cgs.mc.charity.objectives;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Objectives implements Listener {
  private static Objectives instance;

  private class AugmentedObjective {
    public Objective objective;
    public Objective.Meta meta;

    public AugmentedObjective(Objective objective, Objective.Meta meta) {
      this.objective = objective;
      this.meta = meta;
    }
  }

  private HashMap<String, AugmentedObjective> objectives;
  private HashMap<String, AugmentedObjective> advancementObjectives;

  private Objectives() {
    objectives = new HashMap<>();
    advancementObjectives = new HashMap<>();
  }

  public void registerObjectives(Objective... objectives) {
    for (Objective objective : objectives) {
      CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
      if (objective instanceof Listener) {
        Listener listener = (Listener) objective;
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
      }
      Objective.Meta meta = objective.getClass().getAnnotation(Objective.Meta.class);
      if (meta == null) {
        Bukkit.getLogger().severe("Missing metadata for class: " + objective.getClass().getName());
        throw new RuntimeException(
            "Failed to start the plugin. Please annotate all objective classes.");
      }
      var augmented = new AugmentedObjective(objective, meta);
      this.objectives.put(meta.key(), augmented);
      if (meta.advancement() != "") {
        this.advancementObjectives.put(meta.advancement(), augmented);
      }
    }
  }

  @EventHandler
  public void onAdvancement(PlayerAdvancementDoneEvent event) {
    Player player = event.getPlayer();
    String key = event.getAdvancement().getKey().toString();
    AugmentedObjective objective = advancementObjectives.get(key);
    if (objective == null)
      return;

    String objKey = objective.meta.key();

    Team t = Teams.get().fromPlayer(player);
    if (t == null)
      return;
    t.unlock(objKey, player);
  }

  public Set<String> getKeys() {
    return this.objectives.keySet();
  }

  public Objective.Meta getMeta(String key) {
    return this.objectives.get(key).meta;
  }

  public static void onEnable() {
    if (instance != null) {
      throw new IllegalStateException("Objectives is already initialized.");
    }
    instance = new Objectives();
    CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
    plugin.getServer().getPluginManager().registerEvents(instance, plugin);
  }

  public static Objectives get() {
    if (instance == null) {
      throw new IllegalStateException("Objectives not initialized yet.");
    }
    return instance;
  }

  public static void onDisable() {
    instance = null;
  }
}
