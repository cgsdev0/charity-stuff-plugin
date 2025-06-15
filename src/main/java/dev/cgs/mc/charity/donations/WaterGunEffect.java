package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

@DonationEffect.Meta(key = "water-guns", name = "Cool Off", tier = Tier.TIER_1)
public class WaterGunEffect extends DonationEffect implements Listener {
  @Override
  public void start(CharityMain plugin) {
    plugin.getServer().getOnlinePlayers().forEach(player -> { giveWaterGun(player); });
  }

  public static NamespacedKey waterGunKey;
  public static NamespacedKey mysticArrowKey;

  public WaterGunEffect() {
    CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
    waterGunKey = new NamespacedKey(plugin, "water_gun");
    mysticArrowKey = new NamespacedKey(plugin, "mystic_arrow");
  }

  private boolean isWaterGun(ItemStack stack) {
    if (stack == null)
      return false;
    if (stack.getPersistentDataContainer().getOrDefault(
            waterGunKey, PersistentDataType.BOOLEAN, false)) {
      return true;
    }
    return false;
  }

  public void giveWaterGun(Player player) {
    ItemStack gun = new ItemStack(Material.BOW);
    ItemMeta meta = gun.getItemMeta();
    int idx = ThreadLocalRandom.current().nextInt(3) + 1;
    meta.setItemModel(new NamespacedKey("custom", "water_gun" + String.valueOf(idx)));
    meta.customName(Component.text("Water Gun"));
    ((Repairable) meta).setRepairCost(50);
    ((Damageable) meta).setMaxDamage(10);
    gun.setItemMeta(meta);
    gun.editPersistentDataContainer(
        pdc -> { pdc.set(waterGunKey, PersistentDataType.BOOLEAN, true); });
    player.give(gun);
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      ItemStack item = e.getPlayer().getInventory().getItem(e.getHand());
      if (!isWaterGun(item)) {
        return;
      }
      // CraftPlayer cp = (CraftPlayer) p;
      // ServerPlayer nmsPlayer = cp.getHandle();
      // fake an arrow
      // nmsPlayer.connection.send(new ClientboundContainerSetSlotPacket(0, 0, 9, // slot
      //     new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW)));
      ItemStack realItem = p.getInventory().getItem(9);
      ItemStack mysticArrow = new ItemStack(Material.ARROW);
      mysticArrow.editPersistentDataContainer(
          consumer -> { consumer.set(mysticArrowKey, PersistentDataType.BOOLEAN, true); });
      p.getInventory().setItem(9, mysticArrow);

      CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
      var runnable = new BukkitRunnable() {
        int ticks = 0;
        @Override
        public void run() {
          try {
            if (ticks == 0 && mysticArrow.equals(p.getInventory().getItem(9))) {
              p.getInventory().setItem(9, realItem);
            }
            if (!p.isHandRaised()) {
              this.cancel();
              return;
            } else {
              if (ticks % 20 == 0) {
                ItemStack damaged = item.damage(1, p);
                if (damaged.isEmpty()) {
                  this.cancel();
                  return;
                }
              }
              spawnWaterBeam(p);
            }
          } catch (Exception ex) {
            // lol
            this.cancel();
          }
          ticks++;
        }
      };
      runnable.runTaskTimer(plugin, 6L, 2L);
    }
  }

  private void spawnWaterBeam(Player player) {
    Location base = player.getEyeLocation().clone();
    Vector direction = base.getDirection().normalize();
    base = base.add(new Vector(0.1, -0.1, 0.0)).add(direction.clone().multiply(0.8));

    double knockbackStrength = 0.7;
    double hitRadius = 1.0;
    for (int i = 0; i < 25; i++) {
      Location point = base.clone().add(direction.clone().multiply(i * 0.25));
      player.getWorld().spawnParticle(Particle.RAIN, point, 1, 0, 0, 0, 0);
      for (Entity entity :
          point.getWorld().getNearbyEntities(point, hitRadius, hitRadius, hitRadius)) {
        if (entity instanceof LivingEntity && entity != player) {
          Vector away =
              entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
          away.setY(0.2);
          entity.setVelocity(away.multiply(knockbackStrength));
        }
      }
    }
  }

  @EventHandler
  public void onShoot(EntityShootBowEvent event) {
    if (event.getEntity() instanceof Player) {
      ItemStack item = event.getBow();
      if (!isWaterGun(item)) {
        return;
      }
      event.setCancelled(true);
    }
  }
}
