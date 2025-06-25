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

@DonationEffect.Meta(key = "swap", name = "Player Swap", tier = Tier.TIER_1)
public class SwapEffect extends DonationEffect {
  @Override
  public void start(CharityMain plugin) {
    Team a = Teams.get().fromLeader(Team.Leader.BADCOP);
    Team b = Teams.get().fromLeader(Team.Leader.JAKE);

    ArrayList<Player> listA = new ArrayList<>(a.getOnlinePlayers());
    ArrayList<Player> listB = new ArrayList<>(b.getOnlinePlayers());

    if (listA.isEmpty() || listB.isEmpty())
      return;

    Player randomPlayerA = listA.get(ThreadLocalRandom.current().nextInt(listA.size()));
    Player randomPlayerB = listB.get(ThreadLocalRandom.current().nextInt(listB.size()));

    Location locA = randomPlayerA.getLocation();
    Location locB = randomPlayerB.getLocation();

    randomPlayerA.teleport(locB);
    randomPlayerB.teleport(locA);
    Teams.get().playSound(
        Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f),
        locA.x(), locA.y(), locA.z());
    Teams.get().playSound(
        Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f),
        locB.x(), locB.y(), locB.z());
  }
}
