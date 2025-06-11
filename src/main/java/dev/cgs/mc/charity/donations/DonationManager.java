package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.teams.Team;
import io.netty.handler.logging.LogLevel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DonationManager {
  private static DonationManager instance;

  private class AugmentedEffect {
    public DonationEffect effect;
    public DonationEffect.Meta meta;
    public boolean locked;

    public AugmentedEffect(DonationEffect effect, DonationEffect.Meta meta) {
      this.effect = effect;
      this.meta = meta;
      this.locked = false;
    }
  }

  private HashMap<String, AugmentedEffect> effects;

  private DonationManager() {
    effects = new HashMap<>();
  }

  public void registerEffects(DonationEffect... effects) {
    for (DonationEffect effect : effects) {
      CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
      if (effect instanceof Listener) {
        Listener listener = (Listener) effect;
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
      }
      DonationEffect.Meta meta = effect.getClass().getAnnotation(DonationEffect.Meta.class);
      if (meta == null) {
        Bukkit.getLogger().severe("Missing metadata for class: " + effect.getClass().getName());
        throw new RuntimeException(
            "Failed to start the plugin. Please annotate all donation effect classes.");
      }
      this.effects.put(meta.key(), new AugmentedEffect(effect, meta));
    }
  }

  public Set<String> getKeys() {
    return this.effects.keySet();
  }

  public DonationEffect.Meta getMeta(String key) {
    return this.effects.get(key).meta;
  }

  public void start(String key) {
    AugmentedEffect effect = effects.get(key);
    if (effect.locked) {
      throw new Error("That effect is locked!");
    }
    effect.effect.start();
  }

  /** Locks a player for this effect type. They won't receive it again until unlocked **/
  public final void lock(DonationEffect effect) {
    DonationEffect.Meta meta = effect.getClass().getAnnotation(DonationEffect.Meta.class);
    String key = meta.key();
    effects.get(key).locked = true;
  }

  /** Unlocks a player for this effect type **/
  public final void unlock(DonationEffect effect) {
    DonationEffect.Meta meta = effect.getClass().getAnnotation(DonationEffect.Meta.class);
    String key = meta.key();
    effects.get(key).locked = false;
  }

  public static void onEnable() {
    if (instance != null) {
      throw new IllegalStateException("DonationManager is already initialized.");
    }
    instance = new DonationManager();
  }

  public static DonationManager get() {
    if (instance == null) {
      throw new IllegalStateException("DonationManager not initialized yet.");
    }
    return instance;
  }

  public static void onDisable() {
    instance = null;
  }
}
