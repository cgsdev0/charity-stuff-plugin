package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.teams.Teams;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Donations {
  private static Donations instance;
  private Queue<DonationEffect.Tier> redeemQueue = new ArrayDeque<>();
  private boolean loopActive = false;

  private Map<DonationEffect.Tier, Shuffler> shufflers = new HashMap<>();

  private class Shuffler {
    private List<AugmentedEffect> used = new ArrayList<>();
    private List<AugmentedEffect> next;

    public Shuffler(List<AugmentedEffect> initial) {
      this.next = new ArrayList<>(initial);
      Collections.shuffle(this.next);
    }
    private void reshuffle() {
      this.next = used;
      Collections.shuffle(this.next);
      this.used = new ArrayList<>();
    }

    private AugmentedEffect pop(int depth) {
      if (next.isEmpty()) {
        reshuffle();
      }
      AugmentedEffect result = null;
      while (!next.isEmpty() && (result == null || result.locked)) {
        if (result != null) {
          Bukkit.getLogger().warning("Skipping " + result.meta.key() + " because of lock...");
        }
        result = next.remove(0);
        used.add(result);
      }
      if (depth == 1 && (result == null || result.locked))
        return pop(0);
      if (result != null && result.locked)
        return null;
      return result;
    }

    public AugmentedEffect pop() {
      return pop(1);
    }
  }

  public void queue(DonationEffect.Tier tier) {
    redeemQueue.add(tier);
    if (!loopActive) {
      runLoop();
    }
  }

  private void runLoop() {
    if (redeemQueue.isEmpty()) {
      loopActive = false;
      return;
    }
    var first = redeemQueue.remove();

    CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
    var shuffler = shufflers.get(first);
    AugmentedEffect selected = shuffler.pop();
    if (selected != null) {
      new BukkitRunnable() {
        private int tick = 0;
        @Override
        public void run() {
          if (tick >= 3) {
            start(selected.meta.key());
            if (!selected.meta.no_title()) {
              Teams.get().showTitle(
                  Title.title(Component.text(selected.meta.name()), Component.text("")));
            }
            this.cancel();
          } else if (!selected.meta.no_warning()) {
            Teams.get().playSound(
                Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 1.0f),
                Sound.Emitter.self());
          }
          tick++;
        }
      }.runTaskTimer(plugin, 0, 20);
    } else {
      Bukkit.getLogger().warning("We potentially lost a donation effect due to locks!");
      // best effort to make it happen: let's re-queue in a bit
      Bukkit.getScheduler().runTaskLater(plugin, task -> { queue(first); }, 60 * 20);
    }
    Bukkit.getScheduler().runTaskLater(plugin, task -> { runLoop(); }, 10 * 20);
  }

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
    initShufflers();
  }

  private void initShufflers() {
    for (var key : DonationEffect.Tier.values()) {
      shufflers.put(key,
          new Shuffler(this.effects.values()
                  .stream()
                  .filter(effect -> { return effect.meta.tier() == key; })
                  .toList()));
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
