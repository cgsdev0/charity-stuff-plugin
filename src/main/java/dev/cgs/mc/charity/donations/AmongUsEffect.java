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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

@DonationEffect.Meta(key = "amongus", name = "Among Us", tier = Tier.TIER_3)
public class AmongUsEffect extends DonationEffect implements Listener {
  public int id = 0;
  private class State {
    public BossBar bossBar;
    public UUID imposter;
    public int killCount = 0;
    public CharityMain plugin;
    public int id;
    public State() {
      bossBar = Bukkit.createBossBar("Imposter", BarColor.RED, BarStyle.SEGMENTED_20);
      bossBar.setProgress(1.0);
      bossBar.removeAll();
    }
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
    team.playSound(Sound.sound(Key.key("custom.amongus"), Sound.Source.MASTER, 1f, 1f));
    team.sendMessage(Component.text("One player on each team has become the imposter. If 3 crewmates die within 3 minutes of each other, the imposter wins. If the imposter dies or takes too long, the crewmates win.").color(NamedTextColor.GOLD));
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
        p.showTitle(
            Title.title(Component.text("Imposter").color(NamedTextColor.RED), Component.text("Betray your allies to win")));
        p.sendMessage(Component.text("You are the imposter! You can wait as long as you like to make the first kill.").color(NamedTextColor.RED));
        state.bossBar.addPlayer(p);
      } else {
        p.showTitle(Title.title(
            Component.text("Crewmate").color(NamedTextColor.AQUA), Component.text("There is 1 imposter on your team")));
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
    state.bossBar.addPlayer(p);
    if (state.killCount == 0) {
      state.bossBar.setTitle("Kill 2 more teammates to win!");
      Bukkit.getScheduler().runTaskTimer(state.plugin, task -> {
        State s = states.get(t.getLeader());
        if (s == null || (s.id != state.id)) {
          task.cancel();
          return;
        }
        double progress = state.bossBar.getProgress() - (1.0/720.0);
        if (progress <= 0.0) {
          task.cancel();
          end(t.getLeader(), s, false);
        } else {
          state.bossBar.setProgress(progress);
        }
      }, 0, 5);
    }
    event.setCancelled(true);
    p.setGameMode(GameMode.SPECTATOR);
    Location l = p.getLocation();
    l.getWorld().playSound(Sound.sound(Key.key("entity.lightning_bolt.impact"), Sound.Source.MASTER, 1f, 1f), p);
    state.killCount++;
    state.bossBar.setTitle("Kill " + (3 - state.killCount) + " more teammate(s) to win!");
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
    Team team = Teams.get().fromLeader(leader);
    if (imposterWins) {
      Player winner = Bukkit.getPlayer(state.imposter);
      winner.give(new ItemStack(Material.TOTEM_OF_UNDYING));
      winner.give(new ItemStack(Material.DIAMOND, 5));
    } else {
      for (Player p : team.getOnlinePlayers()) {
        if (p.getUniqueId().equals(state.imposter)) continue;
        p.give(new ItemStack(Material.DIAMOND, 2));
      }
    }
    // remove spectator mode
    state.bossBar.removeAll();
    if (imposterWins) {
      team.sendMessage(Component.text("Imposter victory!").color(NamedTextColor.RED));
      team.playSound(Sound.sound(Key.key("ui.toast.challenge_complete"), Sound.Source.MASTER, 1.0f, 1.0f));
    } else {
      team.sendMessage(Component.text("Crewmate victory!").color(NamedTextColor.AQUA));
      team.playSound(Sound.sound(Key.key("ui.toast.challenge_complete"), Sound.Source.MASTER, 1.0f, 1.0f));
    }
    for (Player p : team.getOnlinePlayers()) {

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
