package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.VoicePlugin;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@DonationEffect.Meta(key = "time_slow", name = "Time Dilation", tier = Tier.TIER_2, mutex = "scale")
public class TimeSlowEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    lock();
    Bukkit.getServer().playSound(Sound.sound(Key.key("custom.slowdown"), Sound.Source.MASTER, 1f, 1f));
    var run = new BukkitRunnable() {
      // TODO: custom sounds for this one
      int tick = 0;
      float rate = 20.0f;

      int fade = 19;
      int duration = 90;
      @Override
      public void run() {
        if (tick < fade) {
          rate = 20.0f - tick;
        }
        if (tick > duration && tick < duration + fade) {
          rate = (float) (tick + 1 - duration);
        }
        plugin.getServer().getServerTickManager().setTickRate(rate);

        if (tick >= duration + fade) {
          rate = 20.0f;
          Bukkit.getServer().playSound(Sound.sound(Key.key("custom.speedup"), Sound.Source.MASTER, 1f, 1f));
          this.cancel();
          unlock();
        }
        plugin.getServer().getOnlinePlayers().forEach(player -> {
          float ratio = (20f - rate) / 19f;
          float pitch = 1f - ratio * 0.4f;
          VoicePlugin.setPitchScale(player.getUniqueId(), pitch);
        });
        tick++;
      }
    };
    run.runTaskTimer(plugin, 0L, 1L);
  }
}
