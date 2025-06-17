package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
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
import org.bukkit.scheduler.BukkitRunnable;

@DonationEffect.Meta(key = "onehp", name = "1 HP and a Dream", tier = Tier.TIER_3)
public class OneHealthEffect extends DonationEffect implements Listener {
  public boolean active = false;
  @Override
  public void start(CharityMain plugin) {
    lock();
    active = true;
    var run = new BukkitRunnable() {
      int amt = 20;
      @Override
      public void run() {
        amt -= 2;
        if (amt < 2) {
          this.cancel();
          return;
        }
        var onlinePlayers = plugin.getServer().getOnlinePlayers();
        onlinePlayers.forEach(player -> {
          player.playSound(
              Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 0.6f),
              Sound.Emitter.self());
          var a = (Attributable) player;
          var attribute = a.getAttribute(Attribute.MAX_HEALTH);
          player.setHealth((double) amt);
          attribute.setBaseValue((double) amt);
        });
      }
    };
    run.runTaskTimer(plugin, 0L, 20L);
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      this.active = false;
      unlock();
      var onlinePlayers = plugin.getServer().getOnlinePlayers();
      onlinePlayers.forEach(player -> {
        var a = (Attributable) player;
        var attribute = a.getAttribute(Attribute.MAX_HEALTH);
        attribute.setBaseValue(20D);
      });
    }, 20 * 60 * 7); // 7 minutes
  }

  @EventHandler
  public void onLogin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    var a = (Attributable) player;
    var attribute = a.getAttribute(Attribute.MAX_HEALTH);
    if (active) {
      attribute.setBaseValue(2D);
    } else {
      attribute.setBaseValue(20D);
    }
  }
}
