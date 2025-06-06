package dev.cgs.mc.charity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

// TODO: backed by a file
// TODO: list of completed objectives
public class Team {
  public enum Leader {
    BADCOP,
    JAKE
  }

  private List<Player> players = new ArrayList<>();
  private Leader leader;

  public Team(Leader leader) {
    this.leader = leader;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public Leader getLeader() {
    return leader;
  }
}
