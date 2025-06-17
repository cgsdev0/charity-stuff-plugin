package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.VoicePlugin;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

@DonationEffect.
Meta(key = "grow", name = "They Grow Up So Fast", tier = Tier.TIER_3, mutex = "scale")
public class BigScaleEffect extends DonationEffect implements Listener {
  public boolean active = false;
  public static final double maxSize = 4.0;

  public void updatePlayerAttrs(Player player, double growth) {
    double pitch = 1.0 - growth * 0.4;
    double stepModifier = 0.6 * (1.0 + growth);
    double rangeModifier = 4.5 * (1.0 + growth);
    double jumpModifier = 0.42 * (1.0 + growth);
    double safeFallModifier = 3.0 * (1.0 + growth);
    VoicePlugin.setPitchScale(player.getUniqueId(), pitch);
    var a = (Attributable) player;
    a.getAttribute(Attribute.SCALE).setBaseValue(growth * maxSize + 1.0);
    a.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(stepModifier);
    a.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(rangeModifier);
    a.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(jumpModifier);
    a.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(safeFallModifier);
  }
  @Override
  public void start(CharityMain plugin) {
    lock();
    active = true;
    var run = new BukkitRunnable() {
      double amt = 1.0;
      @Override
      public void run() {
        amt += 0.02;
        if (amt > maxSize) {
          this.cancel();
          return;
        }
        double growth = (amt - 1.0) / maxSize;
        var onlinePlayers = plugin.getServer().getOnlinePlayers();
        onlinePlayers.forEach(player -> { updatePlayerAttrs(player, growth); });
      }
    };
    run.runTaskTimer(plugin, 0L, 3L);
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      this.active = false;
      unlock();
      var onlinePlayers = plugin.getServer().getOnlinePlayers();
      onlinePlayers.forEach(player -> {
        player.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 0.6f),
            Sound.Emitter.self());
        updatePlayerAttrs(player, 0.0);
      });
    }, 20 * 60 * 10); // 10 minutes
  }

  @EventHandler
  public void onLogin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (active) {
      updatePlayerAttrs(player, maxSize);
    }
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    if (active) {
      updatePlayerAttrs(player, 0.0);
    }
  }
}
