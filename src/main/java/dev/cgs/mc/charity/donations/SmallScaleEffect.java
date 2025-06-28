package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.VoicePlugin;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
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

@DonationEffect.Meta(key = "shrink", name = "Fun Sized", tier = Tier.TIER_2, mutex = "scale")
public class SmallScaleEffect extends DonationEffect implements Listener {
  public boolean active = false;
  public static final double minSize = 0.4;

  public void updatePlayerAttrs(Player player, double growth) {
    double pitch = 1.0 - (growth - 1.0) / minSize * 0.35;
    VoicePlugin.setPitchScale(player.getUniqueId(), pitch);
    var a = (Attributable) player;
    a.getAttribute(Attribute.SCALE).setBaseValue(growth);
  }

  @Override
  public void start(CharityMain plugin) {
    lock();
    active = true;
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      this.active = false;
      unlock();
      var onlinePlayers = plugin.getServer().getOnlinePlayers();
      onlinePlayers.forEach(player -> {
        player.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 0.6f),
            Sound.Emitter.self());
        updatePlayerAttrs(player, 1.0);
      });
    }, 20 * 60 * 5); // 5 minutes
    var run = new BukkitRunnable() {
      double amt = 1.0;
      @Override
      public void run() {
        amt -= 0.005;
        if (amt < minSize) {
          this.cancel();
          return;
        }
        var onlinePlayers = Teams.get().getOnlinePlayers();
        onlinePlayers.forEach(player -> { updatePlayerAttrs(player, amt); });
      }
    };
    run.runTaskTimer(plugin, 0L, 3L);
  }

  @EventHandler
  public void onLogin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (active) {
      updatePlayerAttrs(player, minSize);
    }
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    if (active) {
      updatePlayerAttrs(player, 1.0);
    }
  }
}
