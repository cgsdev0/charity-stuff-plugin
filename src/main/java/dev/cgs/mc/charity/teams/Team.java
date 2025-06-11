package dev.cgs.mc.charity.teams;

import dev.cgs.mc.charity.objectives.Objective;
import dev.cgs.mc.charity.objectives.ObjectiveManager;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

public class Team implements ConfigurationSerializable {
  {
    ConfigurationSerialization.registerClass(Team.class);
    ConfigurationSerialization.registerClass(UnlockMeta.class);
    ConfigurationSerialization.registerClass(ObjectiveKey.class);
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("leader", leader.toString());
    data.put("players", players);
    data.put("objectives", objectives);
    data.put("score", score);
    return data;
  }

  public Team(Map<String, Object> data) {
    this.leader = Team.Leader.valueOf((String) data.get("leader"));
    this.players = (Set<OfflinePlayer>) data.get("players");
    this.objectives = (HashMap<ObjectiveKey, UnlockMeta>) data.get("objectives");
    this.score = (int) data.getOrDefault("score", 0);
    this.onlinePlayers = new HashSet<>();
  }

  public enum Leader { BADCOP, JAKE }

  public class ObjectiveKey implements ConfigurationSerializable {
    @Override
    public Map<String, Object> serialize() {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("key", key);
      data.put("player", player);
      return data;
    }

    public String key;
    public OfflinePlayer player;

    public ObjectiveKey(Map<String, Object> data) {
      this.key = (String) data.get("key");
      this.player = (OfflinePlayer) data.get("player");
    }

    public ObjectiveKey(String key, OfflinePlayer player) {
      this.key = key;
      this.player = player;
    }

    public int hashCode() {
      return (player == null ? 0 : player.hashCode() * 31) + key.hashCode();
    }
  }

  public class UnlockMeta implements ConfigurationSerializable {
    public OfflinePlayer unlockedBy;
    public Date unlockedAt;
    {
    }

    @Override
    public Map<String, Object> serialize() {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("unlockedBy", unlockedBy);
      data.put("unlockedAt", unlockedAt);
      return data;
    }

    public UnlockMeta() {}

    public UnlockMeta(Map<String, Object> data) {
      this.unlockedAt = (Date) data.get("unlockedAt");
      this.unlockedBy = (OfflinePlayer) data.get("unlockedBy");
    }
  }

  private Set<OfflinePlayer> players;
  private Set<Player> onlinePlayers;
  private HashMap<ObjectiveKey, UnlockMeta> objectives;
  private Leader leader;
  private int score;

  public Team(Leader leader) {
    this.leader = leader;
    this.objectives = new HashMap<>();
    this.players = new HashSet<>();
    this.onlinePlayers = new HashSet<>();
    this.score = 0;
  }

  public void unlock(String objective, OfflinePlayer who) {
    Objective.Meta meta = ObjectiveManager.get().getMeta(objective);
    ObjectiveKey key = new ObjectiveKey(objective, who);
    if (!players.contains(who)) {
      throw new AssertionError("Player is not on that team!");
    }
    if (meta.kind() == Objective.Kind.PER_TEAM) {
      key.player = null;
    }
    if (objectives.containsKey(key)) {
      return;
    }
    score += meta.worth();
    UnlockMeta unlock = new UnlockMeta();
    unlock.unlockedBy = who;
    unlock.unlockedAt = new Date();
    objectives.put(key, unlock);
    Bukkit.getLogger().info(who.getName() + " unlocked " + objective + "!");
  }

  public boolean hasPlayer(Player player) {
    return this.players.contains(player);
  }

  public void assign(Player player) {
    players.add(player);
    onlinePlayers.add(player);
  }

  public void onLogin(Player player) {
    onlinePlayers.add(player);
  }

  public void onQuit(Player player) {
    onlinePlayers.remove(player);
  }

  public Set<OfflinePlayer> getPlayers() {
    return players;
  }

  public Set<Player> getOnlinePlayers() {
    return onlinePlayers;
  }

  public Leader getLeader() {
    return leader;
  }
}
