package dev.cgs.mc.charity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

// TODO: backed by a file
// TODO: list of completed objectives
public class Team {
  public enum Leader {
    BADCOP,
    JAKE
  }

  private Set<OfflinePlayer> players = new HashSet<>();
  private Leader leader;

  public Team(Leader leader) {
    this.leader = leader;
  }

  public void assign(OfflinePlayer player) {
    players.add(player);
  }

  public Set<OfflinePlayer> getPlayers() {
    return players;
  }

  public Leader getLeader() {
    return leader;
  }
}
