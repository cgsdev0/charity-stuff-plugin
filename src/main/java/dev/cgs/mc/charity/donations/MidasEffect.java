package dev.cgs.mc.charity.donations;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

@DonationEffect.Meta(key = "midas", name = "Midas Touch", tier = Tier.TIER_3)
public class MidasEffect extends DonationEffect {

    public void convertPickaxesToGold(Player player) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null) continue;

            Material fromType = item.getType();
            Material newType = remapMaterial(fromType);
            if (newType == null) continue;

            int oldMax = fromType.getMaxDurability();
            int newMax = newType.getMaxDurability();

            ItemMeta oldMeta = item.getItemMeta();
            short oldDamage = 0;
            if (oldMeta instanceof Damageable damageable && oldMax > 0) {
                oldDamage = (short) damageable.getDamage();
            }

            // Scale the damage proportionally to golden pickaxe durability
            short newDamage = 0;
            if (oldMax > 0 && newMax > 0) {
                double damageRatio = (double) oldDamage / oldMax;
                newDamage = (short) Math.round(damageRatio * newMax);
            }

            // Create new golden pickaxe
            ItemStack replacement = new ItemStack(newType, item.getAmount());
            ItemMeta newMeta = replacement.getItemMeta();

            if (newMeta != null && oldMeta != null) {
                newMeta.setDisplayName(oldMeta.getDisplayName());
                newMeta.setLore(oldMeta.getLore());
                for (Enchantment e : oldMeta.getEnchants().keySet()) {
                  newMeta.addEnchant(e, oldMeta.getEnchants().get(e), true);
                }

                if (newMeta instanceof Damageable newDamageable) {
                    newDamageable.setDamage(newDamage);
                }

                replacement.setItemMeta(newMeta);
            }

            contents[i] = replacement;
        }

        player.getInventory().setContents(contents);
    }

    public Material remapMaterial(Material type) {
        return switch (type) {
            case IRON_PICKAXE, DIAMOND_PICKAXE -> Material.GOLDEN_PICKAXE;
            case IRON_SHOVEL, DIAMOND_SHOVEL -> Material.GOLDEN_SHOVEL;
            case IRON_HOE, DIAMOND_HOE -> Material.GOLDEN_HOE;
            case IRON_AXE, DIAMOND_AXE -> Material.GOLDEN_AXE;
            case IRON_SWORD, DIAMOND_SWORD -> Material.GOLDEN_SWORD;
            case IRON_HELMET, DIAMOND_HELMET -> Material.GOLDEN_HELMET;
            case IRON_BOOTS, DIAMOND_BOOTS -> Material.GOLDEN_BOOTS;
            case IRON_CHESTPLATE, DIAMOND_CHESTPLATE -> Material.GOLDEN_CHESTPLATE;
            case IRON_LEGGINGS, DIAMOND_LEGGINGS -> Material.GOLDEN_LEGGINGS;
            default -> null;
        };
    }

  @Override
  public void start(CharityMain plugin) {
    
    Teams.get().getOnlinePlayers().forEach(player -> {
      convertPickaxesToGold(player);
      player.playSound(Sound.sound(Key.key("block.enchantment_table.use"), Sound.Source.MASTER, 1.0f, 1.0f),
          Sound.Emitter.self());
    });
  }
}
