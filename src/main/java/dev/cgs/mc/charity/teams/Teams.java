package dev.cgs.mc.charity.teams;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcsLoadedEvent;
import dev.cgs.mc.charity.CharityMain;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Teams implements Listener, ForwardingAudience {
  private static Teams instance;
  private File dataFile;

  private List<Team> teams;

  private Teams() {
    teams = new ArrayList<>();
    teams.add(new Team(Team.Leader.JAKE));
    teams.add(new Team(Team.Leader.BADCOP));

    CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
    dataFile = new File(plugin.getDataFolder(), "data.yml");
    if (!dataFile.exists()) {
      try {
        plugin.getDataFolder().mkdirs();
        dataFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public @NotNull Iterable<? extends Audience> audiences() {
    return this.teams;
  }

  public void updateRecruiters() {
    Team a = this.teams.get(0);
    Team b = this.teams.get(1);
    a.updateRecruiter(a.getPlayers().size() <= b.getPlayers().size());
    b.updateRecruiter(b.getPlayers().size() <= a.getPlayers().size());
  }

  public void saveData() {
    if (dataFile == null)
      return;
    YamlConfiguration dataConfig = new YamlConfiguration();
    dataConfig.set("teams", teams);

    try {
      dataConfig.save(dataFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadData() {
    YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    Object config = dataConfig.get("teams");
    if (config == null) {
      return;
    }
    this.teams = (List<Team>) config;
  }

  public Team fromPlayer(OfflinePlayer player) {
    return teams.stream().filter(team -> team.hasPlayer(player)).findFirst().orElse(null);
  }

  public List<String> getKeys() {
    return Stream.of(Team.Leader.values()).map(Enum::name).collect(Collectors.toList());
  }

  public Team fromLeader(Team.Leader leader) {
    return teams.stream().filter(team -> team.getLeader() == leader).findFirst().orElse(null);
  }

  public static void onEnable() {
    if (instance != null) {
      throw new IllegalStateException("Teams is already initialized.");
    }

    instance = new Teams();
    instance.loadData();
    instance.saveData();
    CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
    plugin.getServer().getPluginManager().registerEvents(instance, plugin);
  }

  @EventHandler
  public void onNpcLoad(NpcsLoadedEvent event) {
    instance.updateRecruiters();
  }

  // public void createNPCs() {
  //   CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
  //   plugin.getLogger().info("SPAWNING NPC");
  //   World world = plugin.getServer().getWorld("world");

  //   Location loc = world.getSpawnLocation();
  //   loc.setX(80D);
  //   loc.setY(80D);
  //   loc.setZ(-240D);
  //   NpcData data = new NpcData("badcop", UUID.randomUUID(), loc);
  //   data.setSkin("cutecop");
  //   data.setDisplayName("<red>badcop</red>");
  //   Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
  //   FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
  //   npc.setSaveToFile(false);
  //   npc.create();
  //   npc.spawnForAll();
  // }

  @EventHandler
  public void onLogin(PlayerJoinEvent event) {
    var joiningPlayer = event.getPlayer();
    var team = fromPlayer(joiningPlayer);
    if (team != null) {
      team.onLogin(joiningPlayer);
    } else {
      World world = Bukkit.getServer().getWorld("team_selection");
      joiningPlayer.teleportAsync(world.getSpawnLocation());
      joiningPlayer.setGameMode(GameMode.ADVENTURE);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    var quittingPlayer = event.getPlayer();
    var team = fromPlayer(quittingPlayer);
    if (team != null)
      team.onQuit(quittingPlayer);
  }

  public static Teams get() {
    if (instance == null) {
      throw new IllegalStateException("Teams not initialized yet.");
    }
    return instance;
  }

  public static void onDisable() {
    instance = null;
  }
}
