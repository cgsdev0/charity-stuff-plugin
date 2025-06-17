package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.teams.Teams;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Donations {
  private static Donations instance;

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

  private Donations() {
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
    CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);

    if (effect.locked) {
      throw new Error("That effect is locked!");
    }
    effect.effect.start(plugin);
  }

  /** Locks a player for this effect type. They won't receive it again until unlocked **/
  public final void lock(DonationEffect effect) {
    DonationEffect.Meta meta = effect.getClass().getAnnotation(DonationEffect.Meta.class);
    String mutex = meta.mutex();
    String key = meta.key();
    effects.get(key).locked = true;
    // lock any effects that share our mutex
    if (!mutex.isEmpty()) {
      for (var e : effects.values()) {
        if (e.meta.mutex().equals(mutex)) {
          e.locked = true;
        }
      }
    }
  }

  /** Unlocks a player for this effect type **/
  public final void unlock(DonationEffect effect) {
    DonationEffect.Meta meta = effect.getClass().getAnnotation(DonationEffect.Meta.class);
    String mutex = meta.mutex();
    String key = meta.key();
    effects.get(key).locked = false;
    // unlock any effects that share our mutex
    if (!mutex.isEmpty()) {
      for (var e : effects.values()) {
        if (e.meta.mutex().equals(mutex)) {
          e.locked = false;
        }
      }
    }
  }

  public static void onEnable() {
    if (instance != null) {
      throw new IllegalStateException("Donations is already initialized.");
    }
    instance = new Donations();
  }

  public static Donations get() {
    if (instance == null) {
      throw new IllegalStateException("Donations not initialized yet.");
    }
    return instance;
  }

  public static void onDisable() {
    instance = null;
  }
}
