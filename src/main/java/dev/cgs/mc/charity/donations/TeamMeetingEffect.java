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

  public void meeting(Team team) {
    ArrayList<Player> list = new ArrayList<>(team.getOnlinePlayers());

    if (list.isEmpty())
      return;

    Player randomPlayer = list.get(ThreadLocalRandom.current().nextInt(list.size()));
    team.playSound(
        Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f));

    team.getOnlinePlayers().forEach(player -> {
      player.teleport(randomPlayer.getLocation());
    });
  }

  @Override
  public void start(CharityMain plugin) {
    Team a = Teams.get().fromLeader(Team.Leader.BADCOP);
    Team b = Teams.get().fromLeader(Team.Leader.JAKE);

    meeting(a);
    meeting(b);
  }
}
