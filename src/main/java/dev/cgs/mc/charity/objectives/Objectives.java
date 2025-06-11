package dev.cgs.mc.charity.objectives;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cgs.mc.charity.CharityMain;

public class Objectives {

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

    private Objectives() {
      objectives = new HashMap<>();
    }


    public void registerObjectives(Objective... objectives) {
      for(Objective objective : objectives) {
        CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
        if (objective instanceof Listener) {
          Listener listener = (Listener)objective;
          plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
        Objective.Meta meta = objective.getClass().getAnnotation(Objective.Meta.class);
        if (meta == null) {
          Bukkit.getLogger().severe("Missing metadata for class: " + objective.getClass().getName());
          throw new RuntimeException("Failed to start the plugin. Please annotate all objective classes.");
        }
        this.objectives.put(meta.key(), new AugmentedObjective(objective, meta));
      }
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
