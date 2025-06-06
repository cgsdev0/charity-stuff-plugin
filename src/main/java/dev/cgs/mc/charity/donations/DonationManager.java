package dev.cgs.mc.charity.donations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.Team;
import io.netty.handler.logging.LogLevel;

public class DonationManager {

    private static DonationManager instance;

    private class AugmentedEffect {
      public DonationEffect effect;
      public DonationEffect.Meta meta;

      public AugmentedEffect(DonationEffect effect, DonationEffect.Meta meta) {
        this.effect = effect;
        this.meta = meta;
      }
    }
    private HashMap<String, AugmentedEffect> effects;

    private DonationManager() {
      effects = new HashMap<>();
    }


    public void registerEffects(DonationEffect... effects) {
      for(DonationEffect effect : effects) {
        CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
        if (effect instanceof Listener) {
          Listener listener = (Listener)effect;
          plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
        DonationEffect.Meta meta = effect.getClass().getAnnotation(DonationEffect.Meta.class);
        if (meta == null) {
          Bukkit.getLogger().severe("Missing metadata for class: " + effect.getClass().getName());
          throw new RuntimeException("Failed to start the plugin. Please annotate all donation effect classes.");
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

    /** Locks a player for this effect type. They won't receive it again until unlocked **/
    public final void lock(DonationEffect effect, Player player) {
      // TODO
    }

    /** Unlocks a player for this effect type **/
    public final void unlock(DonationEffect effect, Player player) {
      // TODO
    }

    /** Locks a team for this effect type. They won't receive it again until unlocked **/
    public final void lock(DonationEffect effect, Team team) {
      // TODO
    }

    /** Unlocks a team for this effect type **/
    public final void unlock(DonationEffect effect, Team team) {
      // TODO
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
