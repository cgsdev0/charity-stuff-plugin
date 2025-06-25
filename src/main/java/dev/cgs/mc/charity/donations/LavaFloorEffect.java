package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@DonationEffect.Meta(key = "floor-lava", name = "The Floor is Lava", tier = Tier.TIER_2)
public class LavaFloorEffect extends DonationEffect implements Listener {
  public boolean active = false;
  public Map<Location, Material> blocks = new HashMap<>();
  @Override
  public void start(CharityMain plugin) {
    lock();
    active = true;
    Teams.get().playSound(
        Sound.sound(Key.key("entity.ghast.warn"), Sound.Source.MASTER, 1.0f, 1.0f),
        Sound.Emitter.self());
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      restore();
      this.active = false;
      unlock();
    }, 20 * 60 * 3); // 3 minutes
  }

  public void restore() {
    blocks.forEach((key, value) -> { key.getBlock().setType(value); });
    blocks.clear();
  }

  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    if (!active) {
      return;
    }
    Block b = e.getPlayer().getLocation().add(0, -1, 0).getBlock();
    Material m = b.getType();
    if (m == Material.DIRT || m == Material.GRASS_BLOCK || m == Material.STONE
        || m == Material.NETHERRACK || m == Material.END_STONE) {
      b.setType(Material.MAGMA_BLOCK);
      blocks.put(b.getLocation(), m);
    }
  }
}
