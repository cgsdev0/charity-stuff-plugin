package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DonationEffect.Meta(key = "inv_shuffle", name = "Slight Mixup", tier = Tier.TIER_1)
public class ShuffleEffect extends DonationEffect {

    public static void shuffleStorageContents(Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] storage = inv.getStorageContents(); // slots 0–35

        // Convert to mutable list, shuffle, and set back
        List<ItemStack> shuffled = Arrays.asList(storage);
        Collections.shuffle(shuffled);
        inv.setStorageContents(shuffled.toArray(new ItemStack[0]));
    }

  @Override
  public void start(CharityMain plugin) {
    
    Teams.get().getOnlinePlayers().forEach(player -> {
      shuffleStorageContents(player);
      player.playSound(Sound.sound(Key.key("item.armor.equip_diamond"), Sound.Source.MASTER, 1.0f, 1.0f),
          Sound.Emitter.self());
    });
  }
}
