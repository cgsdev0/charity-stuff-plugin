package dev.cgs.mc.charity.teams;

import dev.cgs.mc.charity.CharityMain;
import io.papermc.paper.configuration.Configuration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.lucko.spark.paper.common.util.config.FileConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Teams implements Listener {
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

  public Team fromPlayer(Player player) {
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
  public void onLogin(PlayerLoginEvent event) {
    var joiningPlayer = event.getPlayer();
    var team = fromPlayer(joiningPlayer);
    if (team != null)
      team.onLogin(joiningPlayer);
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
