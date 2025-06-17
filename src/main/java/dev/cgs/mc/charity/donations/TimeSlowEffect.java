package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.VoicePlugin;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import org.bukkit.scheduler.BukkitRunnable;

@DonationEffect.Meta(key = "time_slow", name = "Time Dilation", tier = Tier.TIER_2, mutex = "scale")
public class TimeSlowEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    lock();
    var run = new BukkitRunnable() {
      // TODO: custom sounds for this one
      int tick = 0;
      float rate = 20.0f;

      int fade = 19;
      int duration = 120;
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
