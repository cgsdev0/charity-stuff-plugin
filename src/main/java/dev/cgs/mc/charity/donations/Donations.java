package dev.cgs.mc.charity.donations;

import com.google.gson.Gson;
import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.teams.Teams;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Donations {
  private static Donations instance;
  private Queue<TiltifyEvent> redeemQueue = new ArrayDeque<>();
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

  Thread eventReader;

  public void queue(TiltifyEvent event) {
    redeemQueue.add(event);
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
    var shuffler = shufflers.get(first.tier);
    AugmentedEffect selected = shuffler.pop();
    if (selected != null) {
      new BukkitRunnable() {
        private int tick = 0;
        @Override
        public void run() {
          if (tick >= 3) {
            this.cancel();
            start(selected.meta.key());
            if (!selected.meta.no_title()) {
              Teams.get().showTitle(Title.title(
                  Component.text(selected.meta.name())
                      .color(first.tier == DonationEffect.Tier.TIER_1    ? NamedTextColor.AQUA
                              : first.tier == DonationEffect.Tier.TIER_2 ? NamedTextColor.GREEN
                                                                         : NamedTextColor.RED),
                  first.toComponent()));
            }
          } else if (!selected.meta.no_warning()) {
            float pitch = 1.0f;
            if (first.tier == DonationEffect.Tier.TIER_2) {
              pitch = 0.75f;
            }
            if (first.tier == DonationEffect.Tier.TIER_3) {
              pitch = 0.5f;
            }
            Teams.get().playSound(
                Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, pitch),
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
  public TiltifyEvent fakeEvent(DonationEffect.Tier tier) {
    var event = new TiltifyEvent();
    event.tier = tier;
    event.synthetic = true;
    return event;
  }

  public class TiltifyEvent {
    public TextComponent toComponent() {
      if (synthetic)
        return Component.text("");
      return Component.text(this.data.amount.toString())
          .color(NamedTextColor.GOLD)
          .append(Component.text(" donated by ").color(NamedTextColor.WHITE))
          .append(Component.text(this.data.donor_name).color(NamedTextColor.LIGHT_PURPLE))
          .append(Component.text("!").color(NamedTextColor.WHITE));
    }

    public static final String tier1 = "569de385-9825-47c8-910f-19dfd2cee6e6";
    public static final String tier2 = "2c89bd2f-b272-4637-a373-8b8e80bc0b4f";
    public static final String tier3 = "5da39e18-7005-493d-8bba-6032a1944615";

    public DonationEffect.Tier tier;
    public boolean synthetic = false;

    public void calculateTier() {
      tier = DonationEffect.Tier.TIER_1;
      if ("USD".equals(data.amount.currency)) {
        double amt = Double.parseDouble(data.amount.value);
        if (amt >= 10.0) {
          tier = DonationEffect.Tier.TIER_2;
        }
        if (amt >= 100.0) {
          tier = DonationEffect.Tier.TIER_3;
        }
      } else {
        if (tier2.equals(data.reward_id)) {
          tier = DonationEffect.Tier.TIER_2;
        }
        if (tier3.equals(data.reward_id)) {
          tier = DonationEffect.Tier.TIER_3;
        }
      }
    }

    public class EventData {
      public class Amount {
        public String currency;
        public String value;
        @Override
        public String toString() {
          return value + " " + currency;
        }
      }
      public Amount amount;
      public String campaign_id;
      public String cause_id;
      public Date completed_at;
      public Date created_at;
      public String donor_comment;
      public String donor_name;
      public String id;
      public String reward_id;
    }
    public EventData data;
    public class Meta {
      public String id;
      public String event_type;
      public Date attempted_at;
      public Date generated_at;
      public String subscription_source_id;
      public String subscription_source_type;
    }
    public Meta meta;
  }
  public void readEvents() {
    String pipePath = "/tmp/tiltify";
    try (BufferedReader reader =
             new BufferedReader(new InputStreamReader(new FileInputStream(pipePath)))) {
      String line;
      while (get() != null) {
        line = reader.readLine(); // blocks until input arrives
        if (line == null) {
          continue;
        }
        System.out.println("Received: " + line);
        TiltifyEvent event = new Gson().fromJson(line, TiltifyEvent.class);
        event.calculateTier();
        CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
        Bukkit.getScheduler().runTask(plugin, task -> { this.queue(event); });
        System.out.println("Parsed: " + new Gson().toJson(event));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void onEnable() {
    if (instance != null) {
      throw new IllegalStateException("Donations is already initialized.");
    }
    instance = new Donations();
    instance.eventReader = new Thread(instance::readEvents);
    instance.eventReader.start();
  }

  public static Donations get() {
    return instance;
  }

  public static void onDisable() {
    instance = null;
  }
}
