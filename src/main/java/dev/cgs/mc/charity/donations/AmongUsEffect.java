package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@DonationEffect.Meta(key = "amongus", name = "Among Us", tier = Tier.TIER_3)
public class AmongUsEffect extends DonationEffect implements Listener {
  public int id = 0;
  private class State {
    public UUID imposter;
    public int killCount = 0;
    public CharityMain plugin;
    public int id;
  }

  public Map<Team.Leader, State> states = new HashMap<>();

  @Override
  public void start(CharityMain plugin) {
    lock();
    id++;
    startForTeam(Team.Leader.BADCOP, plugin);
    startForTeam(Team.Leader.JAKE, plugin);
  }

  public void startForTeam(Team.Leader leader, CharityMain plugin) {
    State state = new State();
    Team team = Teams.get().fromLeader(leader);
    ArrayList<Player> list = new ArrayList<>(team.getOnlinePlayers());
    if (list.isEmpty())
      return;
    int idx = ThreadLocalRandom.current().nextInt(list.size());
    state.imposter = list.get(idx).getUniqueId();
    state.plugin = plugin;
    state.id = this.id;
    states.put(leader, state);
    for (int i = 0; i < list.size(); i++) {
      Player p = list.get(i);
      if (i == idx) {
        p.showTitle(Title.title(Component.text("imposter"), Component.text("sus")));
      } else {
        p.showTitle(Title.title(Component.text("crewmate"), Component.text("survive")));
      }
    }
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    Player p = event.getPlayer();
    Team t = Teams.get().fromPlayer(p);
    if (t == null)
      return;
    if (!states.containsKey(t.getLeader()))
      return;
    State state = states.get(t.getLeader());
    if (p.getUniqueId() == state.imposter) {
      end(t.getLeader(), state, false);
      return;
    }
    if (state.killCount == 0) {
      Bukkit.getScheduler().runTaskLater(state.plugin, task -> {
        State s = states.get(t.getLeader());
        if (s == null)
          return;
        if (s.id != state.id)
          return;
        end(t.getLeader(), s, false);
      }, 5 * 60 * 20);
    }
    event.setCancelled(true);
    p.setGameMode(GameMode.SPECTATOR);
    state.killCount++;
    if (state.killCount >= 3) {
      end(t.getLeader(), state, true);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player p = event.getPlayer();
    if (p.getGameMode() == GameMode.SPECTATOR) {
      p.setGameMode(GameMode.SURVIVAL);
      Location l = p.getRespawnLocation();
      if (l != null) {
        p.teleport(l);
      } else {
        l = Bukkit.getServer().getWorld("world").getSpawnLocation();
        p.teleport(l);
        p.setHealth(20.0);
      }
    }
  }

  public void end(Team.Leader leader, State state, boolean imposterWins) {
    // TODO: distribute rewards

    // remove spectator mode
    for (Player p : Teams.get().fromLeader(leader).getOnlinePlayers()) {
      if (imposterWins) {
        p.sendMessage("Imposter win!");
      } else {
        p.sendMessage("Crewmates win!");
      }
      if (p.getGameMode() == GameMode.SPECTATOR) {
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20.0);
        Location l = p.getRespawnLocation();
        if (l != null) {
          p.teleport(l);
        } else {
          l = state.plugin.getServer().getWorld("world").getSpawnLocation();
          p.teleport(l);
        }
      }
    }
    states.remove(leader);
    if (states.isEmpty()) {
      unlock();
    }
  }
}
