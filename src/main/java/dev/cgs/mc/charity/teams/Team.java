package dev.cgs.mc.charity.teams;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.NpcData;
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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Team implements ConfigurationSerializable, ForwardingAudience {
  static {
    ConfigurationSerialization.registerClass(Team.class);
    ConfigurationSerialization.registerClass(UnlockMeta.class);
    ConfigurationSerialization.registerClass(ObjectiveKey.class);
  }

  @Override
  public @NotNull Iterable<? extends Audience> audiences() {
    return this.onlinePlayers;
  }

  @Override
  public int hashCode() {
    return this.leader.hashCode();
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
      data.put("player", player == null ? "" : player.getUniqueId().toString());
      return data;
    }

    public String key;
    public OfflinePlayer player;

    public ObjectiveKey(Map<String, Object> data) {
      this.key = (String) data.get("key");
      String playerKey = (String) data.get("player");
      if (!playerKey.isEmpty()) {
        this.player = Bukkit.getOfflinePlayer(UUID.fromString(playerKey));
      }
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
      if (other.player == null && player == null)
        return true;
      if (other.player.equals(player))
        return true;
      return false;
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

  public boolean hasPlayer(OfflinePlayer player) {
    return this.players.contains(player);
  }

  public void updateRecruiter(boolean standing) {
    Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(this.leader.toString().toLowerCase());
    if (npc == null)
      return;
    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    Hologram hologram = manager.getHologram(this.leader.toString().toLowerCase()).orElse(null);
    if (hologram != null) {
      HologramData hologramData = hologram.getData();
      if (hologramData instanceof TextHologramData textData) {
        List<String> lines = new ArrayList<>();
        switch (this.leader) {
          case Leader.JAKE:
            lines.add("Team JakeCreates");
            break;
          case Leader.BADCOP:
            lines.add("Team badcop");
            break;
        }
        lines.add("");
        int count = players.size();
        lines.add(String.valueOf(count) + (count == 1 ? " Player" : " Players"));
        textData.setTextShadow(true);
        textData.setText(lines);
        hologram.forceUpdate();
        hologram.queueUpdate();
      }
    }
    NpcData data = npc.getData();
    NpcAttribute pose =
        FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(EntityType.PLAYER, "pose");
    if (standing) {
      data.setOnClick(player -> {
        player.playSound(
            Sound.sound(Key.key("block.note_block.chime"), Sound.Source.MASTER, 1f, 1f));
        player.sendMessage(Component.text("<" + this.leader.toString().toLowerCase() + "> ")
                .append(Component
                        .text("\"Hello, welcome to the server! Would you like to join my team?\" ")
                        .color(NamedTextColor.GOLD))
                .append(Component.text("[Click here to join]")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.callback(audience -> {
                          if (audience instanceof Player p) {
                            this.assign(p);
                          }
                        }))));
      });
      data.addAttribute(pose, "standing");
    } else {
      data.setOnClick(player -> {
        player.playSound(
            Sound.sound(Key.key("block.note_block.didgeridoo"), Sound.Source.MASTER, 1f, 1f));
        player.sendMessage(Component.text("<" + this.leader.toString().toLowerCase() + "> ")
                .append(Component
                        .text("\"My team currently has more players. Are you sure you want to join "
                            + "anyways?\" ")
                        .color(NamedTextColor.RED))
                .append(Component.text("[Click here to join]")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.callback(audience -> {
                          if (audience instanceof Player p) {
                            this.assign(p);
                          }
                        }))));
      });
      data.addAttribute(pose, "sitting");
    }
    npc.updateForAll();
  }

  public void assign(Player player) {
    // idempotence
    if (Teams.get().fromPlayer(player) != null) {
      return;
    }
    player.playSound(
        Sound.sound(Key.key("entity.firework_rocket.launch"), Sound.Source.MASTER, 1f, 1f));
    onlinePlayers.add(player);
    assignOffline(player);
    Teams.get().sendMessage(Component.text(player.getName())
            .append(Component.text(" has joined " + leader.toString().toLowerCase() + "'s team!")
                    .color(NamedTextColor.LIGHT_PURPLE)));
    Location l = Bukkit.getServer().getWorld("world").getSpawnLocation();
    player.teleport(l);
    player.setGameMode(GameMode.SURVIVAL);
  }

  public void assignOffline(OfflinePlayer player) {
    if (Teams.get().fromPlayer(player) != null) {
      return;
    }
    players.add(player);
    Teams.get().saveData();
    Teams.get().updateRecruiters();
  }

  public void onLogin(Player player) {
    onlinePlayers.add(player);
    String worldName = player.getLocation().getWorld().getName();
    if (worldName.equals("team_selection")) {
      Location l = Bukkit.getServer().getWorld("world").getSpawnLocation();
      player.teleport(l);
      player.setGameMode(GameMode.SURVIVAL);
    }
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
