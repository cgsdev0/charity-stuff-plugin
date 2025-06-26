package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@DonationEffect.Meta(key = "parkour", name = "Mandatory Parkour", tier = Tier.TIER_3)
public class ParkourEffect extends DonationEffect implements Listener {
  public boolean active = false;

  private class Storage {
    public ItemStack[] items;
    public Location location;
    public Location checkpoint;
  }
  public Map<UUID, Storage> invs = new HashMap<>();
  @Override
  public void start(CharityMain plugin) {
    lock();
    active = true;
    Teams.get().getOnlinePlayers().forEach(player -> { warp(player); });
  }

  public void restore(Player player) {
    if (invs.containsKey(player.getUniqueId())) {
      Storage s = invs.remove(player.getUniqueId());
      var inventory = player.getInventory();
      inventory.setContents(s.items);
      player.teleport(s.location);
      player.setGameMode(GameMode.SURVIVAL);
      player.playSound(
          Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f),
          Sound.Emitter.self());
      player.setFallDistance(0.f);
    }
  }

  public void warp(Player player) {
    if (!player.getWorld().getName().equals("parkour")) {
      Storage s = new Storage();
      s.items = player.getInventory().getContents().clone();
      s.location = player.getLocation();
      invs.put(player.getUniqueId(), s);
      player.getInventory().clear();
      player.setGameMode(GameMode.ADVENTURE);
      player.setFoodLevel(20);
    }
    Storage s = invs.get(player.getUniqueId());
    if (s.checkpoint == null) {
      var parkour = Bukkit.getWorld("parkour");
      player.teleportAsync(parkour.getSpawnLocation().setDirection(new Vector(1f, 0f, 0f)));
    } else {
      player.teleportAsync(s.checkpoint);
    }
    player.playSound(
        Sound.sound(Key.key("item.chorus_fruit.teleport"), Sound.Source.MASTER, 1.0f, 1.0f),
        Sound.Emitter.self());
  }

  @EventHandler
  public void onLogin(PlayerJoinEvent event) {
    if (!active)
      return;
    var team = Teams.get().fromPlayer(event.getPlayer());
    if (team == null)
      return;
    warp(event.getPlayer());
  }

  @EventHandler
  public void onLogout(PlayerQuitEvent event) {
    var player = event.getPlayer();
    restore(player);
  }

  @EventHandler
  public void onFall(PlayerMoveEvent ev) {
    if (!active)
      return;
    if (ev.getTo().blockY() < -20) {
      warp(ev.getPlayer());
    }
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (!active)
      return;
    if (event.getEntity() instanceof Player) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPressure(PlayerInteractEvent ev) {
    if (!active)
      return;
    if (!ev.getAction().equals(Action.PHYSICAL)) {
      return;
    }
    if (ev.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
      // win condition
      active = false;
      Player winner = ev.getPlayer();
      winner.playSound(
          Sound.sound(Key.key("ui.toast.challenge_complete"), Sound.Source.MASTER, 1.0f, 1.0f));
      Team team = Teams.get().fromPlayer(winner);
      if (team != null) {
        team.award(50, winner, "Parkour Challenge");
        team.showTitle(Title.title(Component.text("Parkour Completed").color(NamedTextColor.GREEN),
            Component.text(winner.getName() + " has won the race!")));
        team.opposite().showTitle(
            Title.title(Component.text("Parkour Failed").color(NamedTextColor.RED),
                Component.text(winner.getName() + " has won the race!")));
      }
      new ArrayList<>(invs.keySet()).forEach(key -> {
        Player p = Bukkit.getPlayer(key);
        if (p == null)
          return;
        if (!p.equals(winner)) {
          p.playSound(
              Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1.0f, 1.0f));
        }
        restore(p);
      });
      unlock();
    }
    if (ev.getClickedBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
      // checkpoint
      Storage s = invs.get(ev.getPlayer().getUniqueId());
      if (s != null) {
        s.checkpoint = ev.getPlayer().getLocation().setDirection(new Vector(1f, 0f, 0f));
      }
    }
  }
}
