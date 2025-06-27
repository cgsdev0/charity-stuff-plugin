package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DonationEffect.Meta(key = "drinks", name = "Happy Hour", tier = Tier.TIER_1)
public class DrinksEffect extends DonationEffect {

  private static String[] options = {"kiwi_juice", "orange_juice", "strawberry_juice", "coconut_drink"};
  private static PotionEffectType[] effects = { PotionEffectType.ABSORPTION, PotionEffectType.BLINDNESS, PotionEffectType.POISON, PotionEffectType.DOLPHINS_GRACE, PotionEffectType.GLOWING, PotionEffectType.JUMP_BOOST, PotionEffectType.LEVITATION, PotionEffectType.INVISIBILITY, PotionEffectType.HASTE, PotionEffectType.MINING_FATIGUE, PotionEffectType.NIGHT_VISION};
  @Override
  public void start(CharityMain plugin) {
    
    Teams.get().getOnlinePlayers().forEach(player -> {
      ItemStack potion = ItemStack.of(Material.POTION);
      potion.editMeta(meta -> {
        int idx = ThreadLocalRandom.current().nextInt(4);
        meta.setItemModel(new NamespacedKey("summer_furnitures", options[idx]));
        meta.customName(Component.text("Cocktail")
            .decoration(TextDecoration.ITALIC, false)
            .color(NamedTextColor.GREEN));
        if (meta instanceof PotionMeta pm) {
           int idx2 = ThreadLocalRandom.current().nextInt(effects.length);
          pm.addCustomEffect(new PotionEffect(effects[idx2], 30 * 20, 0), true);
          pm.addCustomEffect(new PotionEffect(PotionEffectType.NAUSEA, 6 * 20, 0), true);
        }
      });
      player.give(potion);
      player.playSound(Sound.sound(Key.key("entity.witch.drink"), Sound.Source.MASTER, 1.0f, 1.0f),
          Sound.Emitter.self());
    });
  }
}
