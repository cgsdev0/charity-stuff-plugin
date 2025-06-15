package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

@DonationEffect.Meta(key = "shared_health", name = "Sharing is Caring", tier = Tier.TIER_3)
public class SharedHealthbarEffect extends DonationEffect implements Listener {
  public boolean active = false;

  private class State {
    public boolean locked = false;
  }

  public Map<Team, State> states = new HashMap<>();

  @Override
  public void start(CharityMain plugin) {
    lock();
    startForTeam(plugin, Teams.get().fromLeader(Team.Leader.JAKE));
    startForTeam(plugin, Teams.get().fromLeader(Team.Leader.BADCOP));
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      states.clear();
      unlock();
    }, 20 * 60 * 10); // 10 minutes
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player player))
      return;

    double damage = event.getFinalDamage();

    Team team = Teams.get().fromPlayer(player);
    if (team == null)
      return;
    State state = states.get(team);
    if (state == null)
      return;

    if (state.locked)
      return;

    state.locked = true;
    team.getOnlinePlayers().forEach(p -> {
      if (p.equals(player))
        return;
      p.damage(damage);
    });
    state.locked = false;
  }

  @EventHandler
  public void onEntityRegainHealth(EntityRegainHealthEvent event) {
    if (!(event.getEntity() instanceof Player player))
      return;

    double amount = event.getAmount();
    double expected = player.getHealth() + amount;
    Team team = Teams.get().fromPlayer(player);
    if (team == null)
      return;
    State state = states.get(team);
    if (state == null)
      return;

    if (state.locked)
      return;

    state.locked = true;
    team.getOnlinePlayers().forEach(p -> {
      if (p.equals(player))
        return;
      p.setHealth(expected);
    });
    state.locked = false;
  }

  public void startForTeam(CharityMain plugin, Team team) {
    State s = new State();
    states.put(team, s);
    var min = team.getOnlinePlayers().stream().reduce(
        (p1, p2) -> p1.getHealth() < p2.getHealth() ? p1 : p2);
    if (min.isPresent()) {
      team.getOnlinePlayers().forEach(player -> { player.setHealth(min.get().getHealth()); });
    }
  }
}
