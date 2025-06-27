package dev.cgs.mc.charity.donations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

@DonationEffect.Meta(key = "bee_ride", name = "Bzz Bzz", tier = Tier.TIER_1)
public class BeeEffect extends DonationEffect implements Listener {

    public boolean active = false;
    public static void putPlayerOnBee(Player player) {
            Location loc = player.getLocation();
            Bee bee = (Bee) player.getWorld().spawnEntity(loc, EntityType.BEE);
            bee.addPassenger(player);
    }

  @Override
  public void start(CharityMain plugin) {
    lock();
    active = true;
    Teams.get().getOnlinePlayers().forEach(player -> {
      putPlayerOnBee(player);
      player.playSound(Sound.sound(Key.key("block.beehive.exit"), Sound.Source.MASTER, 1.0f, 1.0f),
          Sound.Emitter.self());
    });
      Bukkit.getScheduler().runTaskLater(plugin, task -> {
        active = false;
      unlock();
      Teams.get().getOnlinePlayers().forEach(player -> {
        var e = player.getVehicle();
        if (e != null && e instanceof Bee) {
            player.leaveVehicle();
        }
      });
    }, 20 * 30); // 30s
  }


  @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (active && event.getEntity() instanceof Player && event.getDismounted() instanceof Bee) {
            event.setCancelled(true);
        }
    }
}
