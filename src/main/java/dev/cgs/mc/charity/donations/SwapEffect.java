package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@DonationEffect.Meta(
  key="swap",
  name="Swap Position",
  tier=Tier.TIER_1
)
public class SwapEffect extends DonationEffect implements Listener {
  @Override
  public void start() {
    Team a = Teams.get().fromLeader(Team.Leader.BADCOP);
    Team b = Teams.get().fromLeader(Team.Leader.JAKE);

    ArrayList<Player> listA = new ArrayList<>(a.getOnlinePlayers());
    ArrayList<Player> listB = new ArrayList<>(b.getOnlinePlayers());

    Player randomPlayerA = listA.get(ThreadLocalRandom.current().nextInt(listA.size()));
    Player randomPlayerB = listB.get(ThreadLocalRandom.current().nextInt(listB.size()));

    Location locA = randomPlayerA.getLocation();
    Location locB = randomPlayerB.getLocation();

    randomPlayerA.teleport(locB);
    randomPlayerB.teleport(locA);
  }
}
