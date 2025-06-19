package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataType;

@DonationEffect.Meta(key = "anvil", name = "Raining Anvils!", tier = Tier.TIER_2)
public class AnvilRainEffect extends DonationEffect implements Listener {
  public Set<Location> anvils = new HashSet<>();
  @Override
  public void start(CharityMain plugin) {
    var onlinePlayers = plugin.getServer().getOnlinePlayers();

    onlinePlayers.forEach(player -> {
      Location playerLoc = player.getLocation();
      Location directHit = playerLoc.clone().add(0, 20, 0);

      FallingBlock fallingAnvil =
          (FallingBlock) directHit.getWorld().spawnEntity(directHit, EntityType.FALLING_BLOCK);
      fallingAnvil.setBlockData(Material.ANVIL.createBlockData());
      fallingAnvil.setDamagePerBlock(2);
      fallingAnvil.setMaxDamage(15);
      fallingAnvil.setDropItem(false);
      // our anvil is special
      fallingAnvil.getPersistentDataContainer().set(
          CharityMain.anvilKey, PersistentDataType.BOOLEAN, true);

      for (int i = 0; i < 20; i++) {
        double angle = Math.random() * 2D * Math.PI;
        double distanceFromPlayer = 5D * Math.sqrt(Math.random());
        int height = 20;

        double x = Math.cos(angle) * distanceFromPlayer;
        double z = Math.sin(angle) * distanceFromPlayer;

        Location spawnLoc = playerLoc.clone().add(x, height, z);

        if (spawnLoc.getWorld() != null) {
          FallingBlock otherFallingAnvil =
              (FallingBlock) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.FALLING_BLOCK);
          otherFallingAnvil.setBlockData(Material.ANVIL.createBlockData());
          otherFallingAnvil.setDropItem(false);
          otherFallingAnvil.getPersistentDataContainer().set(
              CharityMain.anvilKey, PersistentDataType.BOOLEAN, true);
        }
      }
    });

    lock();

    Bukkit.getScheduler().runTaskLater(plugin, task -> {
      for (var anvil : anvils) {
        if (Tag.ANVIL.isTagged(anvil.getBlock().getType())) {
          anvil.getWorld().getBlockAt(anvil).setType(Material.AIR);
        } else {
          plugin.getLogger().warning(
              "Something weird happened to the anvil at " + anvil.toString());
        }
      }
      anvils.clear();
      unlock();
    }, 20 * 30); // 30s
  }

  @EventHandler
  public void onLand(EntityChangeBlockEvent event) {
    if (event.getEntity().getType() != EntityType.FALLING_BLOCK)
      return;
    if (event.getEntity().getPersistentDataContainer().getOrDefault(
            CharityMain.anvilKey, PersistentDataType.BOOLEAN, false)) {
      Location loc = event.getBlock().getLocation();
      anvils.add(loc);
    }
  }

  @EventHandler
  public void onBreak(BlockBreakEvent event) {
    Location loc = event.getBlock().getLocation();
    if (anvils.contains(loc)) {
      anvils.remove(loc);
      event.setDropItems(false);
    }
  }

  @EventHandler
  public void onEntitySpawn(EntitySpawnEvent event) {
    if (event.getEntity() instanceof FallingBlock falling) {
      if (falling.getBlockData().getMaterial() == Material.ANVIL) {
        Location loc = falling.getLocation().getBlock().getLocation();

        if (anvils.contains(loc)) {
          anvils.remove(loc);
          falling.getPersistentDataContainer().set(
              CharityMain.anvilKey, PersistentDataType.BOOLEAN, true);
          falling.setDropItem(false);
        }
      }
    }
  }
}
