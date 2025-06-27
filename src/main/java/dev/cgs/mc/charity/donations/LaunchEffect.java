package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DonationEffect.Meta(key = "launch", name = "Lift-off", tier = Tier.TIER_2)
public class LaunchEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    Teams.get().playSound(
        Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f));
    Teams.get().getOnlinePlayers().forEach(player -> {
      player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20, 99));
    });
  }
}
