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

@DonationEffect.Meta(key = "team-meeting", name = "Team Meeting", tier = Tier.TIER_2)
public class TeamMeetingEffect extends DonationEffect {
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

    Teams.get().playSound(
        Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f));

    a.getOnlinePlayers().forEach(player -> {
      player.teleport(randomPlayerA.getLocation());
    });
    b.getOnlinePlayers().forEach(player -> {
      player.teleport(randomPlayerB.getLocation());
    });
  }
}
