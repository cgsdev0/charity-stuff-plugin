package dev.cgs.mc.charity.teams;

import dev.cgs.mc.charity.objectives.Objective;
import dev.cgs.mc.charity.objectives.Objectives;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

public class Team implements ConfigurationSerializable {
  static {
    ConfigurationSerialization.registerClass(Team.class);
    ConfigurationSerialization.registerClass(UnlockMeta.class);
    ConfigurationSerialization.registerClass(ObjectiveKey.class);
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("leader", leader.toString());
    data.put("players", players.stream().map(player -> player.getUniqueId().toString()).toList());
    data.put("score", score);
    List<Map<String, Object>> serializedObjectives = new ArrayList<>();
    for (Map.Entry<ObjectiveKey, UnlockMeta> entry : objectives.entrySet()) {
      Map<String, Object> obj = new HashMap<>();
      obj.put("key", entry.getKey().serialize());
      obj.put("value", entry.getValue().serialize());
      serializedObjectives.add(obj);
    }
    data.put("objectives", serializedObjectives);
    return data;
  }

  public Team(Map<String, Object> data) {
    this.leader = Team.Leader.valueOf((String) data.get("leader"));
    var players = (List<String>) data.get("players");
    this.players = new HashSet<>();
    for (var p : players) {
      this.players.add(Bukkit.getOfflinePlayer(UUID.fromString(p)));
    }
    this.objectives = new HashMap<>();
    List<Map<String, Object>> serializedObjectives =
        (List<Map<String, Object>>) data.get("objectives");

    if (serializedObjectives != null) {
      for (Map<String, Object> obj : serializedObjectives) {
        Map<String, Object> keyData = (Map<String, Object>) obj.get("key");
        Map<String, Object> valueData = (Map<String, Object>) obj.get("value");
        ObjectiveKey key = new ObjectiveKey(keyData);
        UnlockMeta value = new UnlockMeta(valueData);
        objectives.put(key, value);
      }
    }
    this.score = (int) data.getOrDefault("score", 0);
    this.onlinePlayers = new HashSet<>();
  }

  public enum Leader { BADCOP, JAKE }

  public class ObjectiveKey implements ConfigurationSerializable {
    @Override
    public Map<String, Object> serialize() {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("key", key);
      data.put("player", player.getUniqueId().toString());
      return data;
    }

    public String key;
    public OfflinePlayer player;

    public ObjectiveKey(Map<String, Object> data) {
      this.key = (String) data.get("key");
      this.player = Bukkit.getOfflinePlayer(UUID.fromString((String) data.get("player")));
    }

    public ObjectiveKey(String key, OfflinePlayer player) {
      this.key = key;
      this.player = player;
    }
    @Override
    public boolean equals(Object obj) {
      if (obj == null)
        return false;
      if (obj.getClass() != this.getClass()) {
        return false;
      }
      final ObjectiveKey other = (ObjectiveKey) obj;
      if (!other.key.equals(key))
        return false;
      if (!(other.player == null && player == null) && !other.player.equals(player))
        return false;
      return true;
    }

    public int hashCode() {
      return (player == null ? 0 : player.hashCode() * 31) + key.hashCode();
    }
  }

  public class UnlockMeta implements ConfigurationSerializable {
    public UUID unlockedBy;
    public Date unlockedAt;

    @Override
    public Map<String, Object> serialize() {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("unlockedBy", unlockedBy.toString());
      data.put("unlockedAt", unlockedAt);
      return data;
    }

    public UnlockMeta() {}

    public UnlockMeta(Map<String, Object> data) {
      this.unlockedAt = (Date) data.get("unlockedAt");
      this.unlockedBy = UUID.fromString((String) data.get("unlockedBy"));
    }
  }

  private Set<OfflinePlayer> players;
  private Set<Player> onlinePlayers;
  private Map<ObjectiveKey, UnlockMeta> objectives;
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
    Objective.Meta meta = Objectives.get().getMeta(objective);
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
    unlock.unlockedBy = who.getUniqueId();
    unlock.unlockedAt = new Date();
    objectives.put(key, unlock);
    Teams.get().saveData();
    Bukkit.getLogger().info(who.getName() + " unlocked " + objective + "!");
  }

  public boolean hasPlayer(Player player) {
    return this.players.contains(player);
  }

  public void assign(Player player) {
    players.add((OfflinePlayer) player);
    onlinePlayers.add(player);
    Teams.get().saveData();
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
